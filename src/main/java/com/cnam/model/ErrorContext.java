package com.cnam.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ErrorContext {
    private boolean isPermanentError;
    private String messageContent;
    private Throwable cause;
    private boolean logStacktrace;

    public ErrorContext(boolean isPermanentError, String messageContent, Throwable cause) {
        this.isPermanentError = isPermanentError;
        this.messageContent = messageContent;
        this.cause = cause;
        this.logStacktrace = true;
    }
}
