/*
 * Copyright 2022 Evgenii Plugatar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plugatar.xteps.core.base.supplier;

import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.XtepsBase;
import com.plugatar.xteps.core.exception.ConfigException;
import com.plugatar.xteps.core.exception.StepNameFormatException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link XtepsBaseThrowingSupplier}.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
final class XtepsBaseThrowingSupplierTest {

    @BeforeAll
    static void beforeAll() {
        XtepsBaseThrowingSupplierTest.clearProperties();
    }

    @AfterEach
    void afterEach() {
        XtepsBaseThrowingSupplierTest.clearProperties();
    }

    private static void clearProperties() {
        System.clearProperty("xteps.enabled");
        System.clearProperty("xteps.replacementPattern");
        System.clearProperty("xteps.fieldForceAccess");
        System.clearProperty("xteps.methodForceAccess");
        System.clearProperty("xteps.cleanStackTrace");
        System.clearProperty("xteps.useSPIListeners");
        System.clearProperty("xteps.listeners");
    }

    @Test
    void classIsNotFinal() {
        assertThat(XtepsBaseThrowingSupplier.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = XtepsBaseThrowingSupplier.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .filteredOn(method -> !(method.getName().equals("get") && method.getReturnType() == Object.class))
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void enablePropertyCorrectValueTrueWithListener() {
        System.setProperty("xteps.enabled", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().step("enablePropertyCorrectValueTrueWithListener", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("enablePropertyCorrectValueTrueWithListener");
    }

    @Test
    void enablePropertyCorrectValueTrueWithoutListener() {
        System.setProperty("xteps.enabled", "true");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void enablePropertyCorrectValueFalseWithListener() {
        System.setProperty("xteps.enabled", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().step("enablePropertyCorrectValueFalseWithListener", () -> {});
        assertThat(StaticStepListener.lastStepName()).isNull();
    }

    @Test
    void enablePropertyCorrectValueFalseWithoutListener() {
        System.setProperty("xteps.enabled", "false");

        new XtepsBaseThrowingSupplier().get().steps().step("enablePropertyCorrectValueFalseWithoutListener", () -> {});
        assertThat(StaticStepListener.lastStepName()).isNull();
    }

    @Test
    void enablePropertyDefaultValue() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().step("enablePropertyDefaultValue", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("enablePropertyDefaultValue");
    }

    @Test
    void enablePropertyIncorrectValue() {
        System.setProperty("xteps.enabled", "value");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void patterPropertyCorrectValue1Group() {
        System.setProperty("xteps.replacementPattern", "l([^r]*)r");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(111).step("patterPropertyCorrectValue1Group lcontextr", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("patterPropertyCorrectValue1Group 111");
    }

    @Test
    void patterPropertyCorrectValue2Groups() {
        System.setProperty("xteps.replacementPattern", "l([^r]*)r(abc|def)");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(111).step("patterPropertyCorrectValue2Groups lcontextrabc", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("patterPropertyCorrectValue2Groups 111");
    }

    @Test
    void patterPropertyDefaultValue() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(111).step("patterPropertyDefaultValue {context}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("patterPropertyDefaultValue 111");
    }

    @Test
    void patterPropertyIncorrectValue0Groups() {
        System.setProperty("xteps.replacementPattern", "left right");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void fieldForceAccessPropertyCorrectValueTruePrivateField() {
        System.setProperty("xteps.fieldForceAccess", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(new Obj()).step("fieldForceAccessPropertyCorrectValueTruePrivateField {context.privateField}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("fieldForceAccessPropertyCorrectValueTruePrivateField 222");
    }

    @Test
    void fieldForceAccessPropertyCorrectValueTruePublicField() {
        System.setProperty("xteps.fieldForceAccess", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(new Obj()).step("fieldForceAccessPropertyCorrectValueTruePublicField {context.publicField}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("fieldForceAccessPropertyCorrectValueTruePublicField 111");
    }

    @Test
    void fieldForceAccessPropertyCorrectValueFalsePrivateField() {
        System.setProperty("xteps.fieldForceAccess", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get()
            .steps()
            .toContext(new Obj())
            .step("fieldForceAccessPropertyCorrectValueFalsePrivateField {context.privateField}", context -> {})
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void fieldForceAccessPropertyCorrectValueFalsePublicField() {
        System.setProperty("xteps.fieldForceAccess", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(new Obj()).step("fieldForceAccessPropertyCorrectValueFalsePublicField {context.publicField}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("fieldForceAccessPropertyCorrectValueFalsePublicField 111");
    }

    @Test
    void fieldForceAccessPropertyDefaultValue() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        final XtepsBase xtepsBase = new XtepsBaseThrowingSupplier().get();
        xtepsBase.steps().toContext(new Obj()).step("fieldForceAccessPropertyDefaultValue {context.publicField}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("fieldForceAccessPropertyDefaultValue 111");
        assertThatCode(() -> xtepsBase.steps()
            .toContext(new Obj())
            .step("fieldForceAccessPropertyDefaultValue {context.privateField}", context -> {})
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void fieldForceAccessPropertyIncorrectValue() {
        System.setProperty("xteps.fieldForceAccess", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void methodForceAccessPropertyCorrectValueTruePrivateMethod() {
        System.setProperty("xteps.methodForceAccess", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(new Obj()).step("methodForceAccessPropertyCorrectValueTruePrivateMethod {context.privateMethod()}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("methodForceAccessPropertyCorrectValueTruePrivateMethod 444");
    }

    @Test
    void methodForceAccessPropertyCorrectValueTruePublicMethod() {
        System.setProperty("xteps.methodForceAccess", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(new Obj()).step("methodForceAccessPropertyCorrectValueTruePublicMethod {context.publicMethod()}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("methodForceAccessPropertyCorrectValueTruePublicMethod 333");
    }

    @Test
    void methodForceAccessPropertyCorrectValueFalsePrivateMethod() {
        System.setProperty("xteps.methodForceAccess", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get()
            .steps()
            .toContext(new Obj())
            .step("methodForceAccessPropertyCorrectValueFalsePrivateMethod {context.privateMethod()}", context -> {})
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void methodForceAccessPropertyCorrectValueFalsePublicMethod() {
        System.setProperty("xteps.methodForceAccess", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().toContext(new Obj()).step("methodForceAccessPropertyCorrectValueFalsePublicMethod {context.publicMethod()}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("methodForceAccessPropertyCorrectValueFalsePublicMethod 333");
    }

    @Test
    void methodForceAccessPropertyDefaultValue() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        final XtepsBase xtepsBase = new XtepsBaseThrowingSupplier().get();
        xtepsBase.steps().toContext(new Obj()).step("methodForceAccessPropertyDefaultValue {context.publicMethod()}", context -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("methodForceAccessPropertyDefaultValue 333");
        assertThatCode(() -> xtepsBase.steps()
            .toContext(new Obj())
            .step("methodForceAccessPropertyDefaultValue {context.privateMethod()}", context -> {})
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void methodForceAccessPropertyIncorrectValue() {
        System.setProperty("xteps.methodForceAccess", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void cleanStackTracePropertyCorrectValueTrue() {
        System.setProperty("xteps.cleanStackTrace", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        final XtepsBase xtepsBase = new XtepsBaseThrowingSupplier().get();
        try {
            xtepsBase.steps().step("cleanStackTracePropertyCorrectValueTrue", () -> { throw new RuntimeException(); });
        } catch (final Throwable th) {
            assertThat(th.getStackTrace())
                .filteredOn(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.plugatar.xteps"))
                .isEmpty();
        }
    }

    @Test
    void cleanStackTracePropertyCorrectValueFalse() {
        System.setProperty("xteps.cleanStackTrace", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        final XtepsBase xtepsBase = new XtepsBaseThrowingSupplier().get();
        try {
            xtepsBase.steps().step("cleanStackTracePropertyCorrectValueTrue", () -> { throw new RuntimeException(); });
        } catch (final Throwable th) {
            assertThat(th.getStackTrace())
                .filteredOn(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.plugatar.xteps"))
                .isNotEmpty();
        }
    }

    @Test
    void cleanStackTracePropertyDefaultValue() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        final XtepsBase xtepsBase = new XtepsBaseThrowingSupplier().get();
        try {
            xtepsBase.steps().step("cleanStackTracePropertyCorrectValueTrue", () -> { throw new RuntimeException(); });
        } catch (final Throwable th) {
            assertThat(th.getStackTrace())
                .filteredOn(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.plugatar.xteps"))
                .isEmpty();
        }
    }

    @Test
    void cleanStackTracePropertyIncorrectValue() {
        System.setProperty("xteps.cleanStackTrace", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void useSPIListenersPropertyCorrectValueTrue() {
        System.setProperty("xteps.useSPIListeners", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void useSPIListenersPropertyCorrectValueFalse() {
        System.setProperty("xteps.useSPIListeners", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void useSPIListenersPropertyIncorrectValue() {
        System.setProperty("xteps.useSPIListeners", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void listenersPropertyCorrectValue1Listener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener");

        new XtepsBaseThrowingSupplier().get().steps().step("listenersPropertyCorrectValue1Listener", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("listenersPropertyCorrectValue1Listener");
    }

    @Test
    void listenersPropertyCorrectValue2Listeners() {
        System.setProperty(
            "xteps.listeners",
            "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener," +
                "com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplierTest$StaticStepListener2"
        );

        new XtepsBaseThrowingSupplier().get().steps().step("listenersPropertyCorrectValue2Listeners", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("listenersPropertyCorrectValue2Listeners");
        assertThat(StaticStepListener2.lastStepName()).isEqualTo("listenersPropertyCorrectValue2Listeners");
    }

    @Test
    void listenersPropertyDefaultValue() {
        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void listenersPropertyIncorrectValue0Listeners() {
        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void listenersPropertyIncorrectValueNotListener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.XtepsBaseSupplierTest$NotListener");
        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    @Test
    void listenersPropertyIncorrectValueNonExistentListener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.XtepsBaseSupplierTest$NonExistent");
        assertThatCode(() -> new XtepsBaseThrowingSupplier().get())
            .isInstanceOf(ConfigException.class);
    }

    static final class NotListener {

        public NotListener() {
        }
    }

    public static final class StaticStepListener implements StepListener {
        private static String lastStepName = null;

        public StaticStepListener() {
        }

        static String lastStepName() {
            final String last = lastStepName;
            lastStepName = null;
            return last;
        }

        @Override
        public void stepStarted(final String uuid,
                                final String stepName) {
            lastStepName = stepName;
        }

        @Override
        public void stepPassed(final String uuid,
                               final String stepName) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final String stepName,
                               final Throwable throwable) {
        }
    }

    public static final class StaticStepListener2 implements StepListener {
        private static String lastStepName = null;

        public StaticStepListener2() {
        }

        static String lastStepName() {
            final String last = lastStepName;
            lastStepName = null;
            return last;
        }

        @Override
        public void stepStarted(final String uuid,
                                final String stepName) {
            lastStepName = stepName;
        }

        @Override
        public void stepPassed(final String uuid,
                               final String stepName) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final String stepName,
                               final Throwable throwable) {
        }
    }

    public static final class Obj {
        public final int publicField = 111;
        private final int privateField = 222;

        public int publicMethod() {
            return 333;
        }

        private int privateMethod() {
            return 444;
        }
    }
}
