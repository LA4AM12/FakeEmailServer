package com.fyc412.email.pop3;

public class MultiLineResponse implements POP3Response{
    private static final String OK = "+OK ";
    private static final String ERR = "-ERR ";

    private SingleLineResponse slr = new SingleLineResponse();
    private StringBuilder message = new StringBuilder();

    public MultiLineResponse(String singleMsg, String appendix,boolean status) {
        this.slr.setMessage(singleMsg,status);
        this.setMessage(appendix,status);
    }

    @Override
    public void setMessage(String msg, boolean status) {
        this.message.append(msg);
    }

    @Override
    public String toString() {
        // 结束标识
        this.message.append(".\r\n");

        // Return resultant string together with single line response
        return slr.toString() + this.message.toString();
    }
}
