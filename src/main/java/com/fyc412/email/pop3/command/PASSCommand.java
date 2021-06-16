package com.fyc412.email.pop3.command;

import com.fyc412.email.mailbox.Mailboxes;
import com.fyc412.email.pop3.POP3Response;
import com.fyc412.email.pop3.POP3State;
import com.fyc412.email.pop3.SingleLineResponse;

public class PASSCommand implements Command{
    private POP3State state;
    private String pass;
    private POP3Response res;

    public PASSCommand(POP3State state, String pass) {
        this.state = state;
        this.pass = pass;
        res = new SingleLineResponse();
    }

    @Override
    public POP3Response execute() {
        if (state.getState() != POP3State.AUTHORIZATION){
            res.setMessage("user has already logged in", false);
        }else {
            if (pass == null){
                res.setMessage("PASS command expects a password", false);
            }else {
                String username = state.getUsername();
                if (username.isEmpty()){
                    this.res.setMessage("use USER command to first provide username before PASS to log in", false);
                }else {
                    //此处模拟数据库查询
                    if (username.equals("sloth@fyc.com") && pass.equals("123")){
                        //成功登录，初始化一个信箱
                        Mailboxes.getInstance().setupMailbox(username);
                        state.setState(POP3State.TRANSACTION);
                        this.res.setMessage("mailbox locked and ready", true);
                    }else {
                        res.setMessage("password does not match", false);
                    }
                }
            }
        }
        return res;
    }
}
