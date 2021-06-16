package com.fyc412.email.mailbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Mailbox {
    //一个用户对应一个邮箱
    private String username;
    //boolean 标记是否已经删除
    private Map<Mail,Boolean> mails = new LinkedHashMap<>();

    public Mailbox(String username) {
        this.username = username;
    }

    //add mail to mailbox
    public synchronized void addMail(Mail mail){
        mails.put(mail, true);
    }

    public Map<Mail, Boolean> getMails() {
        return mails;
    }

    //通过mail id 删除邮件
    public synchronized void deleteMail(int mid) throws MailNotFoundException {
        boolean deleted = false;
        for (Map.Entry<Mail, Boolean> mb : mails.entrySet()) {
            Mail m = mb.getKey();
            int mailID = mb.getKey().getID();

            // Check mail ID and mail cannot be deleted beforehand
            if (mid == mailID && mb.getValue()) {
                this.mails.put(m, false);
                deleted = true;
                break;
            }
        }
        if ( ! deleted ) throw new MailNotFoundException();
    }

    public int getNumberOfMails() {
        AtomicInteger sum = new AtomicInteger();
        mails.forEach((k,v)->{
            if (v)
                sum.getAndIncrement();
        });
        return sum.get();
    }

    public int getSumOfBytes() {

        AtomicInteger sum = new AtomicInteger();

        mails.forEach((k,v)-> {
            if (v)
                sum.addAndGet(k.getBody().length());
        });
        return sum.get();
    }

    public Mail getMail(int mid) throws MailNotFoundException {
        if (!mailExists(mid)) {
            throw new MailNotFoundException();
        } else {
            Mail returnedMail = null;

            for (Map.Entry<Mail,Boolean> m : mails.entrySet()){
                if (!m.getValue()) continue;
                if (m.getKey().getID() == mid){
                    returnedMail = m.getKey();
                    break;
                }
            }
            assert (returnedMail != null);
            return returnedMail;
        }
    }

    // Check if mail exists with the mail ID
    public boolean mailExists(int mid) {
        // Check if mail ID exists within mailbox
        boolean exists = false;

        for (Map.Entry<Mail,Boolean> m : mails.entrySet()){
            if (!m.getValue()) continue;
            if (m.getKey().getID() == mid){
                exists = true;
                break;
            }
        }

        return exists;
    }

    public synchronized void deleteMail(Mail key) {
        this.mails.remove(key);
    }
}
