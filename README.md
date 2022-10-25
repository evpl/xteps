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

## How to use

Requires Java 8+ version. Just add suitable dependency.

## Which version to choose?

* If you use Kotlin or use Java but want to hide checked exceptions
    * with Allure - `unchecked-xteps-allure`
    * with ReportPortal - `unchecked-xteps-reportportal`
    * with custom reporting system - `unchecked-xteps`
* If you use Java and want to work with checked exceptions (and maybe sometimes hide them)
    * with Allure - `xteps-allure`
    * with ReportPortal - `xteps-reportportal`
    * with custom reporting system - `xteps`

## What are steps chains?

This is a way of writing steps in a chain. The most suitable situation is when each step is built around one specific
context. This method has many advantages, like:
* Chaining reads better than disparate static method steps
* Unnecessary variables are not created
* It is possible to set additional behavior of the chain like safe AutoCloseable contexts

Xteps provides both options for writing steps, static methods and steps chains.

## API

Main Xteps API is a set of static methods located in the `com.plugatar.xteps.checked.Xteps`
or `com.plugatar.xteps.checked.UncheckedXteps` depending on the version.

- `step(String)` and `step(String, String)` - performs empty step with given name (and description).

```java
step("Step 1");
step("Step 2","Description");
```

- `step(String, ThrowingRunnable)` and `step(String, String, ThrowingRunnable)` - performs given step with given name
(and description).

```java
step("Step 1", () -> {
    //...
});
step("Step 2", "Description", () -> {
    //...
    step("Nested step 1", () -> {
        //...
    });
});
```

- `stepTo(String, ThrowingSupplier)` and `stepTo(String, String, ThrowingSupplier)` - performs given step with given
name (and description) and returns the step result.

```java
String step1Result = stepTo("Step 1", () -> {
    //...
    return"result1";
});
String step2Result = stepTo("Step 2", "Description", () -> {
    //...
    return stepTo("Nested step 1", () -> {
        //...
        return"result2";
    });
});
```

- `stepsChain()` - returns no context steps chain.

```java
stepsChain()
    .step("Step 1", () -> {
        //...
    })
    .nestedSteps("Step 2", stepsChain -> stepsChain
        .step("Nested step 1", "Description", () -> {
            //...
        })
        .step("Nested step 2", () -> {
            //...
        })
    );
    stepsChain().withContext("context")
        .step("Step 3", ctx -> {
            //...
        })
        .nestedSteps("Step 4", stepsChain -> stepsChain
            .step("Nested step 1", "Description", ctx -> {
                //...
            })
            .step("Nested step 2", ctx -> {
                //...
            })
        );
```

## Allure / ReportPortal integration

Set up your project with Allure / ReportPortal and then just add suitable Xteps dependency.

## Safe AutoCloseable context

Don't worry about closing resources, just use steps chain. Context will be closed in case of any exception in
steps chain or in case of `closeAutoCloseableContexts()` method invocation.

```java
stepsChain().withContext(new AutoCloseableImpl1())
    .contextIsAutoCloseable()
    .step("Step 1", ctx -> {
        //...
    })
    .stepToContext("Step 2", ctx -> {
        //...
        return new AutoCloseableImpl2(ctx);
    })
    .contextIsAutoCloseable()
    .step("Step 3", ctx -> {
        //...
    })
    .closeAutoCloseableContexts();
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
        })
    });
}
```

You can choose a mechanism to cheat checked exceptions if you want to hide them. Xteps give you the built-in static
methods by `com.plugatar.xteps.base.ThrowingConsumer`, `com.plugatar.xteps.base.ThrowingFunction`,
`com.plugatar.xteps.base.ThrowingPredicate`, `com.plugatar.xteps.base.ThrowingRunnable`,
`com.plugatar.xteps.base.ThrowingSupplier` classes.

```java
void test() {
    step("Step", () -> {
        //...
        step("Nested step", ThrowingRunnable.unchecked(() -> {
            new URL("abc");
        }))
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
        })
    });
}
```

## How to provide parameters

There are two ways to load parameters. Be aware that higher source override lower one - properties from file can be
overridden by system properties.

| Order | Source                               |
|-------|--------------------------------------|
| 1     | System properties                    |
| 2     | Properties file (`xteps.properties`) |

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
    .nestedStepsTo("Step 1", stepsChain -> stepsChain
        .step("Nested step 1", () -> {})
    );
```

This code can fail to build with this
exception `java: unreported exception java.lang.Throwable; must be caught or declared to be thrown`.

You can switch to Java 9+ or use functional interfaces static methods to hide any throwables.

```java
stepsChain()
    .nestedStepsTo("Step 1", uncheckedFunction(stepsChain -> stepsChain
        .step("Nested step 1", () -> {})
    ));
```

## Simple REST Assured example

```java
final class ExampleTest {

    @Test
    void test() {
        POJO pojo = stepsChain()
            .stepToContext("Step 1 - Request", () ->
                RestAssured.given().get("https://....")
            )
            .step("Step 2 - Status code", response ->
                response.then().statusCode(200)
            )
            .step("Step 3 - Schema", response ->
                response.then().body(matchesJsonSchemaInClasspath("schema.json"))
            )
            .stepTo("Step 4 - POJO", response -> {
                POJO responsePOJO = response.as(POJO.class);
                verifyPOJO(responsePOJO);
                return responsePOJO;
            });

        stepsChain()
            .stepToContext("Step 5 - Database connection", () ->
                Database.connection()
            )
            .contextIsAutoCloseable()
            .step("Step 6 - Verify database", connection ->
                compareResponsePOJOWithDatabase(pojo, connection)
            )
            .closeAutoCloseableContexts();
    }
}
```
