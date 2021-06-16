package com.fyc412.email.pop3.command;

import com.fyc412.email.mailbox.MailNotFoundException;
import com.fyc412.email.mailbox.Mailbox;
import com.fyc412.email.mailbox.Mailboxes;
import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

public class DELECommand implements Command{
    private POP3State state;
    private String param;
    private POP3Response res;
    private Integer mailID = null;


    public DELECommand(POP3State state, String param) {
        this.state = state;
        this.param = param;
        this.res = new SingleLineResponse();
    }


    @Override
    public POP3Response execute() {
        if (state.getState()!=POP3State.TRANSACTION) {
            this.res.setMessage("use USER command to log in first", false);
        }else {
            if (!isSatisfied()){
                this.res.setMessage("DELE command expects an integer of mail to delete", false);
            }else {
                Mailbox mailbox = Mailboxes.getInstance().getMailbox(state.getUsername());
                try {
                    mailbox.deleteMail(mailID);
                    res.setMessage("mail deleted", true);
                } catch (MailNotFoundException e) {
                    res.setMessage("cannot delete mail", false);
                }
            }
        }
        return res;
    }


    public boolean isSatisfied() {
        boolean satisfied = param!=null;

        if (satisfied) {
            try {
                this.mailID = Integer.parseInt(param, 10);
            } catch (NumberFormatException e) {
                this.mailID = null;
                satisfied = false;
            }
        }

        return satisfied;
    }
}
