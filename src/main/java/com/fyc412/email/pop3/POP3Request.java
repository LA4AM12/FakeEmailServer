package com.fyc412.email.pop3;

import com.fyc412.email.pop3.command.*;


/**
 * POP3状态转换表
 * <PRE>
 * -----------+------------------------------------------
 *            |                  State
 * Action     +-------------+-------------+-------------+
 *            | AUTH        |TRANSACTION  | UPDATE      |
 * -----------+-------------+-------------+-------------+
 * user       | +OK/AUTH    | -ERR/TRANS  |    ----     |
 * pass       | +OK/TRANS   | -ERR/TRANS  |    ----     |
 * stat       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * uidl       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * list       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * retr       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * dele       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * top        | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * rset       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * noop       | -ERR/AUTH   | +OK/TRANS   |    ----     |
 * quit       | -ERR/AUTH   | +OK/UPDATE  |    ----     |
 * </PRE>
 */

public class POP3Request {
    //从客户端收到的指令
    private POP3Action action;
    //当前所处的SMTP的状态
    private POP3State state;
    //指令后附带的参数
    private String param;

    public POP3Request(POP3Action action, POP3State state, String param) {
        this.action = action;
        this.state = state;
        this.param = param;
    }

    public static POP3Request createRequest(String s, POP3State state) {
        String[] tmp = s.split(" ");
        POP3Action action;
        String command = tmp[0].toUpperCase();
        String param = tmp.length > 1 ? tmp[1] : null;

        switch (command) {
            case "USER":
                action = POP3Action.USER;
                break;

            case "PASS":
                action = POP3Action.PASS;
                break;

            case "STAT":
                action = POP3Action.STAT;
                break;

            case "LIST":
                action = POP3Action.LIST;
                break;

            case "RETR":
                action = POP3Action.RETR;
                break;

            case "DELE":
                action = POP3Action.DELE;
                break;

            case "RSET":
                action = POP3Action.RSET;
                break;

            case "TOP":
                action = POP3Action.TOP;
                break;

            case "UIDL":
                action = POP3Action.UIDL;
                break;

            case "NOOP":
                action = POP3Action.NOOP;
                break;

            case "QUIT":
                action = POP3Action.QUIT;
                break;

            default:
                action = POP3Action.UNRECOG;
                break;
        }
        return new POP3Request(action, state, param);
    }

    public POP3Response execute() {
        POP3Response res;
        if (action == POP3Action.USER) {
            res = new UserCommand(state, param).execute();
        } else if (action == POP3Action.PASS) {
            res = new PASSCommand(state, param).execute();
        } else if (action == POP3Action.STAT) {
            res = new STATCommand(state, param).execute();
        } else if (action == POP3Action.LIST) {
            res = new LISTCommand(state, param).execute();
        } else if (action == POP3Action.RETR) {
            res = new RETRCommand(state, param).execute();
        } else if (action == POP3Action.DELE) {
            res = new DELECommand(state, param).execute();
        } else if (action == POP3Action.NOOP) {
            res = new NOOPCommand(state).execute();
        } else if (action == POP3Action.QUIT) {
            res = new QUITCommand(state).execute();
        } else {
            res = new SingleLineResponse("Command not recognized", false);
        }

        return res;
    }
}
