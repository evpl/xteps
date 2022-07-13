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
package com.plugatar.xteps;

import com.plugatar.xteps.Xteps.InitialStepsChainSupplier;
import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.XtepsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link InitialStepsChainSupplier}.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
final class InitialStepsChainSupplierTest {

    @BeforeAll
    static void beforeAll() {
        InitialStepsChainSupplierTest.clearProperties();
    }

    @AfterEach
    void afterEach() {
        InitialStepsChainSupplierTest.clearProperties();
    }

    private static void clearProperties() {
        System.clearProperty("xteps.enabled");
        System.clearProperty("xteps.spi");
        System.clearProperty("xteps.listeners");
    }

    @Test
    void enablePropertyCorrectValueTrueWithListener() {
        System.setProperty("xteps.enabled", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.InitialStepsChainSupplierTest$StaticStepListener");

        new InitialStepsChainSupplier().get().step("enablePropertyCorrectValueTrueWithListener", () -> { });
        assertThat(StaticStepListener.lastStepName()).isEqualTo("enablePropertyCorrectValueTrueWithListener");
    }

    @Test
    void enablePropertyCorrectValueTrueWithoutListener() {
        System.setProperty("xteps.enabled", "true");

        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void enablePropertyCorrectValueFalseWithListener() {
        System.setProperty("xteps.enabled", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.InitialStepsChainSupplierTest$StaticStepListener");

        new InitialStepsChainSupplier().get().step("enablePropertyCorrectValueFalseWithListener", () -> { });
        assertThat(StaticStepListener.lastStepName()).isNull();
    }

    @Test
    void enablePropertyCorrectValueFalseWithoutListener() {
        System.setProperty("xteps.enabled", "false");

        new InitialStepsChainSupplier().get().step("enablePropertyCorrectValueFalseWithoutListener", () -> { });
        assertThat(StaticStepListener.lastStepName()).isNull();
    }

    @Test
    void enablePropertyDefaultValue() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.InitialStepsChainSupplierTest$StaticStepListener");

        new InitialStepsChainSupplier().get().step("enablePropertyDefaultValue", () -> { });
        assertThat(StaticStepListener.lastStepName()).isEqualTo("enablePropertyDefaultValue");
    }

    @Test
    void useSPIListenersPropertyCorrectValueTrue() {
        System.setProperty("xteps.spi", "true");

        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void useSPIListenersPropertyCorrectValueFalse() {
        System.setProperty("xteps.useSPIListeners", "false");

        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void listenersPropertyCorrectValue1Listener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.InitialStepsChainSupplierTest$StaticStepListener");

        new InitialStepsChainSupplier().get().step("listenersPropertyCorrectValue1Listener", () -> { });
        assertThat(StaticStepListener.lastStepName()).isEqualTo("listenersPropertyCorrectValue1Listener");
    }

    @Test
    void listenersPropertyCorrectValue2Listeners() {
        System.setProperty(
            "xteps.listeners",
            "com.plugatar.xteps.InitialStepsChainSupplierTest$StaticStepListener," +
                "com.plugatar.xteps.InitialStepsChainSupplierTest$StaticStepListener2"
        );

        new InitialStepsChainSupplier().get().step("listenersPropertyCorrectValue2Listeners", () -> { });
        assertThat(StaticStepListener.lastStepName()).isEqualTo("listenersPropertyCorrectValue2Listeners");
        assertThat(StaticStepListener2.lastStepName()).isEqualTo("listenersPropertyCorrectValue2Listeners");
    }

    @Test
    void listenersPropertyDefaultValue() {
        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void listenersPropertyCorrectValue0Listeners() {
        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .doesNotThrowAnyException();
    }

    @Test
    void listenersPropertyIncorrectValueNotListener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.XtepsBaseSupplierTest$NotListener");
        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void listenersPropertyIncorrectValueNonExistentListener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.XtepsBaseSupplierTest$NonExistent");
        assertThatCode(() -> new InitialStepsChainSupplier().get())
            .isInstanceOf(XtepsException.class);
    }

    public static final class NotListener {

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
                                final String stepName,
                                final String stepDescription) {
            lastStepName = stepName;
        }

        @Override
        public void stepPassed(final String uuid) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final Throwable exception) {
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
                                final String stepName,
                                final String stepDescription) {
            lastStepName = stepName;
        }

        @Override
        public void stepPassed(final String uuid) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final Throwable exception) {
        }
    }
}
