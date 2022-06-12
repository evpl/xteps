# Xteps ReportPortal

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.plugatar.xteps/xteps-reportportal)
[![javadoc](https://javadoc.io/badge2/com.plugatar.xteps/xteps-reportportal/javadoc.svg)](https://javadoc.io/doc/com.plugatar.xteps/xteps-reportportal)

---

## How to use

Just add the dependency to your pom file. ReportPortal listener will be automatically found using the Service Provider
Interface mechanism. Also don't forget to add ReportPortal dependency and configure it.
***

For multi-module projects you need to add listener to `module-info.java` file. For example:
```
provides com.plugatar.xteps.core.StepListener with com.plugatar.xteps.reportportal.ReportPortalStepListener;
```
Or just use property `xteps.listeners` to specify listeners.
