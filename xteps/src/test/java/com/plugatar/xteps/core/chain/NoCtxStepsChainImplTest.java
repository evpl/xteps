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
package com.plugatar.xteps.core.chain;

import com.plugatar.xteps.core.BaseStepsChain;
import com.plugatar.xteps.core.CtxStepsChain;
import com.plugatar.xteps.core.NoCtxStepsChain;
import com.plugatar.xteps.core.StepReporter;
import com.plugatar.xteps.core.XtepsException;
import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link NoCtxStepsChainImpl}.
 */
final class NoCtxStepsChainImplTest {

    @Test
    void ctorThrowsExceptionForNullStepReporter() {
        assertThatCode(() -> new NoCtxStepsChainImpl<>(
            (StepReporter) null, new FakeStepsChain(), new ArrayDeque<>()
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullPrevious() {
        assertThatCode(() -> new NoCtxStepsChainImpl<>(
            mockedStepReporter(), (BaseStepsChain<?>) null, new ArrayDeque<>()
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullACDeque() {
        assertThatCode(() -> new NoCtxStepsChainImpl<>(
            mockedStepReporter(), new FakeStepsChain(), (Deque<AutoCloseable>) null
        )).isInstanceOf(NullPointerException.class);
    }

    private static Stream<Arguments> nullArgsTestCases() {
        return Stream.of(
            Arguments.of("withContext method null supplier arg",
                action(chain -> chain.withContext((ThrowingSupplier<Object, RuntimeException>) null))),

            Arguments.of("step method null stepName arg",
                action(chain -> chain.step((String) null))),

            Arguments.of("step method with description null stepName arg",
                action(chain -> chain.step((String) null, ""))),
            Arguments.of("step method with description null stepDescription arg",
                action(chain -> chain.step("", (String) null))),

            Arguments.of("step method with action null stepName arg",
                action(chain -> chain.step((String) null, () -> { }))),
            Arguments.of("step method with action null runnable arg",
                action(chain -> chain.step("", (ThrowingRunnable<RuntimeException>) null))),

            Arguments.of("step method with description and action null stepName arg",
                action(chain -> chain.step((String) null, "", () -> { }))),
            Arguments.of("step method with description and action null stepDescription arg",
                action(chain -> chain.step("", (String) null, () -> { }))),
            Arguments.of("step method with description and action null runnable arg",
                action(chain -> chain.step("", "", (ThrowingRunnable<RuntimeException>) null))),

            Arguments.of("stepToContext method null stepName arg",
                action(chain -> chain.stepToContext((String) null, Object::new))),
            Arguments.of("stepToContext method null supplier arg",
                action(chain -> chain.stepToContext("", (ThrowingSupplier<Object, RuntimeException>) null))),

            Arguments.of("stepToContext method with description null stepName arg",
                action(chain -> chain.stepToContext((String) null, "", Object::new))),
            Arguments.of("stepToContext method with description null stepDescription arg",
                action(chain -> chain.stepToContext("", (String) null, Object::new))),
            Arguments.of("stepToContext method with description null supplier arg",
                action(chain -> chain.stepToContext("", "", (ThrowingSupplier<Object, RuntimeException>) null))),

            Arguments.of("stepTo method null stepName arg",
                action(chain -> chain.stepTo((String) null, Object::new))),
            Arguments.of("stepTo method null supplier arg",
                action(chain -> chain.stepTo("", (ThrowingSupplier<Object, RuntimeException>) null))),

            Arguments.of("stepTo method with description null stepName arg",
                action(chain -> chain.stepTo((String) null, "", Object::new))),
            Arguments.of("stepTo method with description null stepDescription arg",
                action(chain -> chain.stepTo("", (String) null, Object::new))),
            Arguments.of("stepTo method with description null supplier arg",
                action(chain -> chain.stepTo("", "", (ThrowingSupplier<Object, RuntimeException>) null))),

            Arguments.of("nestedSteps method null stepName arg",
                action(chain -> chain.nestedSteps((String) null, c -> { }))),
            Arguments.of("nestedSteps method null consumer arg",
                action(chain -> chain.nestedSteps("", (ThrowingConsumer<NoCtxStepsChain<FakeStepsChain>, RuntimeException>) null))),

            Arguments.of("nestedSteps method null stepName arg",
                action(chain -> chain.nestedSteps((String) null, "", c -> { }))),
            Arguments.of("nestedSteps method null stepDescription arg",
                action(chain -> chain.nestedSteps("", (String) null, c -> { }))),
            Arguments.of("nestedSteps method null consumer arg",
                action(chain -> chain.nestedSteps("", "", (ThrowingConsumer<NoCtxStepsChain<FakeStepsChain>, RuntimeException>) null))),

            Arguments.of("nestedStepsTo method null stepName arg",
                action(chain -> chain.nestedStepsTo((String) null, c -> new Object()))),
            Arguments.of("nestedStepsTo method null function arg",
                action(chain -> chain.nestedStepsTo("", (ThrowingFunction<NoCtxStepsChain<FakeStepsChain>, Object, RuntimeException>) null))),

            Arguments.of("nestedStepsTo method null stepName arg",
                action(chain -> chain.nestedStepsTo((String) null, "", c -> new Object()))),
            Arguments.of("nestedStepsTo method null stepDescription arg",
                action(chain -> chain.nestedStepsTo("", (String) null, c -> new Object()))),
            Arguments.of("nestedStepsTo method null function arg",
                action(chain -> chain.nestedStepsTo("", "", (ThrowingFunction<NoCtxStepsChain<FakeStepsChain>, Object, RuntimeException>) null)))
        );
    }

    private static Consumer<NoCtxStepsChain<FakeStepsChain>> action(
        final Consumer<NoCtxStepsChain<FakeStepsChain>> consumer
    ) {
        return consumer;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nullArgsTestCases")
    void methodsWithNullArgsThrowsException(final String testCaseName,
                                            final Consumer<NoCtxStepsChain<FakeStepsChain>> action) throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );

        assertThatCode(() -> action.accept(chain))
            .isInstanceOf(XtepsException.class);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void previousStepsChainMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final FakeStepsChain previousStepsChain = new FakeStepsChain();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, previousStepsChain, acDeque
        );

        final FakeStepsChain methodResult = chain.previousStepsChain();
        assertThat(methodResult).isSameAs(previousStepsChain);
        assertThat(acDeque).isEmpty();
    }

    @Test
    void withContextValueMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final Object context = new Object();

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.withContext(context);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextValueMethodNullContext() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final Object context = null;

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.withContext(context);
        assertThat(methodResult.context()).isNull();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextSupplierMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.withContext(supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextSupplierMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final RuntimeException exception = new RuntimeException();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> { throw exception; };

        assertThatCode(() -> chain.withContext(supplier))
            .isSameAs(exception);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void stepMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.step(stepName);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportEmptyStep(eq(stepName), eq(""));
    }

    @Test
    void stepMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.step(stepName, stepDescription);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportEmptyStep(eq(stepName), eq(stepDescription));
    }

    @Test
    void stepMethodWithAction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final ThrowingRunnable<RuntimeException> runnable = () -> { };

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.step(stepName, runnable);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportRunnableStep(eq(stepName), eq(""), same(runnable));
    }

    @Test
    void stepMethodWithDescriptionAndAction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final ThrowingRunnable<RuntimeException> runnable = () -> { };

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.step(stepName, stepDescription, runnable);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportRunnableStep(eq(stepName), eq(stepDescription), same(runnable));
    }

    @Test
    void stepToContextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.stepToContext(stepName, supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportSupplierStep(eq(stepName), eq(""), same(supplier));
    }

    @Test
    void stepToContextMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.stepToContext(stepName, stepDescription, supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportSupplierStep(eq(stepName), eq(stepDescription), same(supplier));
    }

    @Test
    void stepToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final Object result = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> result;

        final Object methodResult = chain.stepTo(stepName, supplier);
        assertThat(methodResult).isSameAs(result);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportSupplierStep(eq(stepName), eq(""), same(supplier));
    }

