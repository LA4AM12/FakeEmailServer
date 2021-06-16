package com.fyc412.email.pop3.command;

import com.fyc412.email.pop3.POP3Response;

public interface Command {
    POP3Response execute();
}
