package com.example.hcl.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when an unknown signal is received
 */
@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownActionException extends RuntimeException {
    public UnknownActionException(String message) {
        super(message);
        log.error(message);
    }
}
