package com.fyc412.email.pop3;

public class POP3State {
    private byte state;

    //当前登录的用户
    private String username = "";

    public static final byte AUTHORIZATION = 1;
    public static final byte TRANSACTION = 2;
    public static final byte UPDATE = 3;

    public POP3State() {
        //初始的状态都为auth
        this.state = AUTHORIZATION;
    }

    public byte getState() {
        return state;
    }

    public String getUsername() {
        return username;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
