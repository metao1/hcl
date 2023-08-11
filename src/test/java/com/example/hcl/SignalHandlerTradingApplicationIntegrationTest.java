package com.example.hcl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Import({TradingApplicationService.class, SignalConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SignalHandlerTradingApplicationIntegrationTest {

    @MockBean
    Algo algo;

    @Autowired
    MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testWhenSendingConfiguredSignalsThenHandlesSuccessfully() {
        // call to signal 1 should cause interaction with all methods configured in configuration.json file associated with it
        mockMvc.perform(post("/api/signal")
                        .param("signal", "1"))
                .andExpect(status().isOk());

        // verifying expected interaction with the algo is happening
        verify(algo).setUp();
        verify(algo).submitToMarket();
        verify(algo).setAlgoParam(1, 60);
        verify(algo).performCalc();
        verify(algo).doAlgo();

        // call to signal 2 should cause interaction with all methods configured in configuration.json file associated with it
        mockMvc.perform(post("/api/signal")
                        .param("signal", "2"))
                .andExpect(status().isOk());

        // verifying expected interaction with the algo is happening
        verify(algo).reverse();
        verify(algo).setAlgoParam(1, 80);
        verify(algo, times(1)).performCalc();
        verify(algo, times(2)).doAlgo();
        verify(algo, times(2)).cancelTrades();
    }

    @Test
    @SneakyThrows
    void testWhenSendingUnconfirmedSignalsThenHandlesWithoutInteractionWithOtherActions() {
        // call to signal 3 that does not exist in the algo should not cause any interaction with the algo amd raise 404
        mockMvc.perform(post("/api/signal")
                        .param("signal", "4"))
                .andExpect(status().isOk());

        // verifying the default behavior of the algo is happening
        // no interaction with the other algo methods except the default ones
        verify(algo).doAlgo();
        verify(algo).cancelTrades();

        // verifying no other interaction with the algo
        verifyNoMoreInteractions(algo);
    }

}
