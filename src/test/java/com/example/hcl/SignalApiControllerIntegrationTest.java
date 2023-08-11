package com.example.hcl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignalApiController.class)
class SignalApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignalHandler signalHandler;

    @MockBean
    private Algo algo;

    @Test
    @SneakyThrows
    void testSignalController() {

        // performing post call to "/api/signal" with param signal = 1
        mockMvc.perform(post("/api/signal")
                        .param("signal", "1"))
                .andExpect(status().isOk());

        verify(signalHandler).handleSignal(1);

        // performing post call to "/api/signal" with param signal = 2
        mockMvc.perform(post("/api/signal")
                        .param("signal", "2"))
                .andExpect(status().isOk());

        verify(signalHandler).handleSignal(2);

        // performing post call to "/api/signal" with param signal = 1 once again
        mockMvc.perform(post("/api/signal")
                        .param("signal", "1"))
                .andExpect(status().isOk());

        // verifying that  post call to "/api/signal" caused handleSignal to be called 2 times
        verify(signalHandler, times(2)).handleSignal(1);

        // when requesting without signal returns bad requests
        mockMvc.perform(post("/api/signal"))
                .andExpect(status().isBadRequest());

        //then verifying that signalHandler has not been touched
        verifyNoMoreInteractions(signalHandler);
    }
}
