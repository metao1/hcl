package com.example.hcl;

import com.example.hcl.exception.ConfigurationException;
import com.example.hcl.exception.UnknownActionException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * This is your team’s code and should be changed as you see fit.
 * <p>
 * This service is the entry point for the trading system.
 * </p>
 * It is called by the trading system when a signal is received.
 * It handles the signal by executing the actions specified in the configuration file.
 */
@Service
class TradingApplicationService implements SignalHandler {

    private final SignalConfig signalConfigService;
    private final Algo algo;

    public TradingApplicationService(Algo algo, SignalConfig signalConfig) {
        this.signalConfigService = signalConfig;
        this.algo = algo;
    }

    /**
     * This handles incoming signals from the trading system.
     * <p>
     * It is called by the trading system when a signal is received.
     * </p>
     *
     * @param signal the signal received from the trading system
     * @throws UnknownActionException if the signal is not configured or found
     */
    @Override
    public void handleSignal(int signal) throws UnknownActionException, ConfigurationException {
        // get signal actions from configurations
        Map<String, List<String>> signalActionMap = signalConfigService.getSignalActions();
        var s = String.valueOf(signal);
        if (signalActionMap.containsKey(s)) { // check if signal is known
            signalActionMap.get(s).forEach(this::executeAction); // execute actions related to that signal
        }
        // these two actions are always executed by default regardless of the other signals
        algo.cancelTrades();
        algo.doAlgo();
    }

    /**
     * This method parses each action and then executes it.
     *
     * @param action the action to be executed
     * @throws UnknownActionException         if the action is not configured or found
     * @throws ArrayIndexOutOfBoundsException if the action has a missed param e.g. setAlgoParam(1)
     * @throws NumberFormatException          if the action has param that is not in number format e.g. setAlgoParam(1, a)
     */
    private void executeAction(String action) throws UnknownActionException, ConfigurationException {
        // setAlgoParam action is a special case
        try {
            if (action.startsWith("setAlgoParam")) {
                // setAlgoParam action has two parameters (param, value) separated by comma
                String[] parts = action.replaceAll("[^\\d,]", "").split(",");
                int param = Integer.parseInt(parts[0].trim());
                int value = Integer.parseInt(parts[1].trim());
                algo.setAlgoParam(param, value);

            } else {
                var actionMethod = Action.fromString(action);
                if (actionMethod != null) {
                    actionMethod.getCommand().accept(algo);
                } else {
                    // handle unknown action
                    throw new UnknownActionException(String.format("Unknown action: %s", action));
                }
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            throw new ConfigurationException(String.format("Action: %s is not configured correctly", action));
        }
    }

}