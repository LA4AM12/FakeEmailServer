package com.fyc412.email.mailbox;

import com.fyc412.email.smtp.SMTPState;

import java.util.*;

public class Mail {
    private int ID;
    private String to;
    private String from;
    private Map<String, List<String>> headers;
    private StringBuilder body;

    public Mail() {
        headers = new LinkedHashMap<>(10);
        body = new StringBuilder();
    }

    /**
     * Update the headers or body depending on the SmtpResponse object and line of input.
     *
     * @param state SMTPState object
     * @param params   remainder of input line after SMTP command has been removed
     */
    public void store(SMTPState state, String params) {
        if (params != null) {
            if (state == SMTPState.MAIL){
                this.from = params;
            } else if (state == SMTPState.RCPT) {
                this.to = params;
            } else if (state == SMTPState.DATA_HDR) {
                int headerNameEnd = params.indexOf(':');
                if (headerNameEnd >= 0) {
                    String name = params.substring(0, headerNameEnd).trim();
                    String value = params.substring(headerNameEnd + 1).trim();
                    addHeader(name, value);
                }
            } else if (state == SMTPState.DATA_BODY) {
                body.append(params+"\r\n");
            }
        }
    }

    /**
     * Adds a header to the Map.
     *
     * @param name  header name
     * @param value header value
     */
    private void addHeader(String name, String value) {
        //如果不存在这个 key(name)，则添加到 hasMap 中。存在则返回
        List<String> valueList = headers.computeIfAbsent(name, k -> new ArrayList<>(1));
        valueList.add(value);
    }

    /**
     * 获取指定的header.
     *
     * @param name header name
     * @return value(s) associated with the header name
     */
    public List<String> getHeaderValues(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(new ArrayList<>(values));
        }
    }

    public StringBuilder getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
            for (String value : stringListEntry.getValue()) {
                msg.append(stringListEntry.getKey());
                msg.append(": ");
                msg.append(value);
                msg.append("\r\n");
            }
        }
        msg.append('\n');
        msg.append(body);
        msg.append('\n');
        return msg.toString();
    }

    public int getID() {
        return ID;
    }

    public String getTo() {
        return to;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFrom() {
        return from;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
