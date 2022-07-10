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
package com.plugatar.xteps;

import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Unchecked}.
 */
final class UncheckedTest {

    @Test
    void uncheckedRunnableMethodReturnsNullForNullArg() {
        assertThat(Unchecked.uncheckedRunnable(null)).isNull();
    }

    @Test
    void uncheckedMethodForRunnableReturnsNullForNullArg() {
        assertThat(Unchecked.unchecked((ThrowingRunnable<Throwable>) null)).isNull();
    }

    @Test
    void uncheckedRunnableMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> runnable = mock(ThrowingRunnable.class);
        doThrow(throwable).when(runnable).run();

        final ThrowingRunnable<RuntimeException> methodResult = Unchecked.uncheckedRunnable(runnable);
        assertThatCode(() -> methodResult.run())
            .isSameAs(throwable);
        verify(runnable, times(1)).run();
    }

    @Test
    void uncheckedMethodForRunnableExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> runnable = mock(ThrowingRunnable.class);
        doThrow(throwable).when(runnable).run();

        final ThrowingRunnable<RuntimeException> methodResult = Unchecked.unchecked(runnable);
        assertThatCode(() -> methodResult.run())
            .isSameAs(throwable);
        verify(runnable, times(1)).run();
    }

    @Test
    void uncheckedRunnableMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> runnable = mock(ThrowingRunnable.class);

        final ThrowingRunnable<RuntimeException> methodResult = Unchecked.uncheckedRunnable(runnable);
        methodResult.run();
        verify(runnable, times(1)).run();
    }

    @Test
    void uncheckedMethodForRunnableLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> runnable = mock(ThrowingRunnable.class);

        final ThrowingRunnable<RuntimeException> methodResult = Unchecked.unchecked(runnable);
        methodResult.run();
        verify(runnable, times(1)).run();
    }

    @Test
    void uncheckedSupplierMethodReturnsNullForNullArg() {
        assertThat(Unchecked.uncheckedSupplier(null)).isNull();
    }

    @Test
    void uncheckedMethodForSupplierReturnsNullForNullArg() {
        assertThat(Unchecked.unchecked((ThrowingSupplier<Object, Throwable>) null)).isNull();
    }

    @Test
    void uncheckedSupplierMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> supplier = mock(ThrowingSupplier.class);
        doThrow(throwable).when(supplier).get();

        final ThrowingSupplier<Object, RuntimeException> methodResult = Unchecked.uncheckedSupplier(supplier);
        assertThatCode(() -> methodResult.get())
            .isSameAs(throwable);
        verify(supplier, times(1)).get();
    }

    @Test
    void uncheckedMethodForSupplierExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> supplier = mock(ThrowingSupplier.class);
        doThrow(throwable).when(supplier).get();

        final ThrowingSupplier<Object, RuntimeException> methodResult = Unchecked.unchecked(supplier);
        assertThatCode(() -> methodResult.get())
            .isSameAs(throwable);
        verify(supplier, times(1)).get();
    }

    @Test
    void uncheckedSupplierMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> supplier = mock(ThrowingSupplier.class);
        final Object supplierResult = new Object();
        when(supplier.get()).thenReturn(supplierResult);

        final ThrowingSupplier<Object, RuntimeException> methodResult = Unchecked.unchecked(supplier);
        assertThat(methodResult.get())
            .isSameAs(supplierResult);
        verify(supplier, times(1)).get();
    }

    @Test
    void uncheckedMethodForSupplierLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> supplier = mock(ThrowingSupplier.class);
        final Object supplierResult = new Object();
        when(supplier.get()).thenReturn(supplierResult);

        final ThrowingSupplier<Object, RuntimeException> methodResult = Unchecked.unchecked(supplier);
        assertThat(methodResult.get())
            .isSameAs(supplierResult);
        verify(supplier, times(1)).get();
    }

    @Test
    void uncheckedConsumerMethodReturnsNullForNullArg() {
        assertThat(Unchecked.uncheckedConsumer(null)).isNull();
    }

    @Test
    void uncheckedMethodForConsumerReturnsNullForNullArg() {
        assertThat(Unchecked.unchecked((ThrowingConsumer<Object, Throwable>) null)).isNull();
    }

    @Test
    void uncheckedConsumerMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> consumer = mock(ThrowingConsumer.class);
        doThrow(throwable).when(consumer).accept(any());
        final Object input = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult = Unchecked.uncheckedConsumer(consumer);
        assertThatCode(() -> methodResult.accept(input))
            .isSameAs(throwable);
        verify(consumer).accept(same(input));
    }

    @Test
    void uncheckedMethodForConsumerExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> consumer = mock(ThrowingConsumer.class);
        doThrow(throwable).when(consumer).accept(any());
        final Object input = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult = Unchecked.unchecked(consumer);
        assertThatCode(() -> methodResult.accept(input))
            .isSameAs(throwable);
        verify(consumer).accept(same(input));
    }

    @Test
    void uncheckedConsumerMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> consumer = mock(ThrowingConsumer.class);
        final Object input = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult = Unchecked.uncheckedConsumer(consumer);
        methodResult.accept(input);
        verify(consumer).accept(same(input));
    }

    @Test
    void uncheckedMethodForConsumerLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> consumer = mock(ThrowingConsumer.class);
        final Object input = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult = Unchecked.unchecked(consumer);
        methodResult.accept(input);
        verify(consumer).accept(same(input));
    }

    @Test
    void uncheckedFunctionMethodReturnsNullForNullArg() {
        assertThat(Unchecked.uncheckedFunction(null)).isNull();
    }

    @Test
    void uncheckedMethodForFunctionReturnsNullForNullArg() {
        assertThat(Unchecked.unchecked((ThrowingFunction<Object, Object, Throwable>) null)).isNull();
    }

    @Test
    void uncheckedFunctionMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, Throwable> function = mock(ThrowingFunction.class);
        doThrow(throwable).when(function).apply(any());
        final Object input = new Object();

        final ThrowingFunction<Object, Object, RuntimeException> methodResult = Unchecked.uncheckedFunction(function);
        assertThatCode(() -> methodResult.apply(input))
            .isSameAs(throwable);
        verify(function).apply(same(input));
    }

    @Test
    void uncheckedMethodForFunctionExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, Throwable> function = mock(ThrowingFunction.class);
        doThrow(throwable).when(function).apply(any());
        final Object input = new Object();

        final ThrowingFunction<Object, Object, RuntimeException> methodResult = Unchecked.unchecked(function);
        assertThatCode(() -> methodResult.apply(input))
            .isSameAs(throwable);
        verify(function).apply(same(input));
    }

    @Test
    void uncheckedFunctionMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, Throwable> function = mock(ThrowingFunction.class);
        final Object functionResult = new Object();
        when(function.apply(any())).thenReturn(functionResult);
        final Object input = new Object();

        final ThrowingFunction<Object, Object, RuntimeException> methodResult = Unchecked.uncheckedFunction(function);
        assertThat(methodResult.apply(input))
            .isSameAs(functionResult);
        verify(function).apply(same(input));
    }

    @Test
    void uncheckedMethodForFunctionLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, Throwable> function = mock(ThrowingFunction.class);
        final Object functionResult = new Object();
        when(function.apply(any())).thenReturn(functionResult);
        final Object input = new Object();

        final ThrowingFunction<Object, Object, RuntimeException> methodResult = Unchecked.unchecked(function);
        assertThat(methodResult.apply(input))
            .isSameAs(functionResult);
        verify(function).apply(same(input));
    }
}
