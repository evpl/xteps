# Xteps

*High-level contextual steps in your tests for any reporting tool*

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Hits-of-Code](https://hitsofcode.com/github/evpl/xteps?branch=master)](https://hitsofcode.com/github/evpl/xteps/view?branch=master)
[![Lines of code](https://img.shields.io/tokei/lines/github/evpl/xteps)](https://en.wikipedia.org/wiki/Source_lines_of_code)

|                              | Maven Central                                                                                                                                                                                                                    | Javadoc                                                                                                                                                                     |
|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| xteps                        | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps)                                               | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps)                                               |
| xteps-allure                 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure)                                 | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-allure/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-allure)                                 |
| xteps-reportportal           | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal)                     | [![javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-reportportal/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-reportportal)                     |
| unchecked-xteps              | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps)                           | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/unchecked-xteps/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps)                           |
| unchecked-xteps-allure       | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-allure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-allure)             | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/unchecked-xteps-allure/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-allure)             |
| unchecked-xteps-reportportal | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-reportportal/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-reportportal) | [![javadoc](https://javadoc.io/badge2/com.plugatar.xteps/unchecked-xteps-reportportal/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-reportportal) |

Xteps is a library that provides a convenient way to report test steps. Xteps allows you to write code in
[_Step objects_ and _Step objects chain_ patterns](https://github.com/evpl/step-objects-pattern) style. Integrations
with Allure and ReportPortal are ready, but you can write your own listener for another reporting tool or just
create an issue.

## Table of Contents

* [How to use](#How-to-use)
* [Which version to choose](#Which-version-to-choose)
* [API](#API)
* [Allure and ReportPortal integration](#Allure-and-ReportPortal-integration)
* [Safe AutoCloseable context](#Safe-AutoCloseable-context)
* [Clean stack trace](#Clean-stack-trace)
* [Checked exceptions](#Checked-exceptions)
* [How to provide parameters](#How-to-provide-parameters)
* [Xteps Java 8 unreported exception bug](#Xteps-Java-8-unreported-exception-bug)
* [Code examples](#Code-examples)
    * [Simple Java code example](#Simple-Java-code-example)
    * [Selenium WebDriver Java code example](#Selenium-WebDriver-Java-code-example)
    * [_Step objects_ and _Step objects
      chain_ patterns example](#_Step-objects_-and-_Step-objects-chain_-patterns-example)

## How to use

Requires Java 8+ version. Just add suitable dependency.

## Which version to choose

* If you use Kotlin or use Java but want to hide checked exceptions
    * with Allure - `unchecked-xteps-allure`
    * with ReportPortal - `unchecked-xteps-reportportal`
    * with custom reporting tool - `unchecked-xteps`
* If you use Java and want to work with checked exceptions (and maybe sometimes hide them)
    * with Allure - `xteps-allure`
    * with ReportPortal - `xteps-reportportal`
    * with custom reporting tool - `xteps`

## API

First part of Xteps API is a set of static methods located in the `com.plugatar.xteps.checked.Xteps` or
`com.plugatar.xteps.unchecked.UncheckedXteps` classes depending on the version.

| Method                                     | Description                                                                                  |
|--------------------------------------------|----------------------------------------------------------------------------------------------|
| `step(String)`                             | Performs and reports empty step with given name.                                             |
| `step(String, String)`                     | Performs and reports empty step with given name and description.                             |
| `step(RunnableStep)`                       | Performs and reports given step.                                                             |
| `step(String, ThrowingRunnable)`           | Performs and reports given step with given name.                                             |
| `step(String, String, ThrowingRunnable)`   | Performs and reports given step with given name and description.                             |
| `stepTo(SupplierStep)`                     | Performs and reports given step and returns the step result.                                 |
| `stepTo(String, ThrowingSupplier)`         | Performs and reports given step with given name and returns the step result.                 |
| `stepTo(String, String, ThrowingSupplier)` | Performs and reports given step with given name and description and returns the step result. |
| `stepsChain()`                             | Starts a chain of steps (returns no context steps chain).                                    |

Second part is a set of steps objects located in the `com.plugatar.xteps.checked` or `com.plugatar.xteps.unchecked`
packages depending on the version.

| Step object       | Description                                                                       |
|-------------------|-----------------------------------------------------------------------------------|
| `RunnableStep`    | The step will be executed and reported when calling the `run()` method.           |
| `SupplierStep`    | The step will be executed and reported when calling the `get()` method.           |
| `ConsumerStep`    | The step will be executed and reported when calling the `accept(T)` method.       |
| `BiConsumerStep`  | The step will be executed and reported when calling the `accept(T, U)` method.    |
| `TriConsumerStep` | The step will be executed and reported when calling the `accept(T, U, V)` method. |
| `FunctionStep`    | The step will be executed and reported when calling the `apply(T)` method.        |
| `BiFunctionStep`  | The step will be executed and reported when calling the `apply(T, U)` method.     |
| `TriFunctionStep` | The step will be executed and reported when calling the `apply(T, U, V)` method.  |

See [code examples](#Code-examples).

## Allure and ReportPortal integration

Set up your project with Allure / ReportPortal and then just add suitable Xteps dependency.

## Safe AutoCloseable context

Don't worry about closing resources, just use steps chain. Context will be closed in case of any exception in
steps chain or in case of `closeAutoCloseableContexts()` method invocation.

```java
class AutoCloseableImpl implements AutoCloseable {

    @Override
    public void close() {
        System.out.println("AutoCloseableImpl is closed");
    }
}

class NotAutoCloseableImpl {

    public void quit() {
        System.out.println("NotAutoCloseableImpl is closed");
    }
}

class ExampleTest {

    @Test
    void test() {
        stepsChain().withContext(new AutoCloseableImpl())
            .contextIsCloseable()
            .step("Step 1", ctx -> {
                //...
            })
            .stepToContext("Step 2", ctx -> {
                //...
                return new NotAutoCloseableImpl();
            })
            .contextIsCloseable(context -> context.quit())
            .step("Step 3", ctx -> {
                //...
            })
            .closeCloseableContexts();
    }
}
```

## Clean stack trace

`cleanStackTrace` option is enabled by default. It allows to clear the stack trace from Xteps calls.

```java
final class ExampleTest {

    @Test
    void test() {
        step("Step", () -> {
            //...
            step("Nested step", () -> {
                //...
                throw new RuntimeException("Nested step exception");
            });
        });
    }
}
```

Stacktrace will look like this

```
java.lang.RuntimeException: Nested step exception

	at my.project.ExampleTest.lambda$null$0(ExampleTest.java:15)
	at my.project.ExampleTest.lambda$test$1(ExampleTest.java:13)
	at my.project.ExampleTest.test(ExampleTest.java:11)
	...
```

## Checked exceptions

Xteps is tolerant to checked exceptions. Each method allows you to throw generic exception, the type of this exception
is defined by the step body.

```java
@Test
void test() throws MalformedURLException {
    step("Step", () -> {
        //...
        step("Nested step", () -> {
            new URL("abc");
        });
    });
}
```

You can choose a mechanism to cheat checked exceptions if you want to hide them. Xteps give you the built-in static
methods by `com.plugatar.xteps.base.ThrowingConsumer`, `com.plugatar.xteps.base.ThrowingFunction`,
`com.plugatar.xteps.base.ThrowingRunnable`, `com.plugatar.xteps.base.ThrowingSupplier` classes.

```java
@Test
void test() {
    step("Step", () -> {
        //...
        step("Nested step", ThrowingRunnable.unchecked(() -> {
            new URL("abc");
        }));
    });
}
```

Unchecked Xteps just hide any exceptions.

```java
@Test
void test() {
    step("Step", () -> {
        //...
        step("Nested step", () -> {
            new URL("abc");
        });
    });
}
```

## How to provide parameters

There are two ways to load parameters. Be aware that higher source override lower one - properties from file can be
overridden by system properties.

| Priority | Source                               |
|----------|--------------------------------------|
| 1        | System properties                    |
| 2        | Properties file (`xteps.properties`) |

### Properties list

| Name                  | Type    | Default value | Description                                                                                                                                                                                                   |
|-----------------------|---------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| xteps.enabled         | Boolean | `true`        | Enable/disable steps logging.                                                                                                                                                                                 |
| xteps.spi             | Boolean | `true`        | Enable/disable Service Provider Interface mechanism to detect and instantiate `com.plugatar.xteps.checked.core.StepListener` implementations. Implementations should have zero-argument public constructor.   |
| xteps.listeners       | String  |               | List of `com.plugatar.xteps.checked.core.StepListener` implementations names in `Class#getTypeName()` format. Names should be separated by `,`. Implementations should have zero-argument public constructor. |
| xteps.cleanStackTrace | Boolean | `true`        | Removes all stack trace lines about Xteps from any exception except XtepsException.                                                                                                                           |

### Examples

xteps.properties file example:

```properties
xteps.enabled=true
xteps.spi=true
xteps.listeners=com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2
xteps.cleanStackTrace=true
```

## Xteps Java 8 unreported exception bug

You may run into a problem if you use Java 8. The issue is caused by generic exceptions (Unchecked Xteps doesn't have
this issue).

```java
stepsChain()
    .nestedStepsTo("Step", chain -> chain
        .step("Nested step", () -> {})
    );
```

This code can fail to build with this
exception `java: unreported exception java.lang.Throwable; must be caught or declared to be thrown`.

You can switch to Java 9+ or use functional interfaces static methods to hide any throwables.

```java
stepsChain()
    .nestedStepsTo("Step", ThrowingFunction.unchecked(chain -> chain
        .step("Nested step", () -> {})
    ));
```

## Code examples

### Simple Java code example

```java
@Test
void simpleMethodsExample() {
    step("Step 1");
    step("Step 2", "Description", () -> {
        //...
    });
    step("Step 3", () -> {
        step("Nested step 1", () -> {
            //...
        });
        step("Nested step 2", "Description");
    });
    final String stepResult = stepTo("Step 4", () -> {    // = "step result"
        step("Nested step 1", () -> {
            //...
        });
        return stepTo("Nested step 2", () -> {
            //...
            return "step result";
        });
    });
}

@Test
void stepsChainWithoutContextExample() {
    final String stepResult = stepsChain()    // = "step result"
        .step("Step 1")
        .step("Step 2", "Description", () -> {
            //...
        })
        .nestedSteps("Step 3", chain -> chain
            .step("Nested step 1", () -> {
                //...
            })
            .step("Nested step 2", "Description")
        )
        .nestedStepsTo("Step 4", chain -> chain
            .step("Nested step 1", () -> {
                //...
            })
            .stepTo("Nested step 2", () -> {
                //...
                return "step result";
            })
        );
}

@Test
void stepsChainWithContextExample() {
    final String stepResult = stepsChain()    // = "step result"
        .step("Step 1")
        .stepToContext("Step 2", "Description", () -> {
            //...
            return "step";
        })
        .nestedStepsTo("Step 3", chain -> chain
            .step("Nested step 1", ctx -> {
                //...
            })
            .stepToContext("Nested step 2", "Description", ctx -> {
                //...
                return " ";
            })
        )
        .withContext("result")
        .nestedStepsTo("Step 4", chain -> chain
            .step("Nested step 1", ctx -> {
                //...
            })
            .stepTo("Nested step 2", (ctx, previousCtx1, previousCtx2) -> {
                //...
                return previousCtx2 + previousCtx1 + ctx;
            })
        );
}
```

Allure report looks like this for every test.

![simple_java_code_example](https://user-images.githubusercontent.com/54626653/198648739-15ba8a27-4025-4902-8174-49baed3a69d2.png)

### _Step objects_ and _Step objects chain_ patterns example

Xteps allows you to write code in
[_Step objects_ and _Step objects chain_ patterns](https://github.com/evpl/step-objects-pattern) style.

```java
class RandomString extends SupplierStep<String, RuntimeException> {

    public RandomString(int length) {
        super("Generate random string with length = " + length, () -> {
            byte[] array = new byte[length];
            new Random().nextBytes(array);
            return new String(array, StandardCharsets.UTF_8);
        });
    }
}

class Wait extends RunnableStep<InterruptedException> {

    public Wait(int millis) {
        super("Wait for " + millis + " millis", () -> Thread.sleep(millis));
    }
}

class GetStringLength extends FunctionStep<String, Integer, RuntimeException> {

    public GetStringLength() {
        super("Get string length", "Full string: {context}", String::length);
    }
}

class AssertEquals<T> extends ConsumerStep<T, RuntimeException> {

    public AssertEquals(T expected) {
        super("Assert equals, actual = {context}, expected = " + expected, actual -> {
            if (!Objects.equals(actual, expected)) {
                throw new AssertionError("actual = " + actual + ", expected = " + expected);
            }
        });
    }
}

class ExampleTest {

    @Test
    void noChainTest() throws InterruptedException {
        int expectedStringLength = 10;
        String randomString = new RandomString(expectedStringLength).get();
        new Wait(1000).run();
        int actualStringLength = new GetStringLength().apply(randomString);
        new AssertEquals<>(expectedStringLength).accept(actualStringLength);
    }

    @Test
    void chainTest() throws InterruptedException {
        int expectedStringLength = 10;
        stepsChain()
            .stepToContext(
                new RandomString(expectedStringLength)
            )
            .step(
                new Wait(1000).asConsumerStep()
            )
            .stepToContext(
                new GetStringLength()
            )
            .step(
                new AssertEquals<>(expectedStringLength)
            );
    }
}
```

Allure report looks like this for both tests.

![step_objects_example](https://user-images.githubusercontent.com/54626653/200389156-241e5d10-2f00-4073-87f4-d6d33c7777ca.png)
