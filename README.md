# Xteps

# This project is archived. New version - https://github.com/evpl/xteps2

*High-level contextual steps in your tests for any reporting tool*

[![Maven Central](https://img.shields.io/badge/maven--central-5.8-brightgreen?style=flat)](https://search.maven.org/search?q=com.plugatar.xteps)
[![Javadoc](https://img.shields.io/badge/javadoc-5.8-blue?style=flat)](https://javadoc.io/doc/com.plugatar.xteps)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Hits-of-Code](https://hitsofcode.com/github/evpl/xteps?branch=master)](https://hitsofcode.com/github/evpl/xteps/view?branch=master)
![Lines of code](https://img.shields.io/tokei/lines/github/evpl/xteps?label=Total%20lines)

Xteps is a facade that provides a convenient way to report test steps. Integrations with Allure, Qase, TestIT and
ReportPortal are ready, but you can write your own listener for another reporting system or just create an issue.

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
* [JDK 8 unreported exception bug](#JDK-8-unreported-exception-bug)

## How to use

Requires Java 8+ version or Kotlin JVM. Just add suitable dependency.

|                         | Java (checked exceptions are not ignored)                                                                                                                                                                                                                                                                                                                | Java (checked exceptions are ignored) / Kotlin JVM                                                                                                                                                                                                                                                                                                                                     |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Allure                  | `xteps-allure`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:xteps-allure) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/xteps-allure)                   | `unchecked-xteps-allure`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:unchecked-xteps-allure) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-allure)                   |
| Qase                    | `xteps-qase`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:xteps-qase) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/xteps-qase)                         | `unchecked-xteps-qase`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:unchecked-xteps-qase) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-qase)                         |
| ReportPortal            | `xteps-reportportal`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:xteps-reportportal) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/xteps-reportportal) | `unchecked-xteps-reportportal`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:unchecked-xteps-reportportal) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-reportportal) |
| TestIT                  | `xteps-testit`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:xteps-testit) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/xteps-testit)                   | `unchecked-xteps-testit`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:unchecked-xteps-testit) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps-testit)                   |
| Custom reporting system | `xteps`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:xteps) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/xteps)                                        | `unchecked-xteps`<br><br>[![Maven Central](https://img.shields.io/badge/-maven--central-brightgreen?style=flat-square)](https://search.maven.org/search?q=g:com.plugatar.xteps%20AND%20a:unchecked-xteps) [![Javadoc](https://img.shields.io/badge/-javadoc-blue?style=flat-square)](https://javadoc.io/doc/com.plugatar.xteps/unchecked-xteps)                                        |

Maven:

```xml
<dependency>
  <groupId>com.plugatar.xteps</groupId>
  <artifactId>{artifact name from table}</artifactId>
  <version>5.8</version>
  <scope>test</scope>
</dependency>
```

Gradle:

```groovy
dependencies {
    testImplementation 'com.plugatar.xteps:{artifact name from table}:5.8'
}
```

Kotlin DSL:

```groovy
dependencies {
    testImplementation("com.plugatar.xteps:{artifact name from table}:5.8")
}
```

Read more about [checked exceptions](#Checked-exceptions) and [integrations](#Integrations).

## API

### Static methods

First part of Xteps API is a set of `step`, `stepTo` and `threadHook` static methods located in the
`com.plugatar.xteps.checked.Xteps` / `com.plugatar.xteps.unchecked.UncheckedXteps` class.

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

Second part is a steps chain starts with `stepsChain`, `stepsChainOf`, `stepsChainOf(T)`, `stepsChainOf(T1, T2)` and
`stepsChainOf(T1, T2, T3)` static method located in the `com.plugatar.xteps.checked.Xteps` /
`com.plugatar.xteps.unchecked.UncheckedXteps` class.

```java
class ExampleTest {

    @Test
    void stepsChainMethodExample() {
        stepsChain()
            .stepToCtx("Create WebDriver", () -> {
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
            .stepToCtx("Get database connection", () -> {
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
            .callChainHooks();
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
            .stepToCtx(new CreateWebDriver())
            .chainHook(WebDriver::quit)
            .step(new OpenPage("https://.../login"))
            .step(new TypeLogin("user123"))
            .step(new TypePassword("1234567890"))
            .step(new ClickOnLoginButton())
            .stepToCtx(new DatabaseConnection())
            .chainHook(Connection::close)
            .step(new CheckUserFullName("user123"))
            .callChainHooks();
    }
}
```

Steps can be combined.

```java
class LoginAs extends ConsumerStep<WebDriver> {

    public LoginAs(String username, String password) {
        super("Login as " + username + " / " + password, driver ->
            stepsChainOf(driver)
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
void dummyStepObjectsExample() {
    stepsChain()
        .stepToCtx(SupplierStep.<WebDriver>dummy("Create WebDriver"))
        .chainHook(WebDriver::quit)
        .step(RunnableStep.dummy("Open page https://.../login"))
        .step(RunnableStep.dummy("Type login user123"))
        .step(RunnableStep.dummy("Type password 1234567890"))
        .step(RunnableStep.dummy("Click on login button"))
        .stepToCtx(SupplierStep.<Connection>dummy("Get database connection"))
        .chainHook(Connection::close)
        .step(RunnableStep.dummy("Check user full name"))
        .callChainHooks();
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
            .withCtx(() -> {
                /* ... */
                return new ChromeDriver();
            })
            .chainHook(WebDriver::quit)
            .step("When", new OpenPage("https://.../login"))
            .step("And", new TypeLogin("user123"))
            .step("And", new TypePassword("1234567890"))
            .step("And", new ClickOnLoginButton())
            .step("Then", new UserFullNameIs("Expected Name"))
            .callChainHooks();
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

| Name                            | Type    | Required | Default value | Description                                                                                                                                                                                           |
|---------------------------------|---------|----------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| xteps.enabled                   | Boolean | No       | `true`        | Enable/disable steps logging.                                                                                                                                                                         |
| xteps.spi                       | Boolean | No       | `true`        | Enable/disable Service Provider Interface mechanism to detect and instantiate `com.plugatar.xteps.base.StepListener` implementations. Implementations should have zero-argument public constructor.   |
| xteps.listeners                 | String  | No       |               | List of `com.plugatar.xteps.base.StepListener` implementations names in `Class#getTypeName()` format. Names should be separated by `,`. Implementations should have zero-argument public constructor. |
| xteps.cleanStackTrace           | Boolean | No       | `true`        | Removes all stack trace lines about Xteps from any exception except XtepsException.                                                                                                                   |
| xteps.defaultHooksOrder         | Enum    | No       | `FROM_LAST`   | The order in which chain and thread hooks of the same priority will be called - `FROM_FIRST` / `FROM_LAST`.                                                                                           |
| xteps.threadHooksThreadInterval | Long    | No       | `100`         | Interval between thread hooks daemon thread executions in milliseconds.                                                                                                                               |
| xteps.threadHooksThreadPriority | Integer | No       | `5`           | Thread hooks daemon thread priority in the range `1` to `10`.                                                                                                                                         |

### Examples

xteps.properties file example:

```properties
xteps.enabled=true
xteps.spi=true
xteps.listeners=com.my.prj.StepListenerImpl1,com.my.prj.StepListenerImpl2
xteps.cleanStackTrace=true
xteps.defaultHooksOrder=FROM_LAST
xteps.threadHooksThreadInterval=100
xteps.threadHooksThreadPriority=5
```

## Additional features

### Steps chain hooks

You can use hooks in a steps chain. Hooks will be called in case of any exception in steps chain or in case of
`callChainHooks` method call. You also can use hooks to release resources.

```java
stepsChain().withCtx(new AutoCloseableImpl())
    .chainHook(AutoCloseable::close)
    .step("Step", ctx -> {
        //...
    })
    .callChainHooks();
```

### Thread hooks

You can use thread hooks. Hooks will be called after current thread is finished. You also can use hooks to release
resources.

```java
stepsChain().withCtx(new AutoCloseableImpl())
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

You can use step name or step description replacements by the way provided by Allure / Qase / TestIT / ReportPortal.

```java
stepsChain()
    .withCtx("value")
    .withCtx(111)
    .step("Step with context = {0} and second context = {1}", (integer, string) -> {
        //...
    });
```

This step will be reported with name "Step with context = 111 and second context = value".

You can also use utility methods for Allure, Qase and TestIT - `AllureStepUtils`, `QaseStepUtils`, `TestITStepUtils`.
It allows you to change the step name and other step attributes at runtime.

## JDK 8 unreported exception bug

You may run into a problem if you use Xteps and JDK 8. The issue is caused by generic exceptions.

```java
stepsChain()
    .nestedStepsTo("Step", chain -> chain
        .step("Nested step", () -> {})
    );
```

This code can fail build with this exception `java: unreported exception java.lang.Throwable; must be caught or
declared to be thrown`.

You can switch to JDK 9+ or switch to Unchecked Xteps or use functional interfaces static methods to hide any
exceptions.

```java
stepsChain()
    .nestedStepsTo("Step", ThrowingFunction.unchecked(chain -> chain
        .step("Nested step", () -> {})
    ));
```
