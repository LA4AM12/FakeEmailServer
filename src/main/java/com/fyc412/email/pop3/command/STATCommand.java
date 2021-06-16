package com.fyc412.email.pop3.command;

import com.fyc412.email.mailbox.Mailbox;
import com.fyc412.email.mailbox.Mailboxes;
import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;


public class STATCommand implements Command{
    private POP3State state;
    private String param;
    private POP3Response res;

    public STATCommand(POP3State state, String param) {
        this.state = state;
        this.param = param;
        res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        if (state.getState()!=POP3State.TRANSACTION){
            res.setMessage("use USER command to log in first", false);
        }else {
            if (param != null)
                this.res.setMessage("STAT command expects no arguments", false);
            else {
                Mailbox mailbox = Mailboxes.getInstance().getMailbox(state.getUsername());

                //列出信箱中所有的邮件以及大小
                int count = mailbox.getNumberOfMails();
                int len = mailbox.getSumOfBytes();
                this.res.setMessage(count + " " + len, true);
            }
        }
        return res;
    }
}
