
package com.example.hcl;

import com.example.hcl.exception.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingApplicationTest {

    private final Algo algo = mock(Algo.class);
    private final SignalConfig signalConfig = mock(SignalConfig.class);
    private final SignalHandler signalHandler = new TradingApplicationService(algo, signalConfig);

    @Test
    void testHandleSignalScenarios() {
        // Set up test data
        int signal = 1;
        Map<String, List<String>> actions;
        actions = Map.of(String.valueOf(signal), List.of("setUp", "setAlgoParam(1, 60)", "performCalc", "submitToMarket"));
        when(signalConfig.getSignalActions())
                .thenReturn(actions);

        // Call the method under test
        signalHandler.handleSignal(signal);

        verify(algo).setUp();
        verify(algo).setAlgoParam(1, 60);
        verify(algo).performCalc();
        verify(algo).submitToMarket();
        verify(algo).cancelTrades();
        verify(algo, times(1)).doAlgo();
        verify(signalConfig).getSignalActions();

        // Verify that no other interactions with the mocks occurred
        verifyNoMoreInteractions(signalConfig, algo);

        // Set up test data
        signal = 2;
        actions = Map.of(String.valueOf(signal), List.of("reverse", "setAlgoParam(1, 80)", "submitToMarket"));
        when(signalConfig.getSignalActions())
                .thenReturn(actions);

        // Call the method under test
        signalHandler.handleSignal(signal);

        verify(algo).reverse();
        verify(algo).setAlgoParam(1, 80);
        verify(algo, times(2)).cancelTrades();
        verify(algo, times(2)).submitToMarket();
        verify(algo, times(2)).doAlgo();
        verify(signalConfig, times(2)).getSignalActions();

        // Verify that no other interactions with the mocks occurred
        verifyNoMoreInteractions(signalConfig, algo);


        // Set up test data with malformed action
        // we expect a ConfigurationException to be thrown since the action is not in the correct format
        final int sig = 3;
        actions = Map.of(String.valueOf(sig), List.of("setAlgoParam(180)"));
        when(signalConfig.getSignalActions())
                .thenReturn(actions);

        // Call the method under test
        assertThrows(ConfigurationException.class, () -> signalHandler.handleSignal(sig));

        verifyNoMoreInteractions(algo);

        // Set up test data with malformed action
        // we expect a ConfigurationException to be thrown since the action is not in the correct format
        final int sig2 = 4;
        actions = Map.of(String.valueOf(sig2), List.of("setAlgoParam(180"));
        when(signalConfig.getSignalActions())
                .thenReturn(actions);

        // Call the method under test
        assertThrows(ConfigurationException.class, () -> signalHandler.handleSignal(sig2));

        verifyNoMoreInteractions(algo);

        // Set up test data with malformed action
        // we expect a ConfigurationException to be thrown since the action is not in the correct format
        final int sig3 = 5;
        actions = Map.of(String.valueOf(sig3), List.of("setAlgoParam(180, \"a\")"));
        when(signalConfig.getSignalActions())
                .thenReturn(actions);

        // Call the method under test
        assertThrows(ConfigurationException.class, () -> signalHandler.handleSignal(sig3));

        verifyNoMoreInteractions(algo);
    }
}