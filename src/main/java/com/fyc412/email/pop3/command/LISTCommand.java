package com.fyc412.email.pop3.command;

import com.fyc412.email.mailbox.Mail;
import com.fyc412.email.mailbox.MailNotFoundException;
import com.fyc412.email.mailbox.Mailbox;
import com.fyc412.email.mailbox.Mailboxes;
import com.fyc412.email.pop3.MultiLineResponse;
import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

import java.util.concurrent.atomic.AtomicInteger;


public class LISTCommand implements Command{
    private POP3State state;
    private String param;
    private POP3Response res;
    private Integer mailID = null;

    public LISTCommand(POP3State state, String param) {
        this.state = state;
        this.param = param;
        this.res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        if (state.getState() != POP3State.TRANSACTION) {
            this.res.setMessage("use USER command to log in first", false);
        } else {
            // 可选参数为邮件ID
            if (param != null) {
                try {
                    mailID = Integer.parseInt(param);
                }catch (NumberFormatException e){
                    this.res.setMessage("argument provided is invalid", false);
                }
            }
            
            Mailbox mailbox = Mailboxes.getInstance().getMailbox(state.getUsername());

            //如果没有指定邮件id 则列举信箱中的全部邮件
            if (mailID == null) {
                StringBuilder singleLine = new StringBuilder();
                StringBuilder multiline = new StringBuilder();

                AtomicInteger id = new AtomicInteger();
                mailbox.getMails().forEach((k,v)->{
                    if (v){
                        k.setID(id.incrementAndGet());
                        int len = k.getBody().length();
                        multiline.append(id).append(" ").append(len).append("\r\n");
                    }
                });

                singleLine.append(mailbox.getNumberOfMails()).append(" ").append(mailbox.getSumOfBytes());

                // 重定义为多行的resp
                this.res = new MultiLineResponse(singleLine.toString(), multiline.toString(), true);

            } else {
                //列出指定信件的信息
                try {
                    Mail m = mailbox.getMail(this.mailID);
                    this.res.setMessage(this.mailID + " " + m.getBody().length(), true);
                } catch (MailNotFoundException e) {
                    this.res.setMessage("no such mail", false);
                }
            }

        }
        return res;
    }
}
