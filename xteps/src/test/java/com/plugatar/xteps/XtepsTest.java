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

import com.plugatar.xteps.core.InitialStepsChain;
import com.plugatar.xteps.core.StepListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

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
        System.clearProperty("xteps.spi");
        System.clearProperty("xteps.listeners");
    }

    @Test
    void emptyStepMethod() {
        Xteps.step("emptyStepMethod");
        assertThat(StaticStepListener.lastStepName()).isEqualTo("emptyStepMethod");
    }

    @Test
    void stepMethod() {
        Xteps.step("stepMethod", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepMethod");
    }

    @Test
    void stepToMethod() {
        final Object result = new Object();
        assertThat(Xteps.stepTo("stepToMethod", () -> result)).isSameAs(result);
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepToMethod");
    }

    @Test
    void stepsChainMethod() {
        final InitialStepsChain stepsChain = Xteps.stepsChain();
        assertThat(Xteps.stepsChain()).isSameAs(stepsChain);
        stepsChain.step("stepsChainMethod", () -> {});
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepsChainMethod");
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
        public void stepPassed(final String uuid) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final Throwable throwable) {
        }
    }
}
