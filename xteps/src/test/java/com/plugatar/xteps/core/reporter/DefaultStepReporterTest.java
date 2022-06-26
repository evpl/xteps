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
package com.plugatar.xteps.core.reporter;

import com.plugatar.xteps.Unchecked;
import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.XtepsException;
import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultStepReporter}.
 */
final class DefaultStepReporterTest {

    @Test
    void ctorThrowsExceptionForNullStepListenerArray() {
        assertThatCode(() -> new DefaultStepReporter(null))
            .isInstanceOf(NullPointerException.class)
            .is(containsXtepsStackTrace());
    }

    @Test
    void ctorThrowsExceptionForNullStepListenerInArray() {
        final StepListener[] stepListeners = {mock(StepListener.class), null, mock(StepListener.class)};

        assertThatCode(() -> new DefaultStepReporter(stepListeners))
            .isInstanceOf(NullPointerException.class)
            .is(containsXtepsStackTrace());
    }

    @Test
    void ctorDoesntThrowExceptionForEmptyStepListenerArray() {
        assertThatCode(() -> new DefaultStepReporter(new StepListener[0]))
            .doesNotThrowAnyException();
    }

    public static void main(String[] args) {
        throw Unchecked.uncheckedException(null);

    }

    @Test
    void reportEmptyStepMethodThrowsExceptionForNullStepName() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportEmptyStep(null),
            "StepReporter reportEmptyStep method stepName arg is null",
            stepListeners
        );
    }

    @Test
    void reportEmptyStepMethod() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();

        stepReporter.reportEmptyStep(stepName);
        assertThatStepPassed(stepName, stepListeners);
    }

    @Test
    void reportFailedStepMethodThrowsExceptionForNullStepName() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportFailedStep(null, new RuntimeException()),
            "StepReporter reportFailedStep method stepName arg is null",
            stepListeners
        );
    }

    @Test
    void reportFailedStepMethodThrowsExceptionForException() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportFailedStep("step name", null),
            "StepReporter reportFailedStep method exception arg is null",
            stepListeners
        );
    }

    @Test
    void reportFailedStepMethod() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final RuntimeException exception = new RuntimeException();

        assertThatStepFailed(
            () -> stepReporter.reportFailedStep(stepName, exception),
            stepName,
            exception,
            stepListeners
        );
    }

//    @Test
//    void reportFailedStepMethodForWrappedException() {
//        final StepListener[] stepListeners = mockedStepListeners(3);
//        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
//        final String stepName = randomStepName();
//        final Throwable innerException = new Throwable();
//        final RuntimeException wrappedException = DefaultStepReporter.wrappedException(innerException);
//
//        assertThatStepFailed(
//            () -> stepReporter.reportFailedStep(stepName, wrappedException),
//            stepName,
//            innerException,
//            stepListeners
//        );
//    }

