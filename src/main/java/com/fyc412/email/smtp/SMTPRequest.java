package com.fyc412.email.smtp;
/**
 * SMTP状态转换表
 * <PRE>
 * -----------+-------------------------------------------------------------------------------------------------
 *            |                                 State
 *  Action    +-------------+-----------+-----------+--------------+---------------+---------------+------------
 *            | CONNECT     | GREET     | MAIL      | RCPT         | DATA_HDR      | DATA_BODY     | QUIT
 * -----------+-------------+-----------+-----------+--------------+---------------+---------------+------------
 * connect    | 220/GREET   | 503/GREET | 503/MAIL  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * helo       | 503/CONNECT | 250/MAIL  | 503/MAIL  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * mail       | 503/CONNECT | 503/GREET | 250/RCPT  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 250/RCPT
 * rcpt       | 503/CONNECT | 503/GREET | 503/MAIL  | 250/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * data       | 503/CONNECT | 503/GREET | 503/MAIL  | 354/DATA_HDR | 503/DATA_HDR  | 503/DATA_BODY | 503/QUIT
 * data_end   | 503/CONNECT | 503/GREET | 503/MAIL  | 503/RCPT     | 250/QUIT      | 250/QUIT      | 503/QUIT
 * unrecog    | 500/CONNECT | 500/GREET | 500/MAIL  | 500/RCPT     | ---/DATA_HDR  | ---/DATA_BODY | 500/QUIT
 * quit       | 503/CONNECT | 503/GREET | 503/MAIL  | 503/RCPT     | 503/DATA_HDR  | 503/DATA_BODY | 250/CONNECT
 * blank_line | 503/CONNECT | 503/GREET | 503/MAIL  | 503/RCPT     | ---/DATA_BODY | ---/DATA_BODY | 503/QUIT
 * rset       | 250/GREET   | 250/GREET | 250/GREET | 250/GREET    | 250/GREET     | 250/GREET     | 250/GREET
 * vrfy       | 252/CONNECT | 252/GREET | 252/MAIL  | 252/RCPT     | 252/DATA_HDR  | 252/DATA_BODY | 252/QUIT
 * expn       | 252/CONNECT | 252/GREET | 252/MAIL  | 252/RCPT     | 252/DATA_HDR  | 252/DATA_BODY | 252/QUIT
 * help       | 211/CONNECT | 211/GREET | 211/MAIL  | 211/RCPT     | 211/DATA_HDR  | 211/DATA_BODY | 211/QUIT
 * noop       | 250/CONNECT | 250/GREET | 250/MAIL  | 250/RCPT     | 250|DATA_HDR  | 250/DATA_BODY | 250/QUIT
 * </PRE>
 */
public class SMTPRequest {
    //从客户端收到的指令
    private SMTPAction action;
    //当前所处的SMTP的状态
    private SMTPState state;
    //指令后附带的参数
    private String params;

    public SMTPRequest(SMTPAction action, SMTPState state, String params) {
        this.action = action;
        this.state = state;
        this.params = params;
    }

