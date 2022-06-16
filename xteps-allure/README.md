# Xteps Allure

*Xteps Allure integration*

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-allure)
[![Javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-allure/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-allure)

## How to use

Just add the dependency to your pom file. Allure listener will be automatically found using the Service Provider
Interface mechanism. Also don't forget to add Allure dependency and configure it.

For multi-module projects you need to add listener to `module-info.java` file. For example:
```
provides com.plugatar.xteps.core.StepListener with com.plugatar.xteps.allure.AllureStepListener;
```
Or just use property `xteps.listeners` to specify listeners.
