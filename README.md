# Xteps

*High-level contextual steps in your tests for any reporting tool*

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Hits-of-Code](https://hitsofcode.com/github/evpl/xteps?branch=master)](https://hitsofcode.com/github/evpl/xteps/view?branch=master)
[![Lines of code](https://img.shields.io/tokei/lines/github/evpl/xteps)](https://en.wikipedia.org/wiki/Source_lines_of_code)

|  | Maven Central | Javadoc | Readme |
| --- | --- | --- | --- |
| Xteps              | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps) | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps) |
| Xteps Allure       | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure) | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-allure/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-allure) | [![README](https://img.shields.io/badge/readme-Xteps%20Allure-brightgreen.svg)](https://github.com/evpl/xteps/blob/master/xteps-allure/README.md) |
| Xteps ReportPortal | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal) | [![javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-reportportal/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-reportportal) | [![README](https://img.shields.io/badge/readme-Xteps%20ReportPortal-brightgreen.svg)](https://github.com/evpl/xteps/blob/master/xteps-reportportal/README.md) |

## How to use

The library has no dependencies. Requires Java 8+ version.

If you are using Allure then just add the [Allure integration dependency](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure)
([README](https://github.com/evpl/xteps/blob/master/xteps-allure/README.md)).
For ReportPortal use [ReportPortal integration dependency](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal)
([README](https://github.com/evpl/xteps/blob/master/xteps-reportportal/README.md)).

If you want to use Xteps with a custom step reporting system add a [dependency](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps)
first. Then add listeners via Service Provider Interface or via properties. You’re all set! Now you can use Xteps.

## API
Xteps API is a set of static methods located in the `com.plugatar.xteps.Xteps`.
- `step(String)` and `step(String, String)` - performs empty step with given name (and description).
```java
step("Step 1");
step("Step 2", "Description");
```
- `step(String, ThrowingRunnable)` and `step(String, String, ThrowingRunnable)` - performs given step with given name (and description).
```java
step("Step 1", () -> {
    ...
});
step("Step 2", "Description", () -> {
    ...
    step("Nested step 1", () -> {
        ...
    });
});
```
- `stepTo(String, ThrowingSupplier)` and `stepTo(String, String, ThrowingSupplier)` - performs given step with given name (and description) and returns the step result.
```java
String step1Result = stepTo("Step 1", () -> {
    ...
    return "result1";
});
String step2Result = stepTo("Step 2", "Description", () -> {
    ...
    return stepTo("Nested step 1", () -> {
        ...
        return "result2";
    });
});
```
- `stepsChain()` - returns initial steps chain.
```java
stepsChain()
    .step("Step 1", () -> {
        ...
    })
    .nestedSteps("Step 2", stepsChain -> stepsChain
        .step("Nested step 1", "Description",() -> {
            ...
        })
        .step("Nested step 2", () -> {
            ...
        })
    );
stepsChain().withContext("context")
    .step("Step 3", ctx -> {
        ...
    })
    .nestedSteps("Step 4", stepsChain -> stepsChain
        .step("Nested step 1", "Description", ctx -> {
            ...
        })
        .step("Nested step 2", ctx -> {
            ...
        })
    );
```

## Safe AutoCloseable context
Don't worry about closing resources, just use steps chain. Context will be closed in case of any exception in
steps chain or in case of `closeAutoCloseableContexts()` method invocation.
```java
stepsChain().withContext(new AutoCloseableImpl1())
    .contextIsAutoCloseable()
    .step("Step 1", ctx -> {
        ...
    })
    .stepToContext("Step 2", ctx -> {
        ...
        return new AutoCloseableImpl2();
    })
    .contextIsAutoCloseable()
    .step("Step 3", ctx -> {
        ...
    })
    .closeAutoCloseableContexts();
```

## Checked exceptions

Xteps is tolerant to checked exceptions. Each method allows you to throw generic exception, the type of this exception
is defined by the step body.

```java
@Test
void test() throws MalformedURLException {
    step("Step 1", () -> {
        new URL("abc");
    });
}
```

You can choose a mechanism to cheat checked exceptions if you don't like them. Xteps give you the built-in static
methods using `com.plugatar.xteps.Unchecked` class.

```java
@Test
void test() {
    step("Step 1", uncheckedRunnable(() -> {
        new URL("abc");
    }));
}
```

## How to provide parameters

There are two ways to load parameters. Be aware that higher source override lower one - properties from file can be
overridden by system properties.

| Order | Source                               |
| ----- | ------------------------------------ |
| 1     | System properties                    |
| 2     | Properties file (`xteps.properties`) |

### Properties list

| Name | Type | Default value | Description |
| --- | --- | --- | --- |
| xteps.enabled   | Boolean | `true` | Enable/disable steps logging. |
| xteps.spi       | Boolean | `true` | Enable/disable Service Provider Interface mechanism to detect and instantiate `com.plugatar.xteps.core.StepListener` implementations. Implementations should have zero-argument public constructor. |
| xteps.listeners | String  |        | List of `com.plugatar.xteps.core.StepListener` implementations names in `Class#getTypeName()` format. Names should be separated by `,`. Implementations should have zero-argument public constructor. |

### Examples

Maven test run command example:

`mvn test -Dxteps.enabled=true -Dxteps.spi=true -Dxteps.listeners="com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2"`

xteps.properties file example:

```properties
xteps.enabled=true
xteps.spi=true
xteps.listeners=com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2
```

## Java 8 unreported exception bug

You may run into a problem if you use Java 8. The problem is caused by generic exceptions.

```java
stepsChain()
    .nestedStepsTo("Step 1", stepsChain -> stepsChain
        .step("Nested step 1", () -> {})
    );
```

This code can fail to build with this exception `java: unreported exception java.lang.Throwable; must be caught or declared to be thrown`.

You can switch to Java 9+ or use `com.plugatar.xteps.Unchecked` static methods to hide any throwables.

```java
stepsChain()
    .nestedStepsTo("Step 1", uncheckedFunction(stepsChain -> stepsChain
        .step("Nested step 1", () -> {}))
    );
```
