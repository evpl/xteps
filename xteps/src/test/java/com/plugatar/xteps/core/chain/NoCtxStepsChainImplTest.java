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
 * Tests for {@link NoCtxStepsChainImpl}.
 */
final class NoCtxStepsChainImplTest {

    @Test
    void ctorThrowsExceptionForNullStepReporter() {
        assertThatCode(() -> new NoCtxStepsChainImpl<>(null, new FakeStepsChain()))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullPrevious() {
        assertThatCode(() -> new NoCtxStepsChainImpl<>(mockedStepReporter(), null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void previousStepsChainMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final FakeStepsChain previousStepsChain = new FakeStepsChain();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, previousStepsChain);

        final FakeStepsChain methodResult = chain.previousStepsChain();
        assertThat(methodResult).isSameAs(previousStepsChain);
    }

    @Test
    void withContextValueMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final Object context = new Object();

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.withContext(context);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextValueMethodForNullContext() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final Object context = null;

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.withContext(context);
        assertThat(methodResult.context()).isNull();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextSupplierMethodThrowsExceptionForNullSupplier() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final ThrowingSupplier<Object, RuntimeException> supplier = null;
        final String failedStepName = "NoCtxStepsChain withContext method contextSupplier arg is null";

        final RuntimeException methodException = assertThrows(RuntimeException.class,
            () -> chain.withContext(supplier));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void withContextSupplierMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.withContext(supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextSupplierMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final RuntimeException exception = new RuntimeException("ex message");
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> { throw exception; };
        final String failedStepName = "NoCtxStepsChain withContext method throws exception";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.withContext(supplier));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName + " java.lang.RuntimeException: ex message")
            .hasCause(exception);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void failedStepMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain failedStep method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.failedStep(null, new RuntimeException()));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void failedStepMethodThrowsExceptionForNullException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain failedStep method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.failedStep(null, new RuntimeException()));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void failedStepMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String stepName = "step name";
        final RuntimeException exception = new RuntimeException();

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.failedStep(stepName, exception));
        assertThat(methodException).isSameAs(exception);
        verify(stepReporter).reportFailedStep(eq(stepName), same(methodException));
    }

    @Test
    void step1ArgMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain step method stepName arg is null";

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
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String stepName = "step name";

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.step(stepName);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportEmptyStep(eq(stepName));
    }

    @Test
    void step2ArgsMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain step method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.step(null, () -> {}));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void step2ArgsMethodThrowsExceptionForNullRunnable() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain step method step arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.step("step name", null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void step2ArgsMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final ThrowingRunnable<RuntimeException> runnable = () -> {};
        final String stepName = "step name";

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.step(stepName, runnable);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportRunnableStep(eq(stepName), same(runnable));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain stepToContext method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepToContext(null, Object::new));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullSupplier() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain stepToContext method step arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepToContext("step name", null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToContextMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;
        final String stepName = "step name";

        final CtxStepsChain<Object, NoCtxStepsChain<FakeStepsChain>> methodResult = chain.stepToContext(stepName, supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verify(stepReporter).reportSupplierStep(eq(stepName), same(supplier));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain stepTo method stepName arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepTo(null, Object::new));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToMethodThrowsExceptionForNullSupplier() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain stepTo method step arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.stepTo("step name", null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void stepToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final Object result = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> result;
        final String stepName = "step name";

        final Object methodResult = chain.stepTo(stepName, supplier);
        assertThat(methodResult).isSameAs(result);
        verify(stepReporter).reportSupplierStep(eq(stepName), same(supplier));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain nestedSteps method stepName arg is null";

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
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain nestedSteps method stepsChain arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.nestedSteps("step name", null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void nestedStepsMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final ThrowingConsumer<NoCtxStepsChain<FakeStepsChain>, RuntimeException> consumer = c -> {};
        final String stepName = "step name";

        final NoCtxStepsChain<FakeStepsChain> methodResult = chain.nestedSteps(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportConsumerStep(eq(stepName), same(chain), same(consumer));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain nestedStepsTo method stepName arg is null";

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
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final String failedStepName = "NoCtxStepsChain nestedStepsTo method stepsChain arg is null";

        final Throwable methodException = assertThrows(Throwable.class,
            () -> chain.nestedStepsTo("step name", null));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(failedStepName);
        verify(stepReporter).reportFailedStep(eq(failedStepName), same(methodException));
    }

    @Test
    void nestedStepsToMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final NoCtxStepsChain<FakeStepsChain> chain = new NoCtxStepsChainImpl<>(stepReporter, new FakeStepsChain());
        final Object result = new Object();
        final ThrowingFunction<NoCtxStepsChain<FakeStepsChain>, Object, RuntimeException> function = c -> result;
        final String stepName = "step name";

        final Object methodResult = chain.nestedStepsTo(stepName, function);
        assertThat(methodResult).isSameAs(result);
        verify(stepReporter).reportFunctionStep(eq(stepName), same(chain), same(function));
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
        public <E extends Throwable> void failedStep(
            final String stepName,
            final E exception
        ) throws E {

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
