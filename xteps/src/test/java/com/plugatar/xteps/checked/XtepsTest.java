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
package com.plugatar.xteps.checked;

import com.plugatar.xteps.base.CloseException;
import com.plugatar.xteps.base.OptionalValue;
import com.plugatar.xteps.base.StepListener;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Xteps}.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
final class XtepsTest {

    @BeforeAll
    static void beforeAll() {
        clearProperties();
        System.setProperty("xteps.listeners", "com.plugatar.xteps.checked.XtepsTest$StaticStepListener");
    }

    @AfterAll
    static void afterAll() {
        clearProperties();
    }

    private static void clearProperties() {
        System.clearProperty("xteps.enabled");
        System.clearProperty("xteps.spi");
        System.clearProperty("xteps.listeners");
        System.clearProperty("xteps.cleanStackTrace");
    }

    @Test
    void stepMethod() {
        Xteps.step("stepMethod");
        assertThat(StaticStepListener.lastUUID()).matches(uuidPattern());
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepMethod");
        assertThat(StaticStepListener.lastStepDescription()).isEmpty();
    }

    @Test
    void stepMethodWithDescription() {
        Xteps.step("stepMethodWithDescription", "stepMethodWithDescription description");
        assertThat(StaticStepListener.lastUUID()).matches(uuidPattern());
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepMethodWithDescription");
        assertThat(StaticStepListener.lastStepDescription()).isEqualTo("stepMethodWithDescription description");
    }

    @Test
    void stepMethodWithAction() {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        Xteps.step("stepMethodWithAction", action);
        assertThat(StaticStepListener.lastUUID()).matches(uuidPattern());
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepMethodWithAction");
        assertThat(StaticStepListener.lastStepDescription()).isEmpty();
        verify(action, times(1)).run();
    }

    @Test
    void stepMethodWithDescriptionAndAction() {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        Xteps.step("stepMethodWithDescriptionAndAction", "stepMethodWithDescriptionAndAction description", action);
        assertThat(StaticStepListener.lastUUID()).matches(uuidPattern());
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepMethodWithDescriptionAndAction");
        assertThat(StaticStepListener.lastStepDescription()).isEqualTo("stepMethodWithDescriptionAndAction description");
        verify(action, times(1)).run();
    }

    @Test
    void stepToMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> action = mock(ThrowingSupplier.class);
        final Object result = new Object();
        when(action.get()).thenReturn(result);

        assertThat(Xteps.stepTo("stepToMethod", action)).isSameAs(result);
        assertThat(StaticStepListener.lastUUID()).matches(uuidPattern());
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepToMethod");
        assertThat(StaticStepListener.lastStepDescription()).isEmpty();
        verify(action, times(1)).get();
    }

    @Test
    void stepToMethodWithDescription() {
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> action = mock(ThrowingSupplier.class);
        final Object result = new Object();
        when(action.get()).thenReturn(result);

        assertThat(Xteps.stepTo("stepToMethodWithDescription", "stepToMethodWithDescription description", action)).isSameAs(result);
        assertThat(StaticStepListener.lastUUID()).matches(uuidPattern());
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepToMethodWithDescription");
        assertThat(StaticStepListener.lastStepDescription()).isEqualTo("stepToMethodWithDescription description");
        verify(action, times(1)).get();
    }

    @Test
    void stepsChainMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        final NoCtxStepsChain stepsChain = Xteps.stepsChain();
        assertThat(Xteps.stepsChain()).isSameAs(stepsChain);
        stepsChain.step("stepsChainMethod", action);
        assertThat(StaticStepListener.lastStepName()).isEqualTo("stepsChainMethod");
        assertThat(StaticStepListener.lastStepDescription()).isEmpty();
        verify(action, times(1)).run();
    }

    @Test
    void chainWithAutoCloseableContextsIfActionFailed() {
        final RuntimeException baseException = new RuntimeException("base ex");
        final RuntimeException exception1 = new RuntimeException("ex 1");
        final AutoCloseable autoCloseable1 = throwingAutoCloseable(exception1);
        final RuntimeException exception2 = new RuntimeException("ex 2");
        final AutoCloseable autoCloseable2 = throwingAutoCloseable(exception2);

        assertThatCode(() ->
            Xteps.stepsChain()
                .withContext(autoCloseable1)
                .contextIsAutoCloseable()
                .withContext(autoCloseable2)
                .contextIsAutoCloseable()
                .supplyContext(ctx -> {
                    throw baseException;
                })
        ).isSameAs(baseException)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void chainWithAutoCloseableContextsIfCloseMethodInvoked() {
        final RuntimeException exception1 = new RuntimeException("ex 1");
        final AutoCloseable autoCloseable1 = throwingAutoCloseable(exception1);
        final RuntimeException exception2 = new RuntimeException("ex 2");
        final AutoCloseable autoCloseable2 = throwingAutoCloseable(exception2);

        assertThatCode(() ->
            Xteps.stepsChain()
                .withContext(autoCloseable1)
                .contextIsAutoCloseable()
                .withContext(autoCloseable2)
                .contextIsAutoCloseable()
                .closeAutoCloseableContexts()
        ).isInstanceOf(CloseException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    private static AutoCloseable throwingAutoCloseable(final Exception exception) {
        return new AutoCloseable() {
            @Override
            public void close() throws Exception {
                throw exception;
            }
        };
    }

    private static Pattern uuidPattern() {
        return Pattern.compile("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$");
    }

    public static final class StaticStepListener implements StepListener {
        private static String lastUUID = null;
        private static String lastStepName = null;
        private static String lastStepDescription = null;

        public StaticStepListener() {
        }

        static String lastUUID() {
            final String last = lastUUID;
            lastUUID = null;
            return last;
        }

        static String lastStepName() {
            final String last = lastStepName;
            lastStepName = null;
            return last;
        }

        static String lastStepDescription() {
            final String last = lastStepDescription;
            lastStepDescription = null;
            return last;
        }

        @Override
        public void stepStarted(final String stepUUID,
                                final String stepName,
                                final String stepDescription,
                                final OptionalValue<?> optionalContext) {
            lastUUID = stepUUID;
            lastStepName = stepName;
            lastStepDescription = stepDescription;
        }

        @Override
        public void stepPassed(final String stepUUID) {
        }

        @Override
        public void stepFailed(final String stepUUID,
                               final Throwable exception) {
        }
    }
}
