package com.fyc412.email.pop3.command;

import com.fyc412.email.mailbox.Mail;
import com.fyc412.email.mailbox.Mailbox;
import com.fyc412.email.mailbox.Mailboxes;
import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

import java.util.Map;

public class QUITCommand implements Command{
    private POP3State state;
    private POP3Response res;

    public QUITCommand(POP3State state) {
        this.state = state;
        this.res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        switch (state.getState()){
            case POP3State.AUTHORIZATION:
                state.setUsername("");
                res.setMessage("Terminating connection", true);
                Thread.currentThread().interrupt();
                break;
            case POP3State.TRANSACTION:
                //转移到UPDATE
                state.setState(POP3State.UPDATE);
                this.execute();
                break;
            case POP3State.UPDATE:
                //此处提交事务，断开TCP连接
                //在数据库中删除所有标记为false的mail

                //在mailbox中也删除
                Mailbox mailbox = Mailboxes.getInstance().getMailbox(state.getUsername());
                Map<Mail, Boolean> mails = mailbox.getMails();
                mails.forEach((k,v)->{
                    if (!v) mailbox.deleteMail(k);
                });
                res.setMessage("", true);
                Thread.currentThread().interrupt();
                break;
        }
        return res;
    }
}
