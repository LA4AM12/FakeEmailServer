package com.fyc412.email.pop3;

public class POP3Action {
    private byte action;

    private static final byte USER_BYTE = 1;
    private static final byte PASS_BYTE = 2;
    private static final byte STAT_BYTE = 3;
    private static final byte LIST_BYTE = 4;
    private static final byte RETR_BYTE = 5;
    private static final byte DELE_BYTE = 6;
    private static final byte RSET_BYTE = 7;
    private static final byte TOP_BYTE  = 8;
    private static final byte UIDL_BYTE = 9;
    private static final byte NOOP_BYTE = 10;
    private static final byte QUIT_BYTE = 11;
    private static final byte UNRECOG_BYTE = 12;

    public static final POP3Action USER = new POP3Action(USER_BYTE);
    public static final POP3Action PASS = new POP3Action(PASS_BYTE);
    public static final POP3Action STAT = new POP3Action(STAT_BYTE);
    public static final POP3Action LIST = new POP3Action(LIST_BYTE);
    public static final POP3Action RETR = new POP3Action(RETR_BYTE);
    public static final POP3Action DELE = new POP3Action(DELE_BYTE);
    public static final POP3Action RSET = new POP3Action(RSET_BYTE);
    public static final POP3Action TOP  = new POP3Action(TOP_BYTE);
    public static final POP3Action UIDL = new POP3Action(UIDL_BYTE);
    public static final POP3Action NOOP = new POP3Action(NOOP_BYTE);
    public static final POP3Action QUIT = new POP3Action(QUIT_BYTE);
    public static final POP3Action UNRECOG = new POP3Action(UNRECOG_BYTE);

    public POP3Action(byte action) {
        this.action = action;
    }

    @Override
    public String toString() {
        switch (action) {
            case USER_BYTE:
                return "USER";
            case PASS_BYTE:
                return "PASS";
            case STAT_BYTE:
                return "STAT";
            case LIST_BYTE:
                return "LIST";
            case RETR_BYTE:
                return "RETR";
            case DELE_BYTE:
                return "DELE";
            case QUIT_BYTE:
                return "QUIT";
            case RSET_BYTE:
                return "RSET";
            case TOP_BYTE:
                return "TOP";
            case UIDL_BYTE:
                return "UIDL";
            case NOOP_BYTE:
                return "NOOP";
            case UNRECOG_BYTE:
                return "UNRECOG";
            default:
                return "Unknown";
        }
    }
}
