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
package com.plugatar.xteps.core.writer;

import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepWriteException;
import com.plugatar.xteps.core.exception.XtepsException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultStepWriter}.
 */
final class DefaultStepWriterTest {

    @Test
    void classIsNotFinal() {
        assertThat(DefaultStepWriter.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = DefaultStepWriter.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullStepListener() {
        assertThatCode(() -> new DefaultStepWriter(null, false))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void writeEmptyStepMethodThrowsExceptionForNullStepName() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeEmptyStep(null))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeEmptyStepMethodReportInfo() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();

        sw.writeEmptyStep(stepName);
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener).stepPassed(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void writeRunnableStepMethodThrowsExceptionForNullStepName() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeRunnableStep(null, () -> {}))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeRunnableStepMethodThrowsExceptionForNullRunnable() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeRunnableStep("step name", null))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeRunnableStepMethodReportInfoIfNoException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> runnable = mock(ThrowingRunnable.class);

        sw.writeRunnableStep(stepName, runnable);
        verify(runnable, times(1)).run();
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener).stepPassed(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void writeRunnableStepMethodReportInfoForException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        final RuntimeException exception = new RuntimeException();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<RuntimeException> runnable = mock(ThrowingRunnable.class);
        doThrow(exception).when(runnable).run();

        assertThatCode(() -> sw.writeRunnableStep(stepName, runnable))
            .isSameAs(exception);
        verify(runnable, times(1)).run();
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepPassed(any(), any());
        verify(stepListener).stepFailed(argThat(notEmptyString()), eq(stepName), refEq(exception));
    }

    @Test
    void writeConsumerStepMethodThrowsExceptionForNullStepName() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeConsumerStep(null, "input", x -> {}))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeConsumerStepMethodNotThrowsExceptionForNullInput() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeConsumerStep("step name", null, x -> {}))
            .doesNotThrowAnyException();
    }

    @Test
    void writeConsumerStepMethodThrowsExceptionForNullConsumer() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeConsumerStep("step name", "input", null))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeConsumerStepMethodReportInfoIfNoException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<String, RuntimeException> consumer = mock(ThrowingConsumer.class);

        sw.writeConsumerStep(stepName, "input", consumer);
        verify(consumer, times(1)).accept(any());
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener).stepPassed(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void writeConsumerStepMethodReportInfoForException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        final RuntimeException exception = new RuntimeException();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<String, RuntimeException> consumer = mock(ThrowingConsumer.class);
        doThrow(exception).when(consumer).accept(any());

        assertThatCode(() -> sw.writeConsumerStep(stepName, "input", consumer))
            .isSameAs(exception);
        verify(consumer, times(1)).accept(any());
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepPassed(any(), any());
        verify(stepListener).stepFailed(argThat(notEmptyString()), eq(stepName), refEq(exception));
    }

    @Test
    void writeSupplierStepMethodThrowsExceptionForNullStepName() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeSupplierStep(null, () -> "result"))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeSupplierStepMethodThrowsExceptionForNullSupplier() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeSupplierStep("step name", null))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeSupplierStepMethodReportInfoIfNoException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        final String supplierResult = UUID.randomUUID().toString();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<String, RuntimeException> supplier = mock(ThrowingSupplier.class);
        when(supplier.get()).thenReturn(supplierResult);

        final String result = sw.writeSupplierStep(stepName, supplier);
        assertThat(result).isEqualTo(supplierResult);
        verify(supplier, times(1)).get();
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener).stepPassed(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void writeSupplierStepMethodReportInfoForException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        final RuntimeException exception = new RuntimeException();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<String, RuntimeException> supplier = mock(ThrowingSupplier.class);
        doThrow(exception).when(supplier).get();

        assertThatCode(() -> sw.writeSupplierStep(stepName, supplier))
            .isSameAs(exception);
        verify(supplier, times(1)).get();
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepPassed(any(), any());
        verify(stepListener).stepFailed(argThat(notEmptyString()), eq(stepName), refEq(exception));
    }


    @Test
    void writeFunctionStepMethodThrowsExceptionForNullStepName() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeFunctionStep(null, "input", x -> "result"))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeFunctionStepMethodNotThrowsExceptionForNullInput() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeConsumerStep("step name", null, x -> {}))
            .doesNotThrowAnyException();
    }

    @Test
    void writeFunctionStepMethodThrowsExceptionForNullFunction() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);

        assertThatCode(() -> sw.writeFunctionStep("step name", "input", null))
            .isInstanceOf(ArgumentException.class);
        verifyNoInteractions(stepListener);
    }

    @Test
    void writeFunctionStepMethodReportInfoIfNoException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        final String functionResult = UUID.randomUUID().toString();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<String, String, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(functionResult);

        final String result = sw.writeFunctionStep(stepName, "input", function);
        assertThat(result).isEqualTo(functionResult);
        verify(function, times(1)).apply(any());
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener).stepPassed(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void writeFunctionStepMethodReportInfoForException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final String stepName = UUID.randomUUID().toString();
        final RuntimeException exception = new RuntimeException();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<String, String, RuntimeException> function = mock(ThrowingFunction.class);
        doThrow(exception).when(function).apply(any());

        assertThatCode(() -> sw.writeFunctionStep(stepName, "input", function))
            .isSameAs(exception);
        verify(function, times(1)).apply(any());
        verify(stepListener).stepStarted(argThat(notEmptyString()), eq(stepName));
        verify(stepListener, never()).stepPassed(any(), any());
        verify(stepListener).stepFailed(argThat(notEmptyString()), eq(stepName), refEq(exception));
    }

    @Test
    void writerCatchListenerStartMethodException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final RuntimeException listenerException = new RuntimeException();
        doThrow(listenerException).when(stepListener).stepStarted(any(), any());

        assertThatCode(() -> sw.writeRunnableStep("step name", () -> {}))
            .isInstanceOf(StepWriteException.class)
            .hasCause(listenerException);
    }

    @Test
    void writerCatchListenerFinishMethodException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final RuntimeException listenerException = new RuntimeException();
        doThrow(listenerException).when(stepListener).stepPassed(any(), any());

        assertThatCode(() -> sw.writeRunnableStep("step name", () -> {}))
            .isInstanceOf(StepWriteException.class)
            .hasCause(listenerException);
    }

    @Test
    void writerCatchListenerFailMethodException() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final RuntimeException listenerException = new RuntimeException();
        doThrow(listenerException).when(stepListener).stepFailed(any(), any(), any());
        final RuntimeException runnableException = new RuntimeException();

        assertThatCode(() -> sw.writeRunnableStep("step name", () -> {throw runnableException;}))
            .isInstanceOf(StepWriteException.class)
            .hasCause(listenerException)
            .hasSuppressedException(runnableException);
    }

    @Test
    void cleanStackTraceArgumentTrue() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, true);
        final RuntimeException ex1 = new XtepsException("ex1");
        final RuntimeException ex2 = new RuntimeException("ex2");
        final RuntimeException ex3 = new XtepsException("ex3");
        final RuntimeException ex4 = new RuntimeException("ex4");
        ex1.initCause(ex2);
        ex2.addSuppressed(ex3);
        ex3.initCause(ex4);
        final ThrowingFunction<String, String, RuntimeException> function = str -> { throw ex1; };

        Throwable actualEx = null;
        try {
            sw.writeFunctionStep("step name", "input", function);
        } catch (final Throwable th) {
            actualEx = th;
        }
        assertThat(actualEx).isSameAs(ex1);
        final Predicate<StackTraceElement> elementWithXtepsInfoPredicate =
            stackTraceElement -> stackTraceElement.getClassName().startsWith("com.plugatar.xteps");
        assertThat(ex1.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isNotEmpty();
        assertThat(ex2.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isEmpty();
        assertThat(ex3.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isNotEmpty();
        assertThat(ex4.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isEmpty();
    }

    @Test
    void cleanStackTraceArgumentFalse() {
        final StepListener stepListener = mock(StepListener.class);
        final DefaultStepWriter sw = new DefaultStepWriter(stepListener, false);
        final RuntimeException ex1 = new XtepsException("ex1");
        final RuntimeException ex2 = new RuntimeException("ex2");
        final RuntimeException ex3 = new XtepsException("ex3");
        final RuntimeException ex4 = new RuntimeException("ex4");
        ex1.initCause(ex2);
        ex2.addSuppressed(ex3);
        ex3.initCause(ex4);
        final ThrowingFunction<String, String, RuntimeException> function = str -> { throw ex1; };

        Throwable actualEx = null;
        try {
            sw.writeFunctionStep("step name", "input", function);
        } catch (final Throwable th) {
            actualEx = th;
        }
        assertThat(actualEx).isSameAs(ex1);
        final Predicate<StackTraceElement> elementWithXtepsInfoPredicate =
            stackTraceElement -> stackTraceElement.getClassName().startsWith("com.plugatar.xteps");
        assertThat(ex1.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isNotEmpty();
        assertThat(ex2.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isNotEmpty();
        assertThat(ex3.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isNotEmpty();
        assertThat(ex4.getStackTrace()).filteredOn(elementWithXtepsInfoPredicate).isNotEmpty();
    }

    private static ArgumentMatcher<String> notEmptyString() {
        return argument -> !(argument == null || argument.isEmpty());
    }
}
