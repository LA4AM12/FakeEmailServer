package com.fyc412.email.mailbox;

import com.fyc412.email.smtp.SMTPServer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//单例模式
public class Mailboxes {
    private static final Mailboxes MAILBOXES = new Mailboxes();

    //线程安全的hashmap
    private ConcurrentHashMap<String,Mailbox> mailboxes = new ConcurrentHashMap<>();

    public static Mailboxes getInstance(){return MAILBOXES;}

    //用户登录后绑定自己的mailbox
    public void setupMailbox(String username){
        if (!this.mailboxes.containsKey(username)){
            //为新登陆的用户创建一个mailbox
            Mailbox mailbox = new Mailbox(username);
            //模拟数据库查询
            List<Mail> receivedEmails = SMTPServer.getReceivedEmails();
            for (Mail mail : receivedEmails){
                if(mail.getTo().equals("<"+username+">")){
                    mailbox.addMail(mail);
                }
            }
            mailboxes.put(username,mailbox);
        }
    }

    public Mailbox getMailbox(String username) {
        Mailbox mb = null;

        try {
            mb = this.mailboxes.get(username);
        } catch (NullPointerException e) {
            System.out.println("Mailbox not set up for user: " + username);
            e.printStackTrace();
        }

        return mb;
    }
}
