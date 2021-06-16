package com.fyc412.email.smtp;
/*
501  参数格式错误
502  命令不可实现
503  错误的命令序列
504  命令参数不可实现
211  系统状态或系统帮助响应
214  帮助信息
220  ＜domain＞服务就绪
221  ＜domain＞服务关闭
421  ＜domain＞服务未就绪，关闭传输信道
250  要求的邮件操作完成
251  用户非本地，将转发向＜forward-path＞
450  要求的邮件操作未完成，邮箱不可用
550  要求的邮件操作未完成，邮箱不可用
451  放弃要求的操作；处理过程中出错
551  用户非本地，请尝试＜forward-path＞
452  系统存储不足，要求的操作未执行
552  过量的存储分配，要求的操作未执行
553  邮箱名不可用，要求的操作未执行
354  开始邮件输入，以"."结束
554  操作失败
*/


public class SMTPResponse {
    //响应码
    private int code;
    //响应说明
    private String message;
    //当请求被执行后的下一个状态
    private SMTPState nextState;

    public SMTPResponse(int code, String message, SMTPState nextState) {
        this.code = code;
        this.message = message;
        this.nextState = nextState;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public SMTPState getNextState() {
        return nextState;
    }
}
