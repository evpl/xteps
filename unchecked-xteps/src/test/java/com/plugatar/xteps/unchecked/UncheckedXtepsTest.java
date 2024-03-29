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

import com.plugatar.xteps.base.StepListener;
import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.chain.NoCtxSC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.same;
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
        System.clearProperty("xteps.defaultHooksOrder");
        System.clearProperty("xteps.threadHooksThreadInterval");
        System.clearProperty("xteps.threadHooksThreadPriority");
    }

    private static void assertThatStepPassed(final String stepName,
                                             final String stepDescription,
                                             final Object[] contexts) {
        /* stepStarted method */
        final String stepStartedUuid = StaticStepListener.stepStartedUUID();
        assertThat(stepStartedUuid).matches(uuidPattern());
        assertThat(StaticStepListener.stepStartedName()).isEqualTo(stepName);
        assertThat(StaticStepListener.stepStartedDescription()).isEqualTo(stepDescription);
        assertThat(StaticStepListener.stepStartedContexts()).isEqualTo(contexts);
        /* stepPassed method */
        assertThat(StaticStepListener.stepPassedUUID()).isSameAs(stepStartedUuid);
    }

    private static void assertThatStepFailed(final String stepName,
                                             final String stepDescription,
                                             final Object[] contexts,
                                             final Throwable exception) {
        /* stepStarted method */
        final String stepStartedUuid = StaticStepListener.stepStartedUUID();
        assertThat(stepStartedUuid).matches(uuidPattern());
        assertThat(StaticStepListener.stepStartedName()).isEqualTo(stepName);
        assertThat(StaticStepListener.stepStartedDescription()).isEqualTo(stepDescription);
        assertThat(StaticStepListener.stepStartedContexts()).isEqualTo(contexts);
        /* stepFailed method */
        assertThat(StaticStepListener.stepFailedUUID()).isSameAs(stepStartedUuid);
        assertThat(StaticStepListener.stepFailedException()).isSameAs(exception);
    }

    private static void assertThatNoStep() {
        assertThat(StaticStepListener.stepStartedUUID()).isNull();
        assertThat(StaticStepListener.stepStartedName()).isNull();
        assertThat(StaticStepListener.stepStartedDescription()).isNull();
        assertThat(StaticStepListener.stepStartedContexts()).isNull();
        assertThat(StaticStepListener.stepPassedUUID()).isNull();
        assertThat(StaticStepListener.stepFailedUUID()).isNull();
        assertThat(StaticStepListener.stepFailedException()).isNull();
    }

    @Test
    void stepMethodWithName() {
        final String stepName = "stepMethodWithName";

        UncheckedXteps.step(stepName);
        assertThatStepPassed(stepName, "", new Object[]{});
    }

    @Test
    void stepMethodWithNameAndDescription() {
        final String stepName = "stepMethodWithNameAndDescription";
        final String stepDescription = "stepMethodWithNameAndDescription description";

        UncheckedXteps.step(stepName, stepDescription);
        assertThatStepPassed(stepName, stepDescription, new Object[]{});
    }

    @Test
    void stepMethodWithNameAndAction() {
        final String stepName = "stepMethodWithNameAndAction";
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        UncheckedXteps.step(stepName, action);
        assertThatStepPassed(stepName, "", new Object[]{});
        verify(action, times(1)).run();
    }

    @Test
    void stepMethodWithNameAndDescriptionAndAction() {
        final String stepName = "stepMethodWithNameAndDescriptionAndAction";
        final String stepDescription = "stepMethodWithNameAndDescriptionAndAction description";
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        UncheckedXteps.step(stepName, stepDescription, action);
        assertThatStepPassed(stepName, stepDescription, new Object[]{});
        verify(action, times(1)).run();
    }

    @Test
    void stepToMethodWithNameAndAction() {
        final String stepName = "stepToMethodWithNameAndAction";
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> action = mock(ThrowingSupplier.class);
        final Object result = new Object();
        when(action.get()).thenReturn(result);

        assertThat(UncheckedXteps.stepTo(stepName, action)).isSameAs(result);
        assertThatStepPassed(stepName, "", new Object[]{});
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
        assertThatStepPassed(stepName, stepDescription, new Object[]{});
        verify(action, times(1)).get();
    }

    @Test
    void stepsChainMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> action = mock(ThrowingRunnable.class);

        final NoCtxSC stepsChain = UncheckedXteps.stepsChain();
        assertThat(UncheckedXteps.stepsChain()).isNotSameAs(stepsChain);

        final String stepName = "stepsChainMethod";
        stepsChain.step(stepName, action);
        assertThatStepPassed(stepName, "", new Object[]{});
        verify(action, times(1)).run();
    }

    @Test
    void stepsChainOf1ArgMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, RuntimeException> action = mock(ThrowingConsumer.class);
        final Object arg1 = new Object();
        final String stepName = "stepsChainOf1ArgMethod";

        UncheckedXteps.stepsChainOf(arg1).step(stepName, action);
        assertThatStepPassed(stepName, "", new Object[]{arg1});
        verify(action, times(1)).accept(same(arg1));
    }

    @Test
    void stepsChainOf2ArgMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingBiConsumer<Object, Object, RuntimeException> action = mock(ThrowingBiConsumer.class);
        final Object arg1 = new Object();
        final Object arg2 = new Object();
        final String stepName = "stepsChainOf2ArgMethod";

        UncheckedXteps.stepsChainOf(arg1, arg2).step(stepName, action);
        assertThatStepPassed(stepName, "", new Object[]{arg1, arg2});
        verify(action, times(1)).accept(same(arg1), same(arg2));
    }

    @Test
    void stepsChainOf3ArgMethod() {
        @SuppressWarnings("unchecked")
        final ThrowingTriConsumer<Object, Object, Object, RuntimeException> action = mock(ThrowingTriConsumer.class);
        final Object arg1 = new Object();
        final Object arg2 = new Object();
        final Object arg3 = new Object();
        final String stepName = "stepsChainOf2ArgMethod";

        UncheckedXteps.stepsChainOf(arg1, arg2, arg3).step(stepName, action);
        assertThatStepPassed(stepName, "", new Object[]{arg1, arg2, arg3});
        verify(action, times(1)).accept(same(arg1), same(arg2), same(arg3));
    }

    @Test
    void stepsChainMethodWithContexts() {
        final Object context1 = new Object();
        final Object context2 = new Object();
        final Object context3 = new Object();

        final String stepName1 = "stepsChainMethodWithContexts 1";
        UncheckedXteps.stepsChain()
            .withCtx(context1)
            .step(stepName1, ctx -> { });
        assertThatStepPassed(stepName1, "", new Object[]{context1});

        final String stepName2 = "stepsChainMethodWithContexts 2";
        UncheckedXteps.stepsChain()
            .withCtx(context1).withCtx(context2)
            .step(stepName2, ctx -> { });
        assertThatStepPassed(stepName2, "", new Object[]{context2, context1});

        final String stepName3 = "stepsChainMethodWithContexts 3";
        UncheckedXteps.stepsChain()
            .withCtx(context1).withCtx(context2).withCtx(context3)
            .step(stepName3, ctx -> { });
        assertThatStepPassed(stepName3, "", new Object[]{context3, context2, context1});
    }

    @Test
    void chainWithAutoCloseableContextsIfActionFailed() {
        final RuntimeException baseException = new RuntimeException("base ex");
        final RuntimeException exception1 = new RuntimeException("ex 1");
        final ThrowingRunnable<?> hook1 = () -> { throw exception1; };
        final RuntimeException exception2 = new RuntimeException("ex 2");
        final ThrowingConsumer<Object, ?> hook2 = c -> { throw exception2; };

        assertThatCode(() ->
            UncheckedXteps.stepsChain()
                .chainHook(hook1)
                .withCtx(new Object())
                .chainHook(hook2)
                .action(ctx -> { throw baseException; })
        ).isSameAs(baseException)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void chainWithAutoCloseableContextsIfCloseMethodInvoked() {
        final RuntimeException exception1 = new RuntimeException("ex 1");
        final ThrowingRunnable<?> hook1 = () -> { throw exception1; };
        final RuntimeException exception2 = new RuntimeException("ex 2");
        final ThrowingConsumer<Object, ?> hook2 = c -> { throw exception2; };

        assertThatCode(() ->
            UncheckedXteps.stepsChain()
                .chainHook(hook1)
                .withCtx(new Object())
                .chainHook(hook2)
                .callChainHooks()
        ).isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    private static Pattern uuidPattern() {
        return Pattern.compile("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$");
    }

    public static final class StaticStepListener implements StepListener {
        private static String stepStartedUUID = null;
        private static String stepStartedName = null;
        private static String stepStartedDescription = null;
        private static Object[] stepStartedContexts = null;
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

        static Object[] stepStartedContexts() {
            final Object[] last = stepStartedContexts;
            stepStartedContexts = null;
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
        public void stepStarted(final String uuid,
                                final String name,
                                final String description,
                                final Object[] params) {
            stepStartedUUID = uuid;
            stepStartedName = name;
            stepStartedDescription = description;
            stepStartedContexts = params;
        }

        @Override
        public void stepPassed(final String uuid) {
            stepPassedUUID = uuid;
        }

        @Override
        public void stepFailed(final String uuid,
                               final Throwable exception) {
            stepFailedUUID = uuid;
            stepFailedException = exception;
        }
    }
}
