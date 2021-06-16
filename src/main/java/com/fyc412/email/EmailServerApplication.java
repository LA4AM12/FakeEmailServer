package com.fyc412.email;


import com.fyc412.email.pop3.POP3Server;
import com.fyc412.email.smtp.SMTPServer;

import java.io.IOException;

public class EmailServerApplication {
    public static void main(String[] args) throws IOException {
        POP3Server.start(POP3Server.DEFAULT_POP3_PORT);
        SMTPServer.start(SMTPServer.DEFAULT_SMTP_PORT);
    }
}
