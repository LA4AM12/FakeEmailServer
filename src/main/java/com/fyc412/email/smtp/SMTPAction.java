package com.fyc412.email.smtp;

//能够支持的操作码
public class SMTPAction {
    private byte action;

    private static final byte CONNECT_BYTE = (byte) 1;
    // HELO ACTION
    private static final byte HELO_BYTE = (byte) 2;
    // MAIL FROM ACTION
    private static final byte MAIL_BYTE = (byte) 3;
    // RCPT TO ACTION
    private static final byte RCPT_BYTE = (byte) 4;
    // DATA ACTION
    private static final byte DATA_BYTE = (byte) 5;
    // <CR><LF>.<CR><LF> ACTION
    private static final byte DATA_END_BYTE = (byte) 6;
    // QUIT ACTION
    private static final byte QUIT_BYTE = (byte) 7;
    //此标识被视为  body text
    private static final byte UNREC_BYTE = (byte) 8;
    //headers和body的分隔标识符(空行)
    private static final byte BLANK_LINE_BYTE = (byte) 9;


    /** 退出/复位当前的邮件传输 */
    private static final byte RSET_BYTE = (byte) -1;
    /** 验证指定的邮箱是否存在 */
    private static final byte VRFY_BYTE = (byte) -2;
    /** 验证给定的邮箱列表是否存在，扩充邮箱列表 */
    private static final byte EXPN_BYTE = (byte) -3;
    /** 查询服务器支持什么命令 */
    private static final byte HELP_BYTE = (byte) -4;
    /** 要求接收SMTP仅做OK应答，用于测试连接 */
    private static final byte NOOP_BYTE = (byte) -5;

    //static final 全局唯一不变

    static final SMTPAction CONNECT = new SMTPAction(CONNECT_BYTE);
    static final SMTPAction HELO = new SMTPAction(HELO_BYTE);
    static final SMTPAction MAIL = new SMTPAction(MAIL_BYTE);
    static final SMTPAction RCPT = new SMTPAction(RCPT_BYTE);
    static final SMTPAction DATA = new SMTPAction(DATA_BYTE);
    static final SMTPAction DATA_END = new SMTPAction(DATA_END_BYTE);
    static final SMTPAction UNRECOG = new SMTPAction(UNREC_BYTE);
    static final SMTPAction QUIT = new SMTPAction(QUIT_BYTE);
    static final SMTPAction BLANK_LINE = new SMTPAction(BLANK_LINE_BYTE);

    //stateless 不会导致状态发生改变
    static final SMTPAction RSET = new SMTPAction(RSET_BYTE);
    static final SMTPAction VRFY = new SMTPAction(VRFY_BYTE);
    static final SMTPAction EXPN = new SMTPAction(EXPN_BYTE);
    static final SMTPAction HELP = new SMTPAction(HELP_BYTE);
    static final SMTPAction NOOP = new SMTPAction(NOOP_BYTE);

    public SMTPAction(byte action) {
        this.action = action;
    }

    public boolean isStateless() {
        return action < 0;
    }

    @Override
    public String toString() {
        switch (action) {
            case CONNECT_BYTE:
                return "CONNECT";
            case HELO_BYTE:
                return "HELO";
            case MAIL_BYTE:
                return "MAIL";
            case RCPT_BYTE:
                return "RCPT";
            case DATA_BYTE:
                return "DATA";
            case DATA_END_BYTE:
                return ".";
            case QUIT_BYTE:
                return "QUIT";
            case RSET_BYTE:
                return "RSET";
            case VRFY_BYTE:
                return "VRFY";
            case EXPN_BYTE:
                return "EXPN";
            case HELP_BYTE:
                return "HELP";
            case NOOP_BYTE:
                return "NOOP";
            case UNREC_BYTE:
                return "Unrecognized command / data";
            case BLANK_LINE_BYTE:
                return "Blank line";
            default:
                return "Unknown";
        }
    }
}
