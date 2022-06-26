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

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link CtxStepsChainImpl}.
 */
final class CtxStepsChainImplTest {

    @Test
    void ctorThrowsExceptionForNullStepReporter() {
        assertThatCode(() -> new CtxStepsChainImpl<>(null, new Object(), new FakeStepsChain()))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullPrevious() {
        assertThatCode(() -> new CtxStepsChainImpl<>(mockedStepReporter(), new Object(), null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void previousStepsChainMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final FakeStepsChain previousStepsChain = new FakeStepsChain();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), previousStepsChain
        );

        final FakeStepsChain methodResult = chain.previousStepsChain();
        assertThat(methodResult).isSameAs(previousStepsChain);
    }

    @Test
    void contextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain()
        );

        final Object methodResult = chain.context();
        assertThat(methodResult).isSameAs(context);
    }

    @Test
    void supplyContextToMethodThrowsExceptionForNullConsumer() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final ThrowingConsumer<Object, RuntimeException> consumer = null;
        final String failedStepName = "CtxStepsChain supplyContextTo method consumer arg is null";

        final RuntimeException methodException = assertThrows(RuntimeException.class,
            () -> chain.supplyContextTo(consumer));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void supplyContextToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> {};

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.supplyContextTo(consumer);
        assertThat(methodResult).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void supplyContextToMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final RuntimeException exception = new RuntimeException("ex message");
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> { throw exception; };
        final String failedStepName = "CtxStepsChain supplyContextTo method throws exception";

        final RuntimeException methodException = assertThrows(RuntimeException.class,
            () -> chain.supplyContextTo(consumer));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName + " java.lang.RuntimeException: ex message");
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void applyContextToMethodThrowsExceptionForNullFunction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final ThrowingFunction<Object, Object, RuntimeException> function = null;
        final String failedStepName = "CtxStepsChain applyContextTo method function arg is null";

        final RuntimeException methodException = assertThrows(RuntimeException.class,
            () -> chain.applyContextTo(function));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void applyContextToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final Object functionResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> functionResult;

        final Object methodResult = chain.applyContextTo(function);
        assertThat(methodResult).isSameAs(functionResult);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void applyContextToMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final RuntimeException exception = new RuntimeException("ex message");
        final ThrowingFunction<Object, Object, RuntimeException> consumer = c -> { throw exception; };
        final String failedStepName = "CtxStepsChain applyContextTo method throws exception";

        final RuntimeException methodException = assertThrows(RuntimeException.class,
            () -> chain.applyContextTo(consumer));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName + " java.lang.RuntimeException: ex message");
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void withContextValueMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final Object newContext = new Object();

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withContext(newContext);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextValueMethodForNullContext() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final Object newContext = null;

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withContext(newContext);
        assertThat(methodResult.context()).isNull();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextFunctionMethodThrowsExceptionForNullFunction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final ThrowingFunction<Object, Object, RuntimeException> function = null;
        final String failedStepName = "CtxStepsChain withContext method contextFunction arg is null";

        final RuntimeException methodException = assertThrows(RuntimeException.class,
            () -> chain.withContext(function));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void withContextFunctionMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final Object newContext = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> newContext;

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withContext(function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextFunctionMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final RuntimeException exception = new RuntimeException("ex message");
        final ThrowingFunction<Object, Object, RuntimeException> supplier = c -> { throw exception; };
        final String failedStepName = "CtxStepsChain withContext method throws exception";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.withContext(supplier));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName + " java.lang.RuntimeException: ex message")
            .hasCause(exception);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void withoutContextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );

        final NoCtxStepsChain<CtxStepsChain<Object, FakeStepsChain>> methodResult = chain.withoutContext();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void step1ArgMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain step method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.step(null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void step1ArgMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String stepName = randomStepName();

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.step(stepName);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportEmptyStep(eq(stepName));
    }

    @Test
    void step2ArgsMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain step method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.step(null, c -> {}));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void step2ArgsMethodThrowsExceptionForNullRunnable() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain step method step arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.step(randomStepName(), null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void step2ArgsMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain()
        );
        final ThrowingConsumer<Object, RuntimeException> consumer = c -> {};
        final String stepName = randomStepName();

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.step(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportConsumerStep(eq(stepName), same(context), same(consumer));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain stepToContext method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepToContext(null, c -> new Object()));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullFunction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain stepToContext method step arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepToContext(randomStepName(), null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToContextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain()
        );
        final Object newContext = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> newContext;
        final String stepName = randomStepName();

        final CtxStepsChain<Object, CtxStepsChain<Object, FakeStepsChain>> methodResult =
            chain.stepToContext(stepName, function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verify(stepReporter).reportFunctionStep(eq(stepName), same(context), same(function));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain stepTo method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepTo(null, c -> new Object()));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToMethodThrowsExceptionForNullSupplier() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain stepTo method step arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepTo(randomStepName(), null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain()
        );
        final Object functionResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> function = c -> functionResult;
        final String stepName = randomStepName();

        final Object methodResult = chain.stepTo(stepName, function);
        assertThat(methodResult).isSameAs(functionResult);
        verify(stepReporter).reportFunctionStep(eq(stepName), same(context), same(function));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain nestedSteps method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.nestedSteps(null, c -> {}));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullConsumer() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain nestedSteps method stepsChain arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.nestedSteps(randomStepName(), null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void nestedStepsMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final ThrowingConsumer<CtxStepsChain<Object, FakeStepsChain>, RuntimeException> consumer = c -> {};
        final String stepName = randomStepName();

        final CtxStepsChain<Object, FakeStepsChain> methodResult = chain.nestedSteps(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportConsumerStep(eq(stepName), same(chain), same(consumer));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain nestedStepsTo method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.nestedStepsTo(null, c -> new Object()));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullFunction() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, new Object(), new FakeStepsChain()
        );
        final String failedStepName = "CtxStepsChain nestedStepsTo method stepsChain arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.nestedStepsTo(randomStepName(), null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void nestedStepsToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final Object context = new Object();
        final CtxStepsChain<Object, FakeStepsChain> chain = new CtxStepsChainImpl<>(
            stepReporter, context, new FakeStepsChain()
        );
        final Object functionResult = new Object();
        final ThrowingFunction<CtxStepsChain<Object, FakeStepsChain>, Object, RuntimeException> function =
            c -> functionResult;
        final String stepName = randomStepName();

        final Object methodResult = chain.nestedStepsTo(stepName, function);
        assertThat(methodResult).isSameAs(functionResult);
        verify(stepReporter).reportFunctionStep(eq(stepName), same(chain), same(function));
    }

    private static String randomStepName() {
        final Random random = new Random();
        byte[] byteArray = new byte[random.nextInt(9) + 1];
        random.nextBytes(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private static StepReporter mockedStepReporter() throws Throwable {
        final StepReporter stepReporter = mock(StepReporter.class);
        doAnswer(in -> {
            throw (Throwable) in.getArgument(1);
        }).when(stepReporter).reportFailedStep(any(), any());
        doAnswer(in -> {
            ((ThrowingRunnable<?>) in.getArgument(1)).run();
            return null;
        }).when(stepReporter).reportRunnableStep(any(), any());
        doAnswer(in -> {
            ((ThrowingConsumer<?, ?>) in.getArgument(2)).accept(in.getArgument(1));
            return null;
        }).when(stepReporter).reportConsumerStep(any(), any(), any());
        doAnswer(in -> {
            return ((ThrowingSupplier<?, ?>) in.getArgument(1)).get();
        }).when(stepReporter).reportSupplierStep(any(), any());
        doAnswer(in -> {
            return ((ThrowingFunction<?, ?, ?>) in.getArgument(2)).apply(in.getArgument(1));
        }).when(stepReporter).reportFunctionStep(any(), any(), any());
        return stepReporter;
    }

    private static class FakeStepsChain implements BaseStepsChain<FakeStepsChain> {

        private FakeStepsChain() {
        }

        @Override
        public <U> CtxStepsChain<U, FakeStepsChain> withContext(final U context) {
            return null;
        }

        @Override
        public FakeStepsChain step(final String stepName) {
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
        public <R, E extends Throwable> R nestedStepsTo(
            final String stepName,
            final ThrowingFunction<FakeStepsChain, ? extends R, ? extends E> stepsChain
        ) throws E {
            return null;
        }
    }
}
