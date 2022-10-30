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
package com.plugatar.xteps.unchecked;

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
 * Tests for {@link UncheckedXteps}.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
final class UncheckedXtepsTest {

    @BeforeAll
    static void beforeAll() {
        clearProperties();
        System.setProperty("xteps.listeners", "com.plugatar.xteps.unchecked.UncheckedXtepsTest$StaticStepListener");
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

    private static void assertThatStepPassed(final String stepName,
                                             final String stepDescription,
                                             final OptionalValue<?> optionalContext) {
        /* stepStarted method */
        final String stepStartedUuid = StaticStepListener.stepStartedUUID();
        assertThat(stepStartedUuid).matches(uuidPattern());
        assertThat(StaticStepListener.stepStartedName()).matches(stepName);
        assertThat(StaticStepListener.stepStartedDescription()).matches(stepDescription);
        final OptionalValue<?> stepStartedOptionalContext = StaticStepListener.stepStartedOptionalContext();
        assertThat(stepStartedOptionalContext).isNotNull();
        assertThat(stepStartedOptionalContext.isPresent()).isSameAs(optionalContext.isPresent());
        if (stepStartedOptionalContext.isPresent()) {
            assertThat(stepStartedOptionalContext.value()).isSameAs(optionalContext.value());
        }
        /* stepPassed method */
        assertThat(StaticStepListener.stepPassedUUID()).isSameAs(stepStartedUuid);
    }

    private static void assertThatStepFailed(final String stepName,
                                             final String stepDescription,
                                             final OptionalValue<?> optionalContext,
                                             final Throwable exception) {
        /* stepStarted method */
        final String stepStartedUuid = StaticStepListener.stepStartedUUID();
        assertThat(stepStartedUuid).matches(uuidPattern());
        assertThat(StaticStepListener.stepStartedName()).matches(stepName);
        assertThat(StaticStepListener.stepStartedDescription()).matches(stepDescription);
        final OptionalValue<?> stepStartedOptionalContext = StaticStepListener.stepStartedOptionalContext();
        assertThat(stepStartedOptionalContext).isNotNull();
        assertThat(stepStartedOptionalContext.isPresent()).isSameAs(optionalContext.isPresent());
        if (stepStartedOptionalContext.isPresent()) {
            assertThat(stepStartedOptionalContext.value()).isSameAs(optionalContext.value());
        }
        /* stepFailed method */
        assertThat(StaticStepListener.stepFailedUUID()).isSameAs(stepStartedUuid);
        assertThat(StaticStepListener.stepFailedException()).isSameAs(exception);
    }

    private static void assertThatNoStep() {
        assertThat(StaticStepListener.stepStartedUUID()).isNull();
        assertThat(StaticStepListener.stepStartedName()).isNull();
        assertThat(StaticStepListener.stepStartedDescription()).isNull();
        assertThat(StaticStepListener.stepStartedOptionalContext()).isNull();
        assertThat(StaticStepListener.stepPassedUUID()).isNull();
        assertThat(StaticStepListener.stepFailedUUID()).isNull();
        assertThat(StaticStepListener.stepFailedException()).isNull();
    }

    @Test
    void stepMethodWithName() {
        final String stepName = "stepMethodWithName";

        UncheckedXteps.step(stepName);
        assertThatStepPassed(stepName, "", OptionalValue.empty());
    }

    @Test
    void stepMethodWithNameAndDescription() {
        final String stepName = "stepMethodWithNameAndDescription";
        final String stepDescription = "stepMethodWithNameAndDescription description";

        UncheckedXteps.step(stepName, stepDescription);
        assertThatStepPassed(stepName, stepDescription, OptionalValue.empty());
    }

    @Test
    void stepMethodWithAction() {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        UncheckedXteps.step(action);
        assertThatNoStep();
        verify(action, times(1)).run();
    }

    @Test
    void stepMethodWithNameAndAction() {
        final String stepName = "stepMethodWithNameAndAction";
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        UncheckedXteps.step(stepName, action);
        assertThatStepPassed(stepName, "", OptionalValue.empty());
        verify(action, times(1)).run();
    }

    @Test
    void stepMethodWithNameAndDescriptionAndAction() {
        final String stepName = "stepMethodWithNameAndDescriptionAndAction";
        final String stepDescription = "stepMethodWithNameAndDescriptionAndAction description";
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        UncheckedXteps.step(stepName, stepDescription, action);
        assertThatStepPassed(stepName, stepDescription, OptionalValue.empty());
        verify(action, times(1)).run();
    }

    @Test
    void stepToMethodWithAction() {
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> action = mock(ThrowingSupplier.class);
        final Object result = new Object();
        when(action.get()).thenReturn(result);

        assertThat(UncheckedXteps.stepTo(action)).isSameAs(result);
        assertThatNoStep();
        verify(action, times(1)).get();
    }

    @Test
    void stepToMethodWithNameAndAction() {
        final String stepName = "stepToMethodWithNameAndAction";
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> action = mock(ThrowingSupplier.class);
        final Object result = new Object();
        when(action.get()).thenReturn(result);

        assertThat(UncheckedXteps.stepTo(stepName, action)).isSameAs(result);
        assertThatStepPassed(stepName, "", OptionalValue.empty());
        verify(action, times(1)).get();
    }

    @Test
    void stepToMethodWithNameAndDescriptionAndAction() {
        final String stepName = "stepToMethodWithNameAndDescriptionAndAction";
        final String stepDescription = "stepToMethodWithNameAndDescriptionAndAction description";
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> action = mock(ThrowingSupplier.class);
        final Object result = new Object();
        when(action.get()).thenReturn(result);

        assertThat(UncheckedXteps.stepTo(stepName, stepDescription, action)).isSameAs(result);
        assertThatStepPassed(stepName, stepDescription, OptionalValue.empty());
        verify(action, times(1)).get();
    }


    @Test
    void stepsChainMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        final NoCtxStepsChain stepsChain = UncheckedXteps.stepsChain();
        assertThat(UncheckedXteps.stepsChain()).isSameAs(stepsChain);

        final String stepName = "stepsChainMethod";
        stepsChain.step(stepName, action);
        assertThatStepPassed(stepName, "", OptionalValue.empty());
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
            UncheckedXteps.stepsChain()
                .withContext(autoCloseable1)
                .contextIsCloseable()
                .withContext(autoCloseable2)
                .contextIsCloseable()
                .supplyContext(ctx -> { throw baseException; })
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
            UncheckedXteps.stepsChain()
                .withContext(autoCloseable1)
                .contextIsCloseable()
                .withContext(autoCloseable2)
                .contextIsCloseable()
                .closeCloseableContexts()
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
        private static String stepStartedUUID = null;
        private static String stepStartedName = null;
        private static String stepStartedDescription = null;
        private static OptionalValue<?> stepStartedOptionalContext = null;
        private static String stepPassedUUID = null;
        private static String stepFailedUUID = null;
        private static Throwable stepFailedException = null;


        public StaticStepListener() {
        }

        static String stepStartedUUID() {
            final String last = stepStartedUUID;
            stepStartedUUID = null;
            return last;
        }

        static String stepStartedName() {
            final String last = stepStartedName;
            stepStartedName = null;
            return last;
        }

        static String stepStartedDescription() {
            final String last = stepStartedDescription;
            stepStartedDescription = null;
            return last;
        }

        static OptionalValue<?> stepStartedOptionalContext() {
            final OptionalValue<?> last = stepStartedOptionalContext;
            stepStartedOptionalContext = null;
            return last;
        }

        static String stepPassedUUID() {
            final String last = stepPassedUUID;
            stepPassedUUID = null;
            return last;
        }

        static String stepFailedUUID() {
            final String last = stepFailedUUID;
            stepFailedUUID = null;
            return last;
        }

        static Throwable stepFailedException() {
            final Throwable last = stepFailedException;
            stepFailedException = null;
            return last;
        }

        @Override
        public void stepStarted(final String stepUUID,
                                final String stepName,
                                final String stepDescription,
                                final OptionalValue<?> optionalContext) {
            stepStartedUUID = stepUUID;
            stepStartedName = stepName;
            stepStartedDescription = stepDescription;
            stepStartedOptionalContext = optionalContext;
        }

        @Override
        public void stepPassed(final String stepUUID) {
            stepPassedUUID = stepUUID;
        }

        @Override
        public void stepFailed(final String stepUUID,
                               final Throwable exception) {
            stepFailedUUID = stepUUID;
            stepFailedException = exception;
        }
    }
}
