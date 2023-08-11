# Trading Application

This project is a trading application that executes a trading algorithm when given a signal. The trading algorithm is provided by an Algo team in the form of a library containing the required software. The `SignalHandler` and `Algo` classes belong to the Trading Algo library and cannot be modified.

## Table of Contents
- [Requirements](#requirements)
- [Task](#task)
- [Run the Application](#run-the-application)
- [Architecture decisions](#architecture-decisions)
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
## Architecture decisions
It sounds like the team is expecting a high volume of new signals to be added to the TradingApplication code once it is in production. 
With up to 50 new signals being added per month, it’s important to have a scalable and maintainable system in place to handle this growth.

One approach could be to use a modular design, where each signal is implemented as a separate module. 
This way, when a new signal specification is given, a new module can be created and added to the system without affecting the existing code. This also makes it easier to test each signal individually.

Another approach could be to use a data-driven design, where the behavior of the system is determined by data rather than hard-coded logic. This can make it easier to add new signals, as the behavior of the system can be changed by updating the data rather than modifying the code.
For example, we could define a set of actions that can be performed by the Algo class, and then use a configuration file or database to specify which actions should be performed for each signal. When a new signal is received, the system would look up the corresponding actions in the data and execute them.
### Configuration in a file

Pros
- Simple and easy to implement
- Easy to test
Cons:
- Need to redeploy the application when a new signal is added
- Configuration is not consistent across all instances
- Configuration is not versioned
- Configuration is not auditable
### Configuration in a database

Pros
- Configuration is consistent across all instances
- No redeployment required when a new signal is added
- Configuration is versioned
- Configuration is auditable
Cons:
- More complex to implement
- More costly to maintain

I have chosen to use a data-driven design for this project because it is a simple and flexible approach that can be easily extended to handle new signals.

For sake of simplicity we only use configuration file here, but we can easily extend it to use a database instead in the future.

For code implementation, I have chosen to use Spring Boot to build the application because it is a popular framework that is easy to use and has a lot of community support. It also provides a lot of features out of the box such as dependency injection, configuration, and logging. 


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
### Project scaling 
We can run multiple cloned instance of the project.
We can use a load balancer to distribute the load between the instances.
Each instance will have its own configuration file but the content should be similar to expect the same behavior of the app.
It is important to roll update the instances one by one to avoid any downtime at the sametime
guarantee the consistency in data behavior.

In future when using database, we need to add configuration with care.
As the database is shared in this case. Although it gives us flexibility to update the configuration without restarting the app, it is important to make sure that the configuration is consistent across all instances.


## Testing
The project has unit tests for the `SignalConfig` and `TradingApplicationService` classes. The tests are located in the `src/test/java` directory and can be run using `mvn test`.