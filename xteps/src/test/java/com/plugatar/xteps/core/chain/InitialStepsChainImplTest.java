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

import com.plugatar.xteps.core.CtxStepsChain;
import com.plugatar.xteps.core.InitialStepsChain;
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
 * Tests for {@link InitialStepsChainImpl}.
 */
final class InitialStepsChainImplTest {

    @Test
    void ctorThrowsExceptionForNullStepReporter() {
        assertThatCode(() -> new InitialStepsChainImpl(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void withContextValueMethod() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final Object context = new Object();

        final CtxStepsChain<Object, InitialStepsChain> methodResult = chain.withContext(context);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextValueMethodForNullContext() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final Object context = null;

        final CtxStepsChain<Object, InitialStepsChain> methodResult = chain.withContext(context);
        assertThat(methodResult.context()).isNull();
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextSupplierMethodThrowsExceptionForNullSupplier() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final ThrowingSupplier<Object, RuntimeException> supplier = null;
        final String failedStepName = "InitialStepsChain withContext method contextSupplier arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;

        final CtxStepsChain<Object, InitialStepsChain> methodResult = chain.withContext(supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verifyNoInteractions(stepReporter);
    }

    @Test
    void withContextSupplierMethodThrowsException() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final RuntimeException exception = new RuntimeException("ex message");
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> { throw exception; };
        final String failedStepName = "InitialStepsChain withContext method throws exception";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain failedStep method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain failedStep method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain step method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String stepName = "step name";

        final InitialStepsChain methodResult = chain.step(stepName);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportEmptyStep(eq(stepName));
    }

    @Test
    void step2ArgsMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain step method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain step method step arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final ThrowingRunnable<RuntimeException> runnable = () -> {};
        final String stepName = "step name";

        final InitialStepsChain methodResult = chain.step(stepName, runnable);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportRunnableStep(eq(stepName), same(runnable));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain stepToContext method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain stepToContext method step arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final Object context = new Object();
        final ThrowingSupplier<Object, RuntimeException> supplier = () -> context;
        final String stepName = "step name";

        final CtxStepsChain<Object, InitialStepsChain> methodResult = chain.stepToContext(stepName, supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previousStepsChain()).isSameAs(chain);
        verify(stepReporter).reportSupplierStep(eq(stepName), same(supplier));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain stepTo method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain stepTo method step arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain nestedSteps method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain nestedSteps method stepsChain arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final ThrowingConsumer<InitialStepsChain, RuntimeException> consumer = c -> {};
        final String stepName = "step name";

        final InitialStepsChain methodResult = chain.nestedSteps(stepName, consumer);
        assertThat(methodResult).isSameAs(chain);
        verify(stepReporter).reportConsumerStep(eq(stepName), same(chain), same(consumer));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullStepName() throws Throwable {
        final StepReporter stepReporter = mockedStepReporter();
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain nestedStepsTo method stepName arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final String failedStepName = "InitialStepsChain nestedStepsTo method stepsChain arg is null";

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
        final InitialStepsChain chain = new InitialStepsChainImpl(stepReporter);
        final Object result = new Object();
        final ThrowingFunction<InitialStepsChain, Object, RuntimeException> function = c -> result;
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
}
