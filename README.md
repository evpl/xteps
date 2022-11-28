# Xteps

*High-level contextual steps in your tests for any reporting tool*

[![Version](https://img.shields.io/badge/Version-5.5-brightgreen?style=flat)](https://search.maven.org/search?q=com.plugatar.xteps)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Hits-of-Code](https://hitsofcode.com/github/evpl/xteps?branch=master)](https://hitsofcode.com/github/evpl/xteps/view?branch=master)
![Lines of code](https://img.shields.io/tokei/lines/github/evpl/xteps?label=Total%20lines)

Xteps is a facade that provides a convenient way to report test steps. Integrations with Allure, Qase and ReportPortal
are ready, but you can write your own listener for another reporting system or just create an issue.

## Table of Contents

* [How to use](#How-to-use)
* [API](#API)
    * [Static methods](#Static-methods)
    * [Steps chain](#Steps-chain)
    * [Step objects](#Step-objects)
* [Parameters](#Parameters)
* [Additional features](#Additional-features)
    * [Steps chain hooks](#Steps-chain-hooks)
    * [Thread hooks](#Thread-hooks)
    * [Clean stack trace](#Clean-stack-trace)
    * [Checked exceptions](#Checked-exceptions)
    * [Integrations](#Integrations)
* [Java 8 unreported exception bug](#Java-8-unreported-exception-bug)

## How to use

Requires Java 8+ version or Kotlin JVM. Just add suitable dependency.

|                         | Java (checked exceptions are not ignored)                                                                                                                                                                                                                                                                                            | Java (checked exceptions are ignored) / Kotlin JVM                                                                                                                                                                                                                                                                                                       |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Allure                  | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/xteps-allure)             | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-allure) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-allure)             |
| Qase                    | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-qase) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/xteps-qase)                 | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-qase) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-qase)                 |
| ReportPortal            | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/xteps-reportportal) | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps-reportportal) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-reportportal) |
| Custom reporting system | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/xteps)                           | [![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/unchecked-xteps) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=for-the-badge)](hhttps://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps)                           |

Read more about [checked exceptions](#Checked-exceptions) and [integrations](#Integrations).

## API

### Static methods

First part of Xteps API is a set of static `step` and `stepTo` methods located in the `com.plugatar.xteps.checked.Xteps`
/ `com.plugatar.xteps.unchecked.UncheckedXteps` class.

```java
class ExampleTest {

    @Test
    void staticMethodsExample() {
        WebDriver driver = stepTo("Create WebDriver", () -> {
            /* ... */
            return new ChromeDriver();
        });
        threadHook(driver::quit);
        step("Login", () -> {
            step("Open page", () ->
                driver.navigate().to("https://.../login")
            );
            step("Type login", () ->
                driver.findElement(By.id("login_field")).sendKeys("user123")
            );
            step("Type password", () ->
                driver.findElement(By.id("password_field")).sendKeys("1234567890")
            );
            step("Click on login button", () ->
                driver.findElements(By.id("login_button"))
            );
        });
        final Connection dbConnection = stepTo("Get database connection", () -> {
            /* ... */
            return getDatabaseConnection();
        });
        threadHook(dbConnection::close);
        step("Check user full name label", () -> {
            final By usernameLabelLocator = By.id("full_name_label");
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(visibilityOfElementLocated(usernameLabelLocator));
            final String databaseFullName = dbConnection.createStatement()
                .executeQuery("select full_name from users where username = user123")
                .getString("full_name");
            final String uiFullName = driver.findElement(usernameLabelLocator).getText();
            assertEquals(uiFullName, databaseFullName);
        });
    }
}
```

### Steps chain

Second part is a static `stepsChain` method located in the `com.plugatar.xteps.checked.Xteps` /
`com.plugatar.xteps.unchecked.UncheckedXteps` class.

```java
class ExampleTest {

    @Test
    void stepsChainMethodExample() {
        stepsChain()
            .stepToContext("Create WebDriver", () -> {
                /* ... */
                return new ChromeDriver();
            })
            .chainHook(WebDriver::quit)
            .nestedSteps("Login", chain -> chain
                .step("Open page", driver ->
                    driver.navigate().to("https://.../login")
                )
                .step("Type login", driver ->
                    driver.findElement(By.id("login_field")).sendKeys("user123")
                )
                .step("Type password", driver ->
                    driver.findElement(By.id("password_field")).sendKeys("1234567890")
                )
                .step("Click on login button", driver ->
                    driver.findElements(By.id("login_button"))
                )
            )
            .stepToContext("Get database connection", () -> {
                /* ... */
                return getDatabaseConnection();
            })
            .chainHook(Connection::close)
            .step("Check user full name label", (dbConnection, driver) -> {
                final By usernameLabelLocator = By.id("full_name_label");
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(visibilityOfElementLocated(usernameLabelLocator));
                final String databaseFullName = dbConnection.createStatement()
                    .executeQuery("select full_name from users where username = user123")
                    .getString("full_name");
                final String uiFullName = driver.findElement(usernameLabelLocator).getText();
                assertEquals(uiFullName, databaseFullName);
            })
            .callHooks();
    }
}
```

### Step objects

Third part is a set of steps objects located in the `com.plugatar.xteps.checked.stepobject` /
`com.plugatar.xteps.unchecked.stepobject` package.

```java
class CreateWebDriver extends SupplierStep<WebDriver> {

    public CreateWebDriver() {
        super("Create WebDriver", () -> {
            /* ... */
            return new ChromeDriver();
        });
    }
}

class OpenPage extends ConsumerStep<WebDriver> {

    public OpenPage(String url) {
        super("Open page: " + url, driver ->
            driver.navigate().to(url)
        );
    }
}

class TypeLogin extends ConsumerStep<WebDriver> {

    public TypeLogin(String login) {
        super("Type login: " + login, driver ->
            driver.findElement(By.id("login_field")).sendKeys(login)
        );
    }
}

class TypePassword extends ConsumerStep<WebDriver> {

    public TypePassword(String password) {
        super("Type password: " + password, driver ->
            driver.findElement(By.id("password_field")).sendKeys(password)
        );
    }
}

class ClickOnLoginButton extends ConsumerStep<WebDriver> {

    public ClickOnLoginButton() {
        super("Click on login button", driver ->
            driver.findElements(By.id("login_button"))
        );
    }
}

class DatabaseConnection extends SupplierStep<Connection> {

    public DatabaseConnection() {
        super("Get database connection", () -> {
            /* ... */
            return getDatabaseConnection();
        });
    }
}

class CheckUserFullName extends BiConsumerStep<Connection, WebDriver> {

    public CheckUserFullName(String username) {
        super("Check user full name", (dbConnection, driver) -> {
            final By usernameLabelLocator = By.id("full_name_label");
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(visibilityOfElementLocated(usernameLabelLocator));
            final String databaseFullName = dbConnection.createStatement()
                .executeQuery("select full_name from users where username = " + username)
                .getString("full_name");
            final String uiFullName = driver.findElement(usernameLabelLocator).getText();
            assertEquals(uiFullName, databaseFullName);
        });
    }
}

public class ExampleTest {

    @Test
    void stepObjectsExample() {
        stepsChain()
            .stepToContext(new CreateWebDriver())
            .chainHook(WebDriver::quit)
            .step(new OpenPage("https://.../login"))
            .step(new TypeLogin("user123"))
            .step(new TypePassword("1234567890"))
            .step(new ClickOnLoginButton())
            .stepToContext(new DatabaseConnection())
            .chainHook(Connection::close)
            .step(new CheckUserFullName("user123"))
            .callHooks();
    }
}
```

Steps can be combined.

```java
class LoginAs extends ConsumerStep<WebDriver> {

    public LoginAs(String username, String password) {
        super("Login as " + username + " / " + password, driver ->
            stepsChain().withContext(driver)
                .step(new OpenPage("https://.../login"))
                .step(new TypeLogin(username))
                .step(new TypePassword(password))
                .step(new ClickOnLoginButton())
        );
    }
}
```

You can use stubs before actually implementing the steps.

```java
@Test
void stepObjectsExample() {
    stepsChain()
        .stepToContext(SupplierStep.<WebDriver>dummy("Create WebDriver"))
        .chainHook(WebDriver::quit)
        .step(RunnableStep.dummy("Open page https://.../login"))
        .step(RunnableStep.dummy("Type login user123"))
        .step(RunnableStep.dummy("Type password 1234567890"))
        .step(RunnableStep.dummy("Click on login button"))
        .stepToContext(SupplierStep.<Connection>dummy("Get database connection"))
        .chainHook(Connection::close)
        .step(RunnableStep.dummy("Check user full name"))
        .callHooks();
}
```

BDD style example.

```java
class OpenPage extends ConsumerStep<WebDriver> {

    public OpenPage(String url) {
        super("I open page " + url, driver ->
            driver.navigate().to(url)
        );
    }
}

/*
...
 */

public class ExampleTest {

    @Test
    void bddStyleExample() {
        stepsChain()
            .withContext(() -> {
                /* ... */
                return new ChromeDriver();
            })
            .chainHook(WebDriver::quit)
            .step("When", new OpenPage("https://.../login"))
            .step("And", new TypeLogin("user123"))
            .step("And", new TypePassword("1234567890"))
            .step("And", new ClickOnLoginButton())
            .step("Then", new UserFullNameIs("Expected Name"))
            .callHooks();
    }
}
```

## Parameters

There are two ways to load parameters. Be aware that higher source override lower one - properties from file can be
overridden by system properties.

| Priority | Source                               |
|----------|--------------------------------------|
| 1        | System properties                    |
| 2        | Properties file (`xteps.properties`) |

### Properties list

| Name                     | Type    | Default value | Description                                                                                                                                                                                           |
|--------------------------|---------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| xteps.enabled            | Boolean | `true`        | Enable/disable steps logging.                                                                                                                                                                         |
| xteps.spi                | Boolean | `true`        | Enable/disable Service Provider Interface mechanism to detect and instantiate `com.plugatar.xteps.base.StepListener` implementations. Implementations should have zero-argument public constructor.   |
| xteps.listeners          | String  |               | List of `com.plugatar.xteps.base.StepListener` implementations names in `Class#getTypeName()` format. Names should be separated by `,`. Implementations should have zero-argument public constructor. |
| xteps.cleanStackTrace    | Boolean | `true`        | Removes all stack trace lines about Xteps from any exception except XtepsException.                                                                                                                   |
| xteps.threadHookInterval | Long    | `100`         | Interval between thread hooks daemon thread executions in milliseconds.                                                                                                                               |
| xteps.threadHookPriority | Integer | `5`           | Thread hook daemon thread priority in the range `1` to `10`.                                                                                                                                          |

### Examples

xteps.properties file example:

```properties
xteps.enabled=true
xteps.spi=true
xteps.listeners=com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2
xteps.cleanStackTrace=true
xteps.threadHookInterval=30000
```

## Additional features

### Steps chain hooks

You can use hooks in a steps chain. Hooks will be called in case of any exception in steps chain or in case of
`callHooks()` method call. You also can use hooks to release resources.

```java
stepsChain().withContext(new AutoCloseableImpl())
    .chainHook(AutoCloseable::close)
    .step("Step", ctx -> {
        //...
    })
    .callHooks();
```

### Thread hooks

You can use thread hooks. Hooks will be called after current thread is finished. You also can use hooks to release
resources.

```java
stepsChain().withContext(new AutoCloseableImpl())
    .threadHook(AutoCloseable::close)
    .step("Step", ctx -> {
        //...
    });
```

### Clean stack trace

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

Stacktrace will look like this.

```
java.lang.RuntimeException: Nested step exception

	at my.project.ExampleTest.lambda$null$0(ExampleTest.java:15)
	at my.project.ExampleTest.lambda$test$1(ExampleTest.java:13)
	at my.project.ExampleTest.test(ExampleTest.java:11)
	...
```

### Checked exceptions

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
methods by `com.plugatar.xteps.base` functional interfaces.

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

Unchecked Xteps just hides any exceptions.

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

### Integrations

Set up your project with Allure / Qase / ReportPortal and then just add suitable Xteps dependency.

You can use step name or step description replacements by the way provided by Allure / Qase / ReportPortal.

```java
stepsChain()
    .withContext("value")
    .withContext(111)
    .step("Step with context = {context} and previous context = {context2}", (integer, string) -> {
        //...
    })
    .step("Step with context = {0} and previous context = {1}", (integer, string) -> {
        //...
    });
```

This steps will be reported with name "Step with context = 111 and previous context = value".

You can also use utility methods for Allure and Qase - `AllureStepUtils` and `QaseStepUtils`. It allows you to change
the step name and other step attributes at runtime.

## Java 8 unreported exception bug

You may run into a problem if you use Xteps and Java 8. The issue is caused by generic exceptions.

```java
stepsChain()
    .nestedStepsTo("Step", chain -> chain
        .step("Nested step", () -> {})
    );
```

This code can fail build with this exception `java: unreported exception java.lang.Throwable; must be caught or
declared to be thrown`.

You can switch to Java 9+ or use functional interfaces static methods to hide any exceptions or just use
Unchecked Xteps.

```java
stepsChain()
    .nestedStepsTo("Step", ThrowingFunction.unchecked(chain -> chain
        .step("Nested step", () -> {})
    ));
```
