package com.example.hcl.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super("Internal Error"); // we don't want to leak internal details to the client
        log.error(message); // we still want to log the internal details
    }
}
