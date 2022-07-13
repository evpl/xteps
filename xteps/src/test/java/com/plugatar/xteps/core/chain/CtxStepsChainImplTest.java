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
 * Tests for {@link CtxStepsChainImpl}.
 */
final class CtxStepsChainImplTest {

    @Test
    void ctorThrowsExceptionForNullStepReporter() {
        assertThatCode(() -> new CtxStepsChainImpl<>(
            (StepReporter) null, new Object(), new FakeStepsChain(), new ArrayDeque<>()
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullPrevious() {
        assertThatCode(() -> new CtxStepsChainImpl<>(
            mockedStepReporter(), new Object(), (BaseStepsChain<?>) null, new ArrayDeque<>()
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullACDeque() {
        assertThatCode(() -> new CtxStepsChainImpl<>(
            mockedStepReporter(), new Object(), new FakeStepsChain(), (Deque<AutoCloseable>) null
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorDoesntThrowExceptionForNullContext() {
        assertThatCode(() -> new CtxStepsChainImpl<>(
            mockedStepReporter(), (Object) null, new FakeStepsChain(), new ArrayDeque<>()
        )).doesNotThrowAnyException();
    }

    private static Stream<Arguments> nullArgsTestCases() {
        return Stream.of(
            Arguments.of("supplyContextTo method null consumer arg",
                action(chain -> chain.supplyContextTo((ThrowingConsumer<Object, RuntimeException>) null))),
            Arguments.of("applyContextTo method null function arg",
                action(chain -> chain.applyContextTo((ThrowingFunction<Object, Object, RuntimeException>) null))),
            Arguments.of("withContext method null function arg",
                action(chain -> chain.withContext((ThrowingFunction<Object, Object, RuntimeException>) null))),

            Arguments.of("step method null stepName arg",
                action(chain -> chain.step((String) null))),

            Arguments.of("step method with description null stepName arg",
                action(chain -> chain.step((String) null, ""))),
            Arguments.of("step method with description null stepDescription arg",
                action(chain -> chain.step("", (String) null))),

            Arguments.of("step method with action null stepName arg",
                action(chain -> chain.step((String) null, c -> { }))),
            Arguments.of("step method with action null consumer arg",
                action(chain -> chain.step("", (ThrowingConsumer<Object, RuntimeException>) null))),

            Arguments.of("step method with description and action null stepName arg",
                action(chain -> chain.step((String) null, "", c -> { }))),
            Arguments.of("step method with description and action null stepDescription arg",
                action(chain -> chain.step("", (String) null, c -> { }))),
            Arguments.of("step method with description and action null consumer arg",
                action(chain -> chain.step("", "", (ThrowingConsumer<Object, RuntimeException>) null))),

            Arguments.of("stepToContext method null stepName arg",
                action(chain -> chain.stepToContext((String) null, c -> new Object()))),
            Arguments.of("stepToContext method null function arg",
                action(chain -> chain.stepToContext("", (ThrowingFunction<Object, Object, RuntimeException>) null))),

            Arguments.of("stepToContext method with description null stepName arg",
                action(chain -> chain.stepToContext((String) null, "", c -> new Object()))),
            Arguments.of("stepToContext method with description null stepDescription arg",
                action(chain -> chain.stepToContext("", (String) null, c -> new Object()))),
            Arguments.of("stepToContext method with description null function arg",
                action(chain -> chain.stepToContext("", "", (ThrowingFunction<Object, Object, RuntimeException>) null))),

            Arguments.of("stepTo method null stepName arg",
                action(chain -> chain.stepTo((String) null, c -> new Object()))),
            Arguments.of("stepTo method null function arg",
                action(chain -> chain.stepTo("", (ThrowingFunction<Object, Object, RuntimeException>) null))),

            Arguments.of("stepTo method with description null stepName arg",
                action(chain -> chain.stepTo((String) null, "", c -> new Object()))),
            Arguments.of("stepTo method with description null stepDescription arg",
                action(chain -> chain.stepTo("", (String) null, c -> new Object()))),
            Arguments.of("stepTo method with description null function arg",
                action(chain -> chain.stepTo("", "", (ThrowingFunction<Object, Object, RuntimeException>) null))),

            Arguments.of("nestedSteps method null stepName arg",
                action(chain -> chain.nestedSteps((String) null, c -> { }))),
            Arguments.of("nestedSteps method null consumer arg",
                action(chain -> chain.nestedSteps("", (ThrowingConsumer<CtxStepsChain<Object, FakeStepsChain>, RuntimeException>) null))),

            Arguments.of("nestedSteps method null stepName arg",
                action(chain -> chain.nestedSteps((String) null, "", c -> { }))),
            Arguments.of("nestedSteps method null stepDescription arg",
                action(chain -> chain.nestedSteps("", (String) null, c -> { }))),
            Arguments.of("nestedSteps method null consumer arg",
                action(chain -> chain.nestedSteps("", "", (ThrowingConsumer<CtxStepsChain<Object, FakeStepsChain>, RuntimeException>) null))),

            Arguments.of("nestedStepsTo method null stepName arg",
                action(chain -> chain.nestedStepsTo((String) null, c -> new Object()))),
            Arguments.of("nestedStepsTo method null function arg",
                action(chain -> chain.nestedStepsTo("", (ThrowingFunction<CtxStepsChain<Object, FakeStepsChain>, Object, RuntimeException>) null))),

            Arguments.of("nestedStepsTo method null stepName arg",
                action(chain -> chain.nestedStepsTo((String) null, "", c -> new Object()))),
            Arguments.of("nestedStepsTo method null stepDescription arg",
                action(chain -> chain.nestedStepsTo("", (String) null, c -> new Object()))),
            Arguments.of("nestedStepsTo method null function arg",
                action(chain -> chain.nestedStepsTo("", "", (ThrowingFunction<CtxStepsChain<Object, FakeStepsChain>, Object, RuntimeException>) null)))
        );
    }

    private static Consumer<CtxStepsChain<Object, FakeStepsChain>> action(
        final Consumer<CtxStepsChain<Object, FakeStepsChain>> consumer
    ) {
        return consumer;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nullArgsTestCases")
    void methodsWithNullArgsThrowsException(final String testCaseName,
                                            final Consumer<CtxStepsChain<Object, FakeStepsChain>> action) throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), new ArrayDeque<>()
        );
        assertThatCode(() -> action.accept(chain))
            .isInstanceOf(XtepsException.class);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void previousStepsChainMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final FakeStepsChain previousStepsChain = new FakeStepsChain();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), previousStepsChain, acDeque
        );

        final FakeStepsChain methodResult = chain.previousStepsChain();
        assertThat(methodResult).isSameAs(previousStepsChain);
        assertThat(acDeque).isEmpty();
    }

    @Test
    void contextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );

        final Object methodResult = chain.context();
        assertThat(methodResult).isSameAs(context);
        assertThat(acDeque).isEmpty();
    }

    @Test
    void supplyContextToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> { };

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.supplyContextTo(consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void supplyContextToMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final RuntimeException exception = new RuntimeException();
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> { throw exception; };

        assertThatCode(() -> chain.supplyContextTo(consumer))
            .isSameAs(exception);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void applyContextToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final Object functionResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> functionResult;

        final Object methodResult = chain.applyContextTo(function);
        assertThat(methodResult).isSameAs(functionResult);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void applyContextToMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final RuntimeException exception = new RuntimeException();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> { throw exception; };

        assertThatCode(() -> chain.applyContextTo(function))
            .isSameAs(exception);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextValueMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final Object newContext = new Object();

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withContext(newContext);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextValueMethodNullContext() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final Object newContext = null;

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withContext(newContext);
        assertThat(methodResult.context()).isNull();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextFunctionMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final Object newContext = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> newContext;

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withContext(function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextFunctionMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final RuntimeException exception = new RuntimeException();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> { throw exception; };

        assertThatCode(() -> chain.withContext(function))
            .isSameAs(exception);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withoutContextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );

        final NoCtxStepsChain<CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withoutContext();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void stepMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.step(stepName);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportEmptyStep(eq(stepName), eq(""));
    }

    @Test
    void stepMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.step(stepName, stepDescription);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportEmptyStep(eq(stepName), eq(stepDescription));
    }

    @Test
    void stepMethodWithAction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> { };

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.step(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportConsumerStep(eq(stepName), eq(""), same(context), same(consumer));
    }

    @Test
    void stepMethodWithDescriptionAndAction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> { };

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.step(stepName, stepDescription, consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportConsumerStep(eq(stepName), eq(stepDescription), same(context), same(consumer));
    }

    @Test
    void stepToContextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final Object newContext = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> newContext;

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult =
            chain.stepToContext(stepName, function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(""), same(context), same(function));
    }

    @Test
    void stepToContextMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final Object newContext = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> newContext;

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult =
            chain.stepToContext(stepName, stepDescription, function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(stepDescription), same(context), same(function));
    }

