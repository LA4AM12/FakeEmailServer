package com.fyc412.email.pop3.command;

import com.fyc412.email.mailbox.Mail;
import com.fyc412.email.mailbox.MailNotFoundException;
import com.fyc412.email.mailbox.Mailbox;
import com.fyc412.email.mailbox.Mailboxes;
import com.fyc412.email.pop3.MultiLineResponse;
import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

public class RETRCommand implements Command{
    private POP3State state;
    private String param;
    private POP3Response res;
    private Integer mailID = null;

    public RETRCommand(POP3State state, String param) {
        this.state = state;
        this.param = param;
        res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        if (state.getState() != POP3State.TRANSACTION) {
            res.setMessage("use USER command to log in first", false);
        } else {
            if (!isSatisfied()) {
                res.setMessage("argument provided is invalid", false);
            } else {
                Mailbox mailbox = Mailboxes.getInstance().getMailbox(state.getUsername());
                try {
                    Mail m = mailbox.getMail(this.mailID);

                    StringBuilder single = new StringBuilder();
                    single.append(m.getBody().length()).append(" octets");

                    this.res = new MultiLineResponse(single.toString(), m.toString(), true);

                } catch (MailNotFoundException e) {
                    this.res.setMessage("no such mail", false);
                }
            }
        }
        return res;
    }


    public boolean isSatisfied() {
        boolean satisfied = param != null;

        if (satisfied) {
            try {
                this.mailID = Integer.parseInt(param, 10);
            } catch (NumberFormatException e) {
                satisfied = false;
            }
        }
        return satisfied;
    }
}
