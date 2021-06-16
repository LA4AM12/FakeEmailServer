package com.fyc412.email.pop3.command;

import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

public class UserCommand implements Command{
    private POP3State state;
    private SingleLineResponse res;
    private String username;

    public UserCommand(POP3State state,String username) {
        this.state = state;
        this.username = username;
        this.res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        if (state.getState() != POP3State.AUTHORIZATION){
            //用户已经登录
            if (state.getState() == POP3State.TRANSACTION)
                this.res.setMessage("user has already logged in", false);
            //用户退出登录
            else if (state.getState() == POP3State.UPDATE)
                this.res.setMessage("updating mail drop", false);
            else
                this.res.setMessage("critical error state not recognized", false);
        }else {
            if (username == null)
                this.res.setMessage("USER command expects a username", false);
            else{
                this.res.setMessage("",true);
                state.setUsername(username);
            }

        }
        return this.res;
    }
}