    @Test
    void stepToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final Object functionResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> functionResult;

        final Object methodResult = chain.stepTo(stepName, function);
        assertThat(methodResult).isSameAs(functionResult);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(""), same(context), same(function));
    }

    @Test
    void stepToMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final Object functionResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> functionResult;

        final Object methodResult = chain.stepTo(stepName, stepDescription, function);
        assertThat(methodResult).isSameAs(functionResult);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(stepDescription), same(context), same(function));
    }

    @Test
    void nestedStepsMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final ThrowingConsumer<CtxStepsChain<Object, FakeStepsChain>, RuntimeException> consumer = c -> { };

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.nestedSteps(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportConsumerStep(eq(stepName), eq(""), same(chain), same(consumer));
    }

    @Test
    void nestedStepsMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final ThrowingConsumer<CtxStepsChain<Object, FakeStepsChain>, RuntimeException> consumer = c -> { };

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.nestedSteps(stepName, stepDescription, consumer);
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportConsumerStep(eq(stepName), eq(stepDescription), same(chain), same(consumer));
    }

    @Test
    void nestedStepsToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final Object functionResult = new Object();
        final ThrowingFunction<CtxStepsChain<Object, FakeStepsChain>, Object, RuntimeException> function =
            c -> functionResult;

        final Object methodResult = chain.nestedStepsTo(stepName, function);
        assertThat(methodResult).isSameAs(functionResult);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(""), same(chain), same(function));
    }

    @Test
    void nestedStepsToMethodWithDescription() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain(), acDeque
        );
        final String stepName = "step name";
        final String stepDescription = "step description";
        final Object functionResult = new Object();
        final ThrowingFunction<CtxStepsChain<Object, FakeStepsChain>, Object, RuntimeException> function =
            c -> functionResult;

        final Object methodResult = chain.nestedStepsTo(stepName, stepDescription, function);
        assertThat(methodResult).isSameAs(functionResult);
        assertThat(acDeque).isEmpty();
        verify(stepReporter).reportFunctionStep(eq(stepName), eq(stepDescription), same(chain), same(function));
    }

    @Test
    void contextIsAutoClosableMethodThrowsExceptionForNonAutoClosableContext() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );

        assertThatCode(() -> chain.contextIsAutoClosable())
            .isInstanceOf(XtepsException.class);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void contextIsAutoClosableMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final AutoCloseable autoCloseable1 = mock(AutoCloseable.class);
        final AutoCloseable autoCloseable2 = mock(AutoCloseable.class);
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, autoCloseable1, new FakeStepsChain(), acDeque
        );

        chain.contextIsAutoClosable();
        assertThat(acDeque).containsExactly(autoCloseable1);

        chain.contextIsAutoClosable();
        assertThat(acDeque).containsExactly(autoCloseable1, autoCloseable1);

        chain.withContext(autoCloseable2).contextIsAutoClosable();
        assertThat(acDeque).containsExactly(autoCloseable1, autoCloseable1, autoCloseable2);

        verifyNoInteractions(autoCloseable1);
        verifyNoInteractions(autoCloseable2);
        verifyNoInteractions(stepReporter);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nullArgsTestCases")
    void autoClosableContextsIfException(final String testCaseName,
                                         final Consumer<CtxStepsChain<Object, FakeStepsChain>> action) throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final AutoCloseable[] autoCloseables = mockedAutoClosable(3);
        final RuntimeException exception1 = new RuntimeException();
        doThrow(exception1).when(autoCloseables[0]).close();
        final RuntimeException exception2 = new RuntimeException();
        doThrow(exception2).when(autoCloseables[2]).close();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        acDeque.offerLast(autoCloseables[0]);
        acDeque.offerLast(autoCloseables[1]);
        acDeque.offerLast(autoCloseables[2]);
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
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
    void closeAutoClosableContextsMethodWithEmptyDeque() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );

        final Object methodResult = chain.closeAutoClosableContexts();
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void closeAutoClosableContextsMethodWithNonEmptyDeque() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final AutoCloseable[] autoCloseables = mockedAutoClosable(3);
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        acDeque.offerLast(autoCloseables[0]);
        acDeque.offerLast(autoCloseables[1]);
        acDeque.offerLast(autoCloseables[2]);
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );

        final Object methodResult = chain.closeAutoClosableContexts();
        assertThat(methodResult).isSameAs(chain);
        assertThat(acDeque).isEmpty();
        verify(autoCloseables[0], times(1)).close();
        verify(autoCloseables[1], times(1)).close();
        verify(autoCloseables[2], times(1)).close();
        verifyNoInteractions(stepReporter);
    }

    @Test
    void closeAutoClosableContextsMethodWithThrowingAutoClosablesDeque() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final AutoCloseable[] autoCloseables = mockedAutoClosable(3);
        final RuntimeException exception1 = new RuntimeException();
        doThrow(exception1).when(autoCloseables[0]).close();
        final RuntimeException exception2 = new RuntimeException();
        doThrow(exception2).when(autoCloseables[2]).close();
        final Deque<AutoCloseable> acDeque = new ArrayDeque<>();
        acDeque.offerLast(autoCloseables[0]);
        acDeque.offerLast(autoCloseables[1]);
        acDeque.offerLast(autoCloseables[2]);
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain(), acDeque
        );

        assertThatCode(() -> chain.closeAutoClosableContexts())
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

    private static AutoCloseable[] mockedAutoClosable(final int count) {
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