    /**
     * 根客户端输入的一行和当前状态来创建一个SMTP请求
     * @param s line of input
     * @param state current state
     * @return a corresponding SmtpRequest object
     */
    public static SMTPRequest createRequest(String s,SMTPState state){
        SMTPAction action;
        String params = null;

        // 单独处理正在输入header或data text的部分
        if (state == SMTPState.DATA_HDR){
            if (s.equals(".")){
                action = SMTPAction.DATA_END;
            } else if (s.length() < 1){
                action = SMTPAction.BLANK_LINE;
            } else {
                action = SMTPAction.UNRECOG;
                params = s;
            }
        } else if (state == SMTPState.DATA_BODY){
            if (s.equals(".")){
                action = SMTPAction.DATA_END;
            } else {
                action = SMTPAction.UNRECOG;
                if (s.length() < 1){
                    params = "\n";
                } else {
                    params = s;
                }
            }
        } else {
            String su = s.toUpperCase();
            if (su.startsWith("HELO ")){
                action = SMTPAction.HELO;
                params = s.substring(5);
            } else if (su.startsWith("MAIL FROM:")) {
                action = SMTPAction.MAIL;
                params = s.substring(10);
            } else if (su.startsWith("RCPT TO:")) {
                action = SMTPAction.RCPT;
                params = s.substring(8);
            } else if (su.startsWith("DATA")) {
                action = SMTPAction.DATA;
            } else if (su.startsWith("QUIT")) {
                action = SMTPAction.QUIT;
            } else if (su.startsWith("RSET")) {
                action = SMTPAction.RSET;
            } else if (su.startsWith("NOOP")) {
                action = SMTPAction.NOOP;
            } else if (su.startsWith("EXPN")) {
                action = SMTPAction.EXPN;
            } else if (su.startsWith("VRFY")) {
                action = SMTPAction.VRFY;
            } else if (su.startsWith("HELP")) {
                action = SMTPAction.HELP;
            } else {
                action = SMTPAction.UNRECOG;
            }
        }
        return new SMTPRequest(action,state,params);
    }
    /**
     * 执行一个请求，根据转换表进行
     * @return response to the request
     */
    public SMTPResponse execute(){
        SMTPResponse response;

        if (action.isStateless()){
            //并没有去实现这几个码，感觉没什么意义
            if (action == SMTPAction.EXPN || action == SMTPAction.VRFY){
                response = new SMTPResponse(502, "Not supported", this.state);
            } else if (action == SMTPAction.HELP){
                response = new SMTPResponse(211, "No help available", this.state);
            } else if (action == SMTPAction.NOOP) {
                response = new SMTPResponse(250, "OK", this.state);
            } else if (action == SMTPAction.RSET){
                response = new SMTPResponse(250, "OK", SMTPState.GREET);
            } else {
                response = new SMTPResponse(502, "command not implemented", this.state);
            }
        } else { // Stateful commands
            if (action == SMTPAction.CONNECT){
                if (state == SMTPState.CONNECT){
                    response = new SMTPResponse(220, "localhost LA4AM12 SMTP service ready", SMTPState.GREET);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands:"+action, this.state);
                }
            } else if (action == SMTPAction.HELO){
                if (state == SMTPState.GREET){
                    response = new SMTPResponse(250, "OK", SMTPState.MAIL);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else if (action == SMTPAction.MAIL){    //状态为quit 表示客户端请求继续发送
                if (state == SMTPState.MAIL || state == SMTPState.QUIT){
                    response = new SMTPResponse(250, "OK", SMTPState.RCPT);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else if (action == SMTPAction.RCPT){
                if (state == SMTPState.RCPT){  //状态不发生改变，可以继续设置接收方
                    response = new SMTPResponse(250, "OK", this.state);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else if (action == SMTPAction.DATA){
                if (state == SMTPState.RCPT){
                    response = new SMTPResponse(354, "Start mail input; end with <CRLF>.<CRLF>", SMTPState.DATA_HDR);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else if (action == SMTPAction.UNRECOG) {   //正在输入正文
                if (state == SMTPState.DATA_HDR || state == SMTPState.DATA_BODY) {
                    response = new SMTPResponse(-1, "", this.state);  //不做回应
                } else {
                    response = new SMTPResponse(502, "command not implemented", this.state);
                }
            } else if (action == SMTPAction.DATA_END) {
                if (state == SMTPState.DATA_HDR || state == SMTPState.DATA_BODY){
                    response = new SMTPResponse(250, "OK", SMTPState.QUIT);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else if (action == SMTPAction.BLANK_LINE) {  //标题和正文通过空行分隔
                if (state == SMTPState.DATA_HDR){
                    response = new SMTPResponse(-1, "", SMTPState.DATA_BODY);
                } else if (state == SMTPState.DATA_BODY){
                    response = new SMTPResponse(-1, "", this.state);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else if (action == SMTPAction.QUIT) {
                if (state == SMTPState.QUIT){
                    response = new SMTPResponse(221, "localhost LA4AM12 SMTP service closing transmission channel", SMTPState.CONNECT);
                } else {
                    response = new SMTPResponse(503, "Bad sequence of commands: "+action, this.state);
                }
            } else {
                response = new SMTPResponse(502, "command not implemented", this.state);
            }
        }
        return response;
    }

    public String getParams() {
        return params;
    }
}
