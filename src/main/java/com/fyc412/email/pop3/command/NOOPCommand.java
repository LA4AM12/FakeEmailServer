package com.fyc412.email.pop3.command;

import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

public class NOOPCommand implements Command{
    private POP3State state;
    private POP3Response res;

    public NOOPCommand(POP3State state) {
        this.state = state;
        res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        if (state.getState() != POP3State.TRANSACTION) {
            this.res.setMessage("command can only be used after logging in", false);
        } else {
            this.res.setMessage("", true);
        }
        return res;
    }
}
