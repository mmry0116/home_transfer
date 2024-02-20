package com.mmry.exception;


import org.springframework.security.core.AuthenticationException;

public class KaptchaNoMathException extends AuthenticationException {
    public KaptchaNoMathException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KaptchaNoMathException(String msg) {
        super(msg);
    }
}
