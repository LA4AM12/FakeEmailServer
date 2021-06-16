package com.fyc412.email.pop3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Slf4j
public class POP3Server {
    public static final int DEFAULT_POP3_PORT = 110;

    private final ServerSocket serverSocket;

    private volatile boolean stopped = false;

    /**
     * 创建一个运行的POP3Server
     *
     * @param port port number the server should listen to
     * @return 一个server引用
     * @throws IOException 可能抛出异常
     */
    public static POP3Server start(int port) throws IOException {
        log.info("Starting POP3 server...");
        return new POP3Server(new ServerSocket(Math.max(port, 0)));
    }

    /**
     * @param serverSocket socket to listen on
     */
    private POP3Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        new Thread(this::server).start();
    }

    //服务器运行线程，一直等待客户端连接
    private void server(){
        while (!stopped){
            try{
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
                    }finally {
                        stopped = true;
                    }
                }
            }
        }
    }

    private void worker(Socket socket){
        try(Scanner in = new Scanner(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8))) {

            //send ready signal
            sendResponse(out,new SingleLineResponse("LA4AM12 POP3 Server ready",true));

            //处理这次连接中的命令
            handleTransaction(in,out);
        } catch (NoSuchElementException e) {
            log.error("Connection lost");
        } catch (IOException e){
            e.printStackTrace();
        } finally{
            try {
                socket.close();
            } catch (IOException e) {
                log.error("terminate connection error");
            }
        }
    }

    private void sendResponse(PrintWriter out,POP3Response response){
        out.print(response);
        out.flush();
    }

    private void handleTransaction(Scanner in,PrintWriter out) throws NoSuchElementException {
        POP3State state = new POP3State();
        while (true){
            String line = in.nextLine();
            if (line == null){
                break;
            }
            POP3Request request = POP3Request.createRequest(line,state);
            POP3Response res = request.execute();

            //send to client
            sendResponse(out,res);

            //使用QUIT命令后会中断线程
            if (Thread.currentThread().isInterrupted()) break;
        }
    }
}