    @Test
    void stepToMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final Object result = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> result;

        final Object methodResult = chain.stepTo(stepName, stepDescription, supplier);
        assertThat(methodResult).isSameAs(result);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportSupplierStep(eq(stepName), eq(stepDescription), same(supplier));
    }

    @Test
    void nestedStepsMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final ThrowingConsumer<NoCtxStepsChain<FakeStepsChain>, RuntimeException> consumer = c -> { };

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.nestedSteps(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportConsumerStep(eq(stepName), eq(""), same(chain), same(consumer));
    }

    @Test
    void nestedStepsMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final ThrowingConsumer<NoCtxStepsChain<FakeStepsChain>, RuntimeException> consumer = c -> { };

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.nestedSteps(stepName, stepDescription, consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportConsumerStep(eq(stepName), eq(stepDescription), same(chain), same(consumer));
    }

    @Test
    void nestedStepsToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final Object result = new Object();
        final ThrowingFunction<NoCtxStepsChain<FakeStepsChain>, Object, RuntimeException> function = c -> result;

        final Object methodResult = chain.nestedStepsTo(stepName, function);
        assertThat(methodResult).isSameAs(result);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(""), same(chain), same(function));
    }

    @Test
    void nestedStepsToMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final Object result = new Object();
        final ThrowingFunction<NoCtxStepsChain<FakeStepsChain>, Object, RuntimeException> function = c -> result;

        final Object methodResult = chain.nestedStepsTo(stepName, stepDescription, function);
        assertThat(methodResult).isSameAs(result);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(stepDescription), same(chain), same(function));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nullArgsTestCases")
    void AutoCloseableContextsIfException(final String testCaseName,
                                         final Consumer<NoCtxStepsChain<FakeStepsChain>> action) throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final AutoCloseable[] autoCloseables = mockedAutoCloseable(3);
        final RuntimeException exception1 = new RuntimeException();
        doThrow(exception1).when(autoCloseables[0]).close();
        final RuntimeException exception2 = new RuntimeException();
        doThrow(exception2).when(autoCloseables[2]).close();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        acDeque.offerLast(autoCloseables[0]);
        acDeque.offerLast(autoCloseables[1]);
        acDeque.offerLast(autoCloseables[2]);
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );

        assertThatCode(() -> action.accept(chain))
            .isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
        assertThat(acDeque).isEmpty();
        verify(autoCloseables[0], times(1)).close();
        verify(autoCloseables[1], times(1)).close();
        verify(autoCloseables[2], times(1)).close();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void closeAutoCloseableContextsMethodWithEmptyDeque() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );

        final Object methodResult = chain.closeAutoCloseableContexts();
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void closeAutoCloseableContextsMethodWithNonEmptyDeque() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final AutoCloseable[] autoCloseables = mockedAutoCloseable(3);
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        acDeque.offerLast(autoCloseables[0]);
        acDeque.offerLast(autoCloseables[1]);
        acDeque.offerLast(autoCloseables[2]);
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );

        final Object methodResult = chain.closeAutoCloseableContexts();
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(autoCloseables[0], times(1)).close();
        verify(autoCloseables[1], times(1)).close();
        verify(autoCloseables[2], times(1)).close();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void closeAutoCloseableContextsMethodWithThrowingAutoCloseablesDeque() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final AutoCloseable[] autoCloseables = mockedAutoCloseable(3);
        final RuntimeException exception1 = new RuntimeException();
        doThrow(exception1).when(autoCloseables[0]).close();
        final RuntimeException exception2 = new RuntimeException();
        doThrow(exception2).when(autoCloseables[2]).close();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        acDeque.offerLast(autoCloseables[0]);
        acDeque.offerLast(autoCloseables[1]);
        acDeque.offerLast(autoCloseables[2]);
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(
            stepReporter, new FakeStepsChain(), acDeque
        );

        assertThatCode(() -> chain.closeAutoCloseableContexts())
            .isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
        assertThat(acDeque).isEmpty();
        verify(autoCloseables[0], times(1)).close();
        verify(autoCloseables[1], times(1)).close();
        verify(autoCloseables[2], times(1)).close();
        verifyNoInteractions(stepReporter);
    }

    private static StepReporter mockedStepReporter() throws Throwable {
        final StepReporter stepReporter = mock(StepReporter.class);
        doAnswer(in -> {
            ((ThrowingRunnable<?>) in.getArgument(2)).run();
            return null;
        }).when(stepReporter).reportRunnableStep(any(), any(), any());
        doAnswer(in -> {
            ((ThrowingConsumer<?, ?>) in.getArgument(3)).accept(in.getArgument(2));
            return null;
        }).when(stepReporter).reportConsumerStep(any(), any(), any(), any());
        doAnswer(in -> {
            return ((ThrowingSupplier<?, ?>) in.getArgument(2)).get();
        }).when(stepReporter).reportSupplierStep(any(), any(), any());
        doAnswer(in -> {
            return ((ThrowingFunction<?, ?, ?>) in.getArgument(3)).apply(in.getArgument(2));
        }).when(stepReporter).reportFunctionStep(any(), any(), any(), any());
        return stepReporter;
    }

    private static AutoCloseable[] mockedAutoCloseable(final int count) {
        final AutoCloseable[] array = new AutoCloseable[count];
        for (int idx = 0; idx < count; ++idx) {
            array[idx] = mock(AutoCloseable.class);
        }
        return array;
    }

    private static class FakeStepsChain implements BaseStepsChain<FakeStepsChain> {

        private FakeStepsChain() {
        }

        @Override
        public FakeStepsChain step(final String stepName) {
            return null;
        }

        @Override
        public FakeStepsChain step(final String stepName,
                                   final String stepDescription) {
            return null;
        }

        @Override
        public <E extends Throwable> FakeStepsChain nestedSteps(
            final String stepName,
            final ThrowingConsumer<FakeStepsChain, ? extends E> stepsChain
        ) throws E {
            return null;
        }

        @Override
        public <E extends Throwable> FakeStepsChain nestedSteps(
            final String stepName,
            final String stepDescription,
            final ThrowingConsumer<FakeStepsChain, ? extends E> stepsChain
        ) throws E {
            return null;
        }

        @Override
        public <R, E extends Throwable> R nestedStepsTo(
            final String stepName,
            final ThrowingFunction<FakeStepsChain, ? extends R, ? extends E> stepsChain
        ) throws E {
            return null;
        }

        @Override
        public <R, E extends Throwable> R nestedStepsTo(
            final String stepName,
            final String stepDescription,
            final ThrowingFunction<FakeStepsChain, ? extends R, ? extends E> stepsChain
        ) throws E {
            return null;
        }
    }
}
