package com.fyc412.email.smtp;

/**
 * SMTP server state.
 */
public class SMTPState {
    private byte state;

    /** CONNECT state. */
    private static final byte CONNECT_BYTE = (byte) 1;
    /** GREET state. */
    private static final byte GREET_BYTE = (byte) 2;
    /** MAIL state. */
    private static final byte MAIL_BYTE = (byte) 3;
    /** RCPT state. */
    private static final byte RCPT_BYTE = (byte) 4;
    /** DATA_HEADER state. */
    private static final byte DATA_HEADER_BYTE = (byte) 5;
    /** DATA_BODY state. */
    private static final byte DATA_BODY_BYTE = (byte) 6;
    /** QUIT state. */
    private static final byte QUIT_BYTE = (byte) 7;

    public static final SMTPState CONNECT = new SMTPState(CONNECT_BYTE);
    public static final SMTPState GREET = new SMTPState(GREET_BYTE);
    public static final SMTPState MAIL = new SMTPState(MAIL_BYTE);
    public static final SMTPState RCPT = new SMTPState(RCPT_BYTE);
    public static final SMTPState DATA_HDR = new SMTPState(DATA_HEADER_BYTE);
    public static final SMTPState DATA_BODY = new SMTPState(DATA_BODY_BYTE);
    public static final SMTPState QUIT = new SMTPState(QUIT_BYTE);

    private SMTPState(byte state) {
        this.state = state;
    }

    public String toString() {
        switch(state) {
            case CONNECT_BYTE:
                return "CONNECT";
            case GREET_BYTE:
                return "GREET";
            case MAIL_BYTE:
                return "MAIL";
            case RCPT_BYTE:
                return "RCPT";
            case DATA_HEADER_BYTE:
                return "DATA_HDR";
            case DATA_BODY_BYTE:
                return "DATA_BODY";
            case QUIT_BYTE:
                return "QUIT";
            default:
                return "Unknown";
        }
    }
}