//    @Test
//    void reportFailedStepMethodForTripleWrappedException() {
//        final StepListener[] stepListeners = mockedStepListeners(3);
//        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
//        final String stepName = randomStepName();
//        final Throwable innerException = new Throwable();
//        final RuntimeException wrappedException = DefaultStepReporter.wrappedException(
//            DefaultStepReporter.wrappedException(
//                DefaultStepReporter.wrappedException(
//                    innerException
//                )
//            )
//        );
//
//        assertThatStepFailed(
//            () -> stepReporter.reportFailedStep(stepName, wrappedException),
//            stepName,
//            innerException,
//            stepListeners
//        );
//    }

    @Test
    void reportRunnableStepMethodThrowsExceptionForNullStepName() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportRunnableStep(null, () -> {}),
            "StepReporter reportRunnableStep method stepName arg is null",
            stepListeners
        );
    }

    @Test
    void reportRunnableStepMethodThrowsExceptionForNullRunnable() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportRunnableStep("step name", null),
            "StepReporter reportRunnableStep method runnable arg is null",
            stepListeners
        );
    }

    @Test
    void reportRunnableStepMethodPassedStep() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> runnable = mock(ThrowingRunnable.class);

        stepReporter.reportRunnableStep(stepName, runnable);
        assertThatStepPassed(stepName, stepListeners);
        verify(runnable, times(1)).run();
    }

    @Test
    void reportRunnableStepMethodFailedStep() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final RuntimeException exception = new RuntimeException();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> runnable = mock(ThrowingRunnable.class);
        doThrow(exception).when(runnable).run();

        assertThatStepFailed(
            () -> stepReporter.reportRunnableStep(stepName, runnable),
            stepName,
            exception,
            stepListeners
        );
        verify(runnable, times(1)).run();
    }

    @Test
    void reportConsumerStepMethodThrowsExceptionForNullStepName() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportConsumerStep(null, "input", x -> {}),
            "StepReporter reportConsumerStep method stepName arg is null",
            stepListeners
        );
    }

    @Test
    void reportConsumerStepMethodThrowsExceptionForNullConsumer() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportConsumerStep("step name", "input", null),
            "StepReporter reportConsumerStep method consumer arg is null",
            stepListeners
        );
    }

    @Test
    void reportConsumerStepMethodStepPassed() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final Object input = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, RuntimeException> consumer = mock(ThrowingConsumer.class);

        stepReporter.reportConsumerStep(stepName, input, consumer);
        assertThatStepPassed(stepName, stepListeners);
        verify(consumer, times(1)).accept(refEq(input));
    }

    @Test
    void reportConsumerStepMethodStepPassedWithNullInputArg() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<String, RuntimeException> consumer = mock(ThrowingConsumer.class);

        stepReporter.reportConsumerStep(stepName, null, consumer);
        assertThatStepPassed(stepName, stepListeners);
        verify(consumer, times(1)).accept(isNull());
    }

    @Test
    void reportConsumerStepMethodStepFailed() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final RuntimeException exception = new RuntimeException();
        final Object input = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, RuntimeException> consumer = mock(ThrowingConsumer.class);
        doThrow(exception).when(consumer).accept(any());

        assertThatStepFailed(
            () -> stepReporter.reportConsumerStep(stepName, input, consumer),
            stepName,
            exception,
            stepListeners
        );
        verify(consumer, times(1)).accept(refEq(input));
    }

    @Test
    void reportSupplierStepMethodThrowsExceptionForNullStepName() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportSupplierStep(null, () -> "result"),
            "StepReporter reportSupplierStep method stepName arg is null",
            stepListeners
        );
    }

    @Test
    void reportSupplierStepMethodThrowsExceptionForNullSupplier() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportSupplierStep("step name", null),
            "StepReporter reportSupplierStep method supplier arg is null",
            stepListeners
        );
    }

    @Test
    void reportSupplierStepMethodStepPassed() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final Object supplierResult = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> supplier = mock(ThrowingSupplier.class);
        when(supplier.get()).thenReturn(supplierResult);

        final Object methodResult = stepReporter.reportSupplierStep(stepName, supplier);
        assertThatStepPassed(stepName, stepListeners);
        assertThat(methodResult).isSameAs(supplierResult);
        verify(supplier, times(1)).get();
    }

    @Test
    void reportSupplierStepMethodStepFailed() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final RuntimeException exception = new RuntimeException();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<String, RuntimeException> supplier = mock(ThrowingSupplier.class);
        doThrow(exception).when(supplier).get();

        assertThatStepFailed(
            () -> stepReporter.reportSupplierStep(stepName, supplier),
            stepName,
            exception,
            stepListeners
        );
        verify(supplier, times(1)).get();
    }

    @Test
    void reportFunctionStepMethodThrowsExceptionForNullStepName() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportFunctionStep(null, "input", x -> new Object()),
            "StepReporter reportFunctionStep method stepName arg is null",
            stepListeners
        );
    }

    @Test
    void reportFunctionStepMethodThrowsExceptionForNullFunction() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);

        assertThatStepFailedWithXtepsException(
            () -> stepReporter.reportFunctionStep("step name", "input", null),
            "StepReporter reportFunctionStep method function arg is null",
            stepListeners
        );
    }

    @Test
    void reportFunctionStepMethodStepPassed() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final Object input = new Object();
        final Object functionResult = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(functionResult);

        final Object methodResult = stepReporter.reportFunctionStep(stepName, input, function);
        assertThatStepPassed(stepName, stepListeners);
        assertThat(methodResult).isSameAs(functionResult);
        verify(function, times(1)).apply(refEq(input));
    }

    @Test
    void reportFunctionStepMethodStepPassedWithNullInputArg() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final Object functionResult = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(functionResult);

        final Object methodResult = stepReporter.reportFunctionStep(stepName, null, function);
        assertThatStepPassed(stepName, stepListeners);
        assertThat(methodResult).isSameAs(functionResult);
        verify(function, times(1)).apply(isNull());
    }

    @Test
    void reportFunctionStepMethodReportInfoForException() {
        final StepListener[] stepListeners = mockedStepListeners(3);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(stepListeners);
        final String stepName = randomStepName();
        final RuntimeException exception = new RuntimeException();
        final Object input = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        doThrow(exception).when(function).apply(any());

        assertThatStepFailed(
            () -> stepReporter.reportFunctionStep(stepName, input, function),
            stepName,
            exception,
            stepListeners
        );
        verify(function, times(1)).apply(refEq(input));
    }

    @Test
    void reporterCatchListenerExceptions() {
        final StepListener stepListener1 = mock(StepListener.class);
        final StepListener stepListener2 = mock(StepListener.class);
        final DefaultStepReporter stepReporter = new DefaultStepReporter(
            new StepListener[]{stepListener1, stepListener2}
        );
        final String stepName = randomStepName();
        final RuntimeException exception1 = new RuntimeException("listener 1 step started exception");
        final RuntimeException exception2 = new RuntimeException("listener 2 step failed exception");
        doThrow(exception1).when(stepListener1).stepStarted(any(), any());
        doThrow(exception2).when(stepListener2).stepFailed(any(), any());

        final Throwable methodException = assertThrows(Throwable.class, () -> stepReporter.reportEmptyStep(stepName));
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage("One or more listeners threw exceptions")
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
        {
            final ArgumentCaptor<String> stepStartedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> stepFailedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<Throwable> stepFailedException = ArgumentCaptor.forClass(Throwable.class);
            verify(stepListener1, times(1)).stepStarted(stepStartedUUID.capture(), eq(stepName));
            verify(stepListener1, times(1)).stepFailed(stepFailedUUID.capture(), stepFailedException.capture());
            assertThat(stepFailedUUID.getValue()).isSameAs(stepStartedUUID.getValue());
            assertThat(stepFailedException.getValue()).isSameAs(methodException);
        }
        {
            final ArgumentCaptor<String> stepStartedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> stepFailedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<Throwable> stepFailedException = ArgumentCaptor.forClass(Throwable.class);
            verify(stepListener2, times(1)).stepStarted(stepStartedUUID.capture(), eq(stepName));
            verify(stepListener2, times(1)).stepFailed(stepFailedUUID.capture(), stepFailedException.capture());
            assertThat(stepFailedUUID.getValue()).isSameAs(stepStartedUUID.getValue());
            assertThat(stepFailedException.getValue()).isSameAs(methodException);
        }
    }

    private static void assertThatStepPassed(final String stepName,
                                             final StepListener... mockedStepListeners) {
        for (final StepListener listener : mockedStepListeners) {
            final ArgumentCaptor<String> stepStartedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> stepPassedUUID = ArgumentCaptor.forClass(String.class);
            verify(listener).stepStarted(stepStartedUUID.capture(), eq(stepName));
            verify(listener).stepPassed(stepPassedUUID.capture());
            verify(listener, never()).stepFailed(any(), any());
            assertThat(stepPassedUUID.getValue()).isSameAs(stepStartedUUID.getValue());
            assertThat(stepStartedUUID.getValue()).matches(uuidPattern());
        }
    }

    private static void assertThatStepFailed(final ThrowableAssert.ThrowingCallable stepAction,
                                             final String stepName,
                                             final Throwable exception,
                                             final StepListener... mockedStepListeners) {
        assertThatCode(stepAction)
            .isSameAs(exception)
            .isNot(containsXtepsStackTrace());
        for (final StepListener listener : mockedStepListeners) {
            final ArgumentCaptor<String> stepStartedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> stepFailedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<Throwable> stepFailedException = ArgumentCaptor.forClass(Throwable.class);
            verify(listener).stepStarted(stepStartedUUID.capture(), eq(stepName));
            verify(listener).stepFailed(stepFailedUUID.capture(), stepFailedException.capture());
            verify(listener, never()).stepPassed(any());
            assertThat(stepFailedUUID.getValue()).isSameAs(stepStartedUUID.getValue());
            assertThat(stepStartedUUID.getValue()).matches(uuidPattern());
            assertThat(stepFailedException.getValue()).isSameAs(exception);
        }
    }

    private static void assertThatStepFailedWithXtepsException(final ThrowableAssert.ThrowingCallable stepAction,
                                                               final String stepName,
                                                               final StepListener... mockedStepListeners) {
        final Throwable methodException = assertThrows(Throwable.class, stepAction::call);
        assertThat(methodException)
            .isInstanceOf(XtepsException.class)
            .hasMessage(stepName)
            .is(containsXtepsStackTrace());
        for (final StepListener listener : mockedStepListeners) {
            final ArgumentCaptor<String> stepStartedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> stepFailedUUID = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<Throwable> stepFailedException = ArgumentCaptor.forClass(Throwable.class);
            verify(listener).stepStarted(stepStartedUUID.capture(), eq(stepName));
            verify(listener).stepFailed(stepFailedUUID.capture(), stepFailedException.capture());
            verify(listener, never()).stepPassed(any());
            assertThat(stepFailedUUID.getValue()).isSameAs(stepStartedUUID.getValue());
            assertThat(stepStartedUUID.getValue()).matches(uuidPattern());
            assertThat(stepFailedException.getValue()).isSameAs(methodException);
        }
    }

    private static StepListener[] mockedStepListeners(final int count) {
        final StepListener[] listeners = new StepListener[count];
        for (int idx = 0; idx < count; ++idx) {
            listeners[idx] = mock(StepListener.class);
        }
        return listeners;
    }

    private static Condition<Throwable> containsXtepsStackTrace() {
        return new Condition<>(
            t -> Arrays.stream(t.getStackTrace()).anyMatch(el -> el.getClassName().startsWith("com.plugatar.xteps")),
            "contains Xteps stack trace"
        );
    }

    private static String uuidPattern() {
        return "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";
    }

    private static String randomStepName() {
        final Random random = new Random();
        byte[] byteArray = new byte[random.nextInt(9) + 1];
        random.nextBytes(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
