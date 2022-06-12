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
***

Add a dependency first. Then add listeners via Service Provider Interface or via properties. You’re all set! Now you
can use Xteps.

## Code example

```java
import static com.plugatar.xteps.Xteps.step;
import static com.plugatar.xteps.Xteps.stepTo;
import static com.plugatar.xteps.Xteps.stepsChainOf;

final class ExampleTest {

    @Test
    void simpleExample() {
        step("Step 1", () -> {
            ...
        });
        step("Step 2");
        step("Step 3", () -> {
            step("Inner step 1", () -> {
                ...
            });
            step("Inner step 2", () -> {
                ...
            });
        });
        final String step4Result = stepTo("Step 4", () -> {
            ...
            return "result";
        });
        final String step5Result = stepTo("Step 5", () -> {
            step("Inner step 1", () -> {
                ...
            });
            return stepTo("Inner step 2", () -> {
                ...
                return "result";
            });
        });
        step("Step 6", () -> assertEquals(step4Result, step5Result));
    }

    @Test
    void chainExample() {
        stepsChainOf("context")
            .step("Step 1", ctx -> {
                ...
            })
            .stepToContext("Step 2", ctx -> 111)
            .step("Step 3", newCtx -> {
                ...
            })
            .previousStepsChain()
            .step("Step 4", previousCtx -> assertEquals(previousCtx, "context"));
    }
}
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
import static com.plugatar.xteps.core.util.function.Unchecked.uncheckedRunnable;

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

| Property name            | Type    | Default value | Description                                                                         |
| ------------------------ | ------- | ------------- | ----------------------------------------------------------------------------------- |
| xteps.enabled            | Boolean | `true`        | Enable/disable steps logging                                                        |
| xteps.spi                | Boolean | `true`        | Enable/disable Service Provider Interface mechanism to detect and instantiate `com.plugatar.xteps.core.StepListener` implementations. Implementations should have zero-argument public constructor. |
| xteps.listeners          | String  |               | List of `com.plugatar.xteps.core.StepListener` implementations names in `Class#getTypeName()` format. Names should be separated by `,`. Implementations should have zero-argument public constructor. |

### Examples

Maven test run command example:

`mvn test -Dxteps.enabled=true -Dxteps.spi=true -Dxteps.listeners="com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2"`
***

xteps.properties file example:

```properties
xteps.enabled=true
xteps.spi=true
xteps.listeners=com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2
```

## Java 8 unreported exception bug

You may run into a problem if you use Java 8. The problem is caused by generic exceptions.

```java
Xteps.nestedStepsTo("Step 1", steps -> steps
    .step("Inner step 1", () -> {})
);
```

This code can fail to build with this
exception `java: unreported exception java.lang.Throwable; must be caught or declared to be thrown`.

You can switch to Java 9+ or use `com.plugatar.xteps.Unchecked` static methods to hide any
throwables.

```java
import static com.plugatar.xteps.Unchecked.uncheckedFunction;

Xteps.nestedStepsTo("Step 1", uncheckedFunction(steps -> steps
    .step("Inner step 1", () -> {})
));
```
