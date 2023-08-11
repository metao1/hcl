package com.example.hcl;

import java.util.function.Consumer;

/**
 * Actions that can be performed on an Algo
 * <p>
 * each action is associated with a method in Algo
 * </p>
 */
public enum Action {
    DOALGO(Algo::doAlgo),
    CANCELTRADES(Algo::cancelTrades),
    REVERSE(Algo::reverse),
    SUBMITTOMARKET(Algo::submitToMarket),
    PERFORMCALC(Algo::performCalc),
    SETUP(Algo::setUp);

    private final Consumer<Algo> command;

    Action(Consumer<Algo> command) {
        this.command = command;
    }

    // This method is used in SignalApiController to convert the String received from the HTTP request into an Action
    public static Action fromString(String action) {
        return Action.valueOf(action.toUpperCase());
    }

    public Consumer<Algo> getCommand() {
        return command;
    }
}
