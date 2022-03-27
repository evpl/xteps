# Xteps

*High-level contextual steps in your tests for any reporting tool.*

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Hits-of-Code](https://hitsofcode.com/github/evpl/xteps?branch=master)](https://hitsofcode.com/github/evpl/xteps/view?branch=master)
[![Lines of code](https://img.shields.io/tokei/lines/github/evpl/xteps)](https://en.wikipedia.org/wiki/Source_lines_of_code)

|  | Maven Central | Javadoc |
| --- | --- | --- |
| Xteps              | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps) | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps) |
| Xteps Allure       | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure) | [![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-allure/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-allure) |
| Xteps ReportPortal | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal) | [![javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-reportportal/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-reportportal) |

## How to use

The library has no dependencies. Requires Java 8+ version.
***

Add a dependency first.

Maven:

```xml
<dependency>
    <groupId>com.plugatar.xteps</groupId>
    <artifactId>xteps</artifactId>
    <version>1.2</version>
    <scope>test</scope>
</dependency>
```

Gradle:

```groovy
dependencies {
    testRuntimeOnly 'com.plugatar:xteps:xteps:1.2'
}
```

If you don't use a multi-module Java project, you should add a listener dependency. Listener will be automatically found
using the Service Provider Interface mechanism.

For multi-module projects you need to add listener to `module-info.java` file. For example:
```
provides com.plugatar.xteps.core.StepListener with com.plugatar.xteps.allure.AllureStepListener;
```
Or just use property `xteps.listeners` to specify listeners.
***

### Allure listener

```xml
<dependency>
    <groupId>com.plugatar.xteps</groupId>
    <artifactId>xteps-allure</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```

Gradle:

```groovy
dependencies {
    testRuntimeOnly 'com.plugatar:xteps:xteps-allure:1.0'
}
```

### ReportPortal listener

```xml
<dependency>
    <groupId>com.plugatar.xteps</groupId>
    <artifactId>xteps-reportportal</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```

Gradle:

```groovy
dependencies {
    testRuntimeOnly 'com.plugatar:xteps:xteps-reportportal:1.0'
}
```
***

You’re all set! Now you can use Xteps.

## Code example

```java
import static com.plugatar.xteps.Xteps.emptyStep;
import static com.plugatar.xteps.Xteps.step;
import static com.plugatar.xteps.Xteps.stepTo;
import static com.plugatar.xteps.Xteps.stepsOf;

final class ExampleTest {

    @Test
    void simpleExample() {
        step("Step 1", () -> {
            ...
        });
        emptyStep("Step 2");
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
        stepsOf("context")
            .step("Step 1, context = {context.toUpperCase()}", ctx -> {
                ...
            })
            .stepToContext("Step 2", ctx -> 111)
            .step("Step 3, context = {0.shortValue()}, previous = {1.toUpperCase()}", newCtx -> {
                ...
            })
            .previous()
            .step("Step 4, context = {0.toUpperCase()}", previousCtx -> assertEquals(previousCtx, "context"));
    }
}
```

## Step name

If step chain contains context, you can use this context in the step name.
***

This code invokes `toString()` method on context and replaces `{context}` part with `(left value, right value)`, finally
step reports the name `Step 1 (left value, right value)`.

```java
stepsOf(Pair.of("left value","right value")).emptyStep("Step 1 {context}")
```
***

You can get field value (only public fields if `xteps.fieldForceAccess` property is `false`).

```java
stepsOf(Pair.of("left value","right value")).emptyStep("Step 1 {context.left}")
```

This code reports step with the name `Step 1 left value`.
***

You can get method (zero-argument) execution result (only public methods if `xteps.methodForceAccess` property
is `false`).

```java
stepsOf(Pair.of("left value","right value")).emptyStep("Step 1 {context.getRight()}")
```

This code reports step with the name `Step 1 right value`.
***

Method-field chain is unlimited.

```java
context.method1().field1.method2().method3().field2
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
methods using `com.plugatar.xteps.core.util.function.Unchecked` class.

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
| xteps.replacementPattern | String  | `\\{([^}]*)}` | The pattern that is used to replace pointers in the step name. Pattern should contain at least one group. Only the first group is used for replacement. (!) Don't forget to escape special symbols. |
| xteps.fieldForceAccess   | Boolean | `false`       | Tries to get field value even if this field is inaccessible.                        |
| xteps.methodForceAccess  | Boolean | `false`       | Tries to invoke method even if this method is inaccessible.                         |
| xteps.cleanStackTrace    | Boolean | `true`        | Removes all stack trace lines about Xteps from any exception except XtepsException. |
| xteps.useSPIListeners    | Boolean | `true`        | Enable/disable Service Provider Interface mechanism to detect and instantiate `com.plugatar.xteps.core.StepListener` implementations. Implementations should have zero-argument public constructor. |
| xteps.listeners          | String  |               | List of `com.plugatar.xteps.core.StepListener` implementations names in `Class#getTypeName()` format. Names should be separated by `,`. Implementations should have zero-argument public constructor. |

### Examples

Maven test run command example:

`mvn test -Denabled=true -DreplacementPattern="l([^r]*)r" -DfieldForceAccess=true -DmethodForceAccess=true -DcleanStackTrace=true -DuseSPIListeners=true -Dlisteners="com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2"`
***

xteps.properties file example:

```properties
xteps.enabled=true
xteps.replacementPattern=l([^r]*)r
xteps.fieldForceAccess=true
xteps.methodForceAccess=true
xteps.cleanStackTrace=true
xteps.useSPIListeners=true
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

You can switch to Java 9+ or use `com.plugatar.xteps.core.util.function.Unchecked` static methods to hide any
throwables.

```java
import static com.plugatar.xteps.core.util.function.Unchecked.uncheckedFunction;

Xteps.nestedStepsTo("Step 1", uncheckedFunction(steps -> steps
    .step("Inner step 1", () -> {})
));
```
