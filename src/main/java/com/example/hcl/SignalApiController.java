package com.example.hcl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle incoming signals
 */
@Slf4j
@RestController
@RequestMapping("/api/signal")
@RequiredArgsConstructor
public class SignalApiController {

    private final SignalHandler signalHandler;

    @PostMapping
    public void handleSignal(@RequestParam Integer signal) {
        log.debug("Received signal: " + signal);
        signalHandler.handleSignal(signal);
        log.debug("Finished handling signal: " + signal);
    }
}
