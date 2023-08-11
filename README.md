# Trading Application

This project is a trading application that executes a trading algorithm when given a signal. The trading algorithm is provided by an Algo team in the form of a library containing the required software. The `SignalHandler` and `Algo` classes belong to the Trading Algo library and cannot be modified.

## Table of Contents
- [Requirements](#requirements)
- [Task](#task)
- [Run the Application](#run-the-application)
- [Classes](#classes)
    - [Exceptions](#exceptions)
    - [Action](#action)
    - [SignalApiController](#signalapicontroller)
    - [SignalConfig](#signalconfig)
    - [TradingApplicationService](#tradingapplicationservice)
- [Algorithm Complexity](#algorithm-complexity)
- [Project Extension](#project-extension)
- [Testing](#testing)

## Requirements
Your team has written the `TradingApplication` code to process signals (simple integers) specified by your analysts in the form of JIRA tickets. Your team's role is to implement new signal specifications and release them to production as quickly as possible. While the current `TradingApplication` code only has three signals, it is expected that up to 50 new signals will be added per month once in production.

## Task
The task is to make changes to your team's code to make it easier to understand, debug, maintain, and add new signals. The code should also be easier to test and have appropriate levels of testing to ensure that the stated requirements are met.

The code should be a running service with a single HTTP endpoint for receiving signals, which will then be passed through the `SignalHandler` interface and onto your application.

## Run the Application
### Prerequisites
- Java 11 or higher
- Maven 3.6.3 or higher

### Steps
1. Clone the repository
2. Run `mvn clean install` to build the project
3. Run `mvn spring-boot:run` to run the application
4. Send a POST request to `http://localhost:8080/api/signal?signal=1` to execute the actions for signal 1 using curl or Postman for example.
   ``` 
   curl -X POST http://localhost:8080/api/signal?signal=1 
   ```

## Classes
### Exceptions
- `ConfigurationException`: This is a custom exception class that extends `RuntimeException`. It is annotated with `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)` to indicate that it should return an HTTP 500 Internal Server Error response when thrown. The constructor takes a `message` parameter and logs it as an error using the `Slf4j` logger.
- `UnknownSignalException`: This is another custom exception class that extends `RuntimeException`. It is annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)` to indicate that it should return an HTTP 404 Not Found response when thrown. The constructor takes a `message` parameter and logs it as an error using the `Slf4j` logger.

### Action
This is an enum that defines the actions that can be performed on an `Algo` object. Each action is associated with a method in the `Algo` class using a `Consumer<Algo>` command. The enum also provides a static method `fromString(String action)` to convert a string into an `Action` object.

### SignalApiController
This is a controller class that handles incoming signals. It has a single HTTP POST endpoint `/api/signal` that takes an integer signal as a request parameter and passes it to the `SignalHandler` interface for processing.

### SignalConfig
This is a configuration class that parses a configuration file and returns a map of signals to lists of actions. It implements the `InitializingBean` interface and overrides the `afterPropertiesSet()` method to parse the configuration file after the bean is created and its properties are set.
The `SignalConfig` class will parse the new signal and the `TradingApplicationService` class will execute the new action when the signal is received.

### TradingApplicationService
This is a service class that serves as the entry point for the trading system. It implements the `SignalHandler` interface and is called by the trading system when a signal is received. The service handles the signal by executing the actions specified in the configuration file.

The class has two instance variables: `signalConfigService` and `algo`. `signalConfigService` is an instance of `SignalConfig` that is used to retrieve the signal actions from the configuration file. `algo` is an instance of `Algo` that represents the trading algorithm to be executed.

The `handleSignal(int signal)` method takes an integer signal as a parameter and throws `UnknownSignalException` and `ConfigurationException` if the signal is not configured or found. The method retrieves the signal actions from the configuration file using `signalConfigService.getSignalActions()`, checks if the signal is known, and executes the actions related to that signal using the `executeAction(String action)` method. The method also always executes two default actions: `algo.cancelTrades()` and `algo.doAlgo()`.

The `executeAction(String action)` method takes a string action as a parameter and throws `UnknownSignalException`, `ConfigurationException`, `ArrayIndexOutOfBoundsException`, and `NumberFormatException` if the action is not configured or found, or if it has missing or invalid parameters. The method parses each action and executes it using either the `Algo.setAlgoParam(int param, int value)` method for the special case of the "setAlgoParam" action, or by converting the action string into an `Action` object using `Action.fromString(action)` and calling its associated command on the `algo` object.

## Algorithm Complexity
The complexity of the algorithm is O(n) where n is the number of actions to be executed. The algorithm iterates over the list of actions and executes each action using the `executeAction(Action action)` method in runtime.

## Project Extension
The project can be extended by adding a new signal to the configuration file in the following format:

```json
"signal": [
    "action1",
    "action2",
    "action3"
]
```
If any new method is introduced in the `Algo` library, we need to add this action to the `Action` enum like so:

```java
NEW_ACTION("newAction", Algo::newAction)
```


## Testing
The project has unit tests for the `SignalConfig` and `TradingApplicationService` classes. The tests are located in the `src/test/java` directory and can be run using `mvn test`.