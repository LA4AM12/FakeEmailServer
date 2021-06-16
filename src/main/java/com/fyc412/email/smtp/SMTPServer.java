package com.fyc412.email.smtp;

import com.fyc412.email.mailbox.Mail;
import com.fyc412.email.mailbox.Mailbox;
import com.fyc412.email.mailbox.Mailboxes;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class SMTPServer {
    //默认端口号
    public static final int DEFAULT_SMTP_PORT = 25;

    //CRLF  空行
    private static final Pattern CRLF = Pattern.compile("\r\n");

    //所有收到的mail
    private static final List<Mail> receivedMail = new ArrayList<>();

    //一个server-socket
    private final ServerSocket serverSocket;

    //标志线程停止工作的变量  volatile保证从内存中读到的是最新值
    private volatile boolean stopped = false;

    //模拟本机的域名
    private static final String HOST = "smtp.fyc.com";

    /**
     * 创建一个运行的SMTPServer
     *
     * @param port port number the server should listen to
     * @return 一个server引用
     * @throws IOException 可能抛出异常
     */
    public static SMTPServer start(int port) throws IOException {
        log.info("Starting SMTP server...");
        return new SMTPServer(new ServerSocket(Math.max(port, 0)));
    }

    /**
     * 私有构造函数，这是一个工厂类
     *
     * @param serverSocket socket to listen on
     */
    private SMTPServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        new Thread(this::server).start();
    }

    //服务器运行线程，一直等待客户端连接
    private void server() {
        while (!stopped) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(() -> worker(socket)).start();
                log.info("new client connected！");
            } catch (IOException e) {
                if (!stopped) {
                    log.error("hit exception when running server", e);
                    try {
                        serverSocket.close();
                    } catch (IOException ex) {
                        log.error("and one when closing the port", ex);
                    } finally {
                        stopped = true;
                    }
                }
            }
        }
    }

    /**
     * 一个连接的工作线程
     *
     * @param socket a connection
     */
    private void worker(Socket socket) {
        try (Scanner in = new Scanner(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)).useDelimiter(CRLF);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            List<Mail> mails = handleTransaction(out, in);
            //在更新邮件列表的时候锁住，防止读写出错
            if (mails.size() > 0) {
                synchronized (receivedMail) {
                    for (Mail m : mails) {
                        String targetHost = getAddress(m.getTo());
                        if (targetHost.equals(HOST)) {
                            receivedMail.add(m);   // 确定是发送给本机的邮件，存入数据库，这里使用一个列表模拟
                            //如果存在mailbox则同步到mailbox里面
                            String boxUser = m.getTo().replace("<","").replace(">","");
                            Mailbox mailbox = Mailboxes.getInstance().getMailbox(boxUser);

                            if (mailbox != null) {
                                mailbox.addMail(m);
                            }
                        } else {
                            //当该服务器收到的邮件目的地不是本机的时候，发送该邮件到达目的地
                            //send(targetHost, m);
                            //由于没有一些安全协议   连接不上真正的smtp服务器
                            //这里通过我的163邮箱代发
                            send("smtp.163.com", m);
                        }
                    }
                }
            }
        } catch (
                IOException e) {
            log.error("读写异常！");
        } catch (
                NoSuchElementException e2) {
            log.error("客户端异常退出！");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("terminate connection error");
            }
        }

    }

    /**
     * 处理一次SMTP事务，从连接开始到客户端输入QUIT命令之间的所有活动
     *
     * @param out output stream
     * @param in  input stream
     * @return List of SmtpMessage
     * @throws IOException
     */
    private static List<Mail> handleTransaction(PrintWriter out, Iterator<String> in) throws NoSuchElementException {
        //初始化SMTP-state
        SMTPState smtpState = SMTPState.CONNECT;
        SMTPRequest smtpRequest = new SMTPRequest(SMTPAction.CONNECT, smtpState, "");

        //执行连接请求
        SMTPResponse smtpResponse = smtpRequest.execute();

        //send initial response
        sendResponse(out, smtpResponse);

        smtpState = smtpResponse.getNextState();
        List<Mail> mails = new ArrayList<>();
        Mail mail = new Mail();

        //当客户端输入QUIT命令后会进入CONNECT状态
        while (smtpState != SMTPState.CONNECT && in != null) {
            String line = in.next();

            if (line == null) {
                break;
            }

            //根据客户端的一行来生成一个请求
            SMTPRequest request = SMTPRequest.createRequest(line, smtpState);
            //执行请求
            SMTPResponse response = request.execute();

            //Store input in message
            String params = request.getParams();
            mail.store(smtpState, params);

            //转移到下一个状态
            smtpState = response.getNextState();
            //send to client
            sendResponse(out, response);

            //data-end 一封邮件以及存完
            if (smtpState == SMTPState.QUIT) {
                mails.add(mail);
                mail = new Mail();
            }
        }
        return mails;
    }

    /**
     * Send response to client.
     *
     * @param out          socket output stream
     * @param smtpResponse response object
     */
    private static void sendResponse(PrintWriter out, SMTPResponse smtpResponse) {
        if (smtpResponse.getCode() > 0) {
            int code = smtpResponse.getCode();
            String message = smtpResponse.getMessage();
            out.print(code + " " + message + "\r\n");
            out.flush();
        }
    }

    public static List<Mail> getReceivedEmails() {
        synchronized (receivedMail) {
            return Collections.unmodifiableList(receivedMail);
        }
    }

    //清空
    public void reset() {
        synchronized (receivedMail) {
            receivedMail.clear();
        }
    }

    //提取邮箱的后缀
    private String getAddress(String dest) {
        String substring = dest.substring(dest.lastIndexOf('@') + 1).replace(">", "");
        return "smtp." + substring;
    }


    //socket发送邮件   由于要实现一些安全性的协议才能发给其他的smtp服务器
    //所以这里借助我自己的163邮箱来进行转发
    private void send(String host, Mail mail) {
        try (Socket socket = new Socket(host, DEFAULT_SMTP_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        ) {
            reader.readLine();
            out.println("HELO xxx");
            reader.readLine();
            out.println("auth login");
            reader.readLine();
            out.println("eGluZ3hpdTQxMkAxNjMuY29t");  //username
            reader.readLine();
            out.println("VVRYR0VKRlFUQ0pOREFIUQ==");  //password
            reader.readLine();
            out.println("mail from:<xingxiu412@163.com>");
            reader.readLine();
            out.println("rcpt to:" + mail.getTo());
            reader.readLine();
            out.println("data");
            reader.readLine();
            Map<String, List<String>> headers = mail.getHeaders();
            headers.forEach((k, v) -> {
                v.forEach(item -> {
                    out.println(k + ":" + item);
                });
            });
            //空行
            out.println();
            out.println(mail.getBody().toString());
            out.println(".");   //正文结束
            reader.readLine();
            out.println("QUIT");
            reader.readLine();
            log.info("邮件发送成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("无法连接到目标主机");
        }
    }
}
