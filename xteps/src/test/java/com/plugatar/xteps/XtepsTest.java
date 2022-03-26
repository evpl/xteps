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

import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.XtepsBase;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Xteps}.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
final class XtepsTest {

    @BeforeAll
    static void beforeAll() {
        clearProperties();
        System.setProperty("xteps.listeners", "com.plugatar.xteps.XtepsTest$StaticStepListener");
    }

    @AfterAll
    static void afterAll() {
        clearProperties();
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
    void classIsFinal() {
        assertThat(Xteps.class).isFinal();
    }

    @Test
    void singlePrivateCtor() {
        assertThat(Xteps.class.getDeclaredConstructors())
            .singleElement()
            .is(new Condition<>(
                ctor -> Modifier.isPrivate(ctor.getModifiers()),
                "private"
            ));
    }

    @Test
    void xtepsBaseMethod() {
        final XtepsBase base = Xteps.xtepsBase();
        base.steps().step("apiMethod", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("apiMethod");
    }

    @Test
    void ofMethod() {
        final NoCtxSteps noCtxSteps = Xteps.of();
        assertThat(Xteps.steps()).isSameAs(noCtxSteps);
        noCtxSteps.step("ofMethod", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("ofMethod");
    }

    @Test
    void stepsMethod() {
        final NoCtxSteps noCtxSteps = Xteps.steps();
        assertThat(Xteps.steps()).isSameAs(noCtxSteps);
        noCtxSteps.step("stepsMethod", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepsMethod");
    }

    @Test
    void ofOfValueMethod() {
        Xteps.of(111)
            .step("ofOfValueMethod {context}", c -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("ofOfValueMethod 111");
    }

    @Test
    void stepsOfValueMethod() {
        Xteps.stepsOf(111)
            .step("stepsOfValueMethod {context}", c -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepsOfValueMethod 111");
    }

    @Test
    void ofOfSupplierMethod() {
        Xteps.of(() -> 111)
            .step("ofOfSupplierMethod {context}", c -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("ofOfSupplierMethod 111");
    }

    @Test
    void stepsOfSupplierMethod() {
        Xteps.stepsOf(() -> 111)
            .step("stepsOfSupplierMethod {context}", c -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepsOfSupplierMethod 111");
    }

    @Test
    void emptyStepMethod() {
        Xteps.emptyStep("emptyStepMethod");
        assertThat(StaticStepListener.lastStepName()).isEqualTo("emptyStepMethod");
    }

    @Test
    void stepMethod() {
        Xteps.step("stepMethod", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepMethod");
    }

    @Test
    void stepToContextMethod() {
        assertThat(Xteps.stepToContext("stepToContextMethod", () -> 111).context())
            .isEqualTo(111);
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepToContextMethod");
    }

    @Test
    void stepToMethod() {
        final Object result = Xteps.stepTo("stepToMethod", () -> 111);
        assertThat(result).isEqualTo(111);
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepToMethod");
    }

    @Test
    void nestedStepsMethod() {
        final AtomicBoolean isExecuted = new AtomicBoolean();
        Xteps.nestedSteps("nestedStepsMethod", () -> isExecuted.set(true));
        assertThat(isExecuted).isTrue();
        assertThat(StaticStepListener.lastStepName()).isEqualTo("nestedStepsMethod");
    }

    @Test
    void nestedStepsToMethod() {
        final AtomicBoolean isExecuted = new AtomicBoolean();
        final Object result = Xteps.nestedStepsTo("nestedStepsToMethod", () -> {
            isExecuted.set(true);
            return 111;
        });
        assertThat(result).isEqualTo(111);
        assertThat(StaticStepListener.lastStepName()).isEqualTo("nestedStepsToMethod");
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
            StaticStepListener.lastStepName = stepName;
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
}
