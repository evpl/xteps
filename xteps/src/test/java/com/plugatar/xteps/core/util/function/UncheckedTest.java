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
package com.plugatar.xteps.core.util.function;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
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
    void classIsFinal() {
        assertThat(Unchecked.class).isFinal();
    }

    @Test
    void singlePrivateCtor() {
        assertThat(Unchecked.class.getDeclaredConstructors())
            .singleElement()
            .is(new Condition<>(
                ctor -> Modifier.isPrivate(ctor.getModifiers()),
                "private"
            ));
    }

    @Test
    void uncheckedRunnableMethodDoesntThrowExceptionForNullArg() {
        assertThat(Unchecked.uncheckedRunnable(null))
            .isNull();
    }

    @Test
    void uncheckedRunnableMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> runnable = mock(ThrowingRunnable.class);
        doThrow(throwable).when(runnable).run();

        final ThrowingRunnable<RuntimeException> methodResult =
            Unchecked.uncheckedRunnable(runnable);
        assertThatCode(() -> methodResult.run())
            .isSameAs(throwable);
        verify(runnable, times(1)).run();
    }

    @Test
    void uncheckedRunnableMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> runnable = mock(ThrowingRunnable.class);

        final ThrowingRunnable<RuntimeException> methodResult =
            Unchecked.uncheckedRunnable(runnable);
        methodResult.run();
        verify(runnable, times(1)).run();
    }

    @Test
    void uncheckedSupplierMethodDoesntThrowExceptionForNullArg() {
        assertThat(Unchecked.uncheckedSupplier(null))
            .isNull();
    }

    @Test
    void uncheckedSupplierMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> supplier = mock(ThrowingSupplier.class);
        doThrow(throwable).when(supplier).get();

        final ThrowingSupplier<Object, RuntimeException> methodResult =
            Unchecked.uncheckedSupplier(supplier);
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

        final ThrowingSupplier<Object, RuntimeException> methodResult =
            Unchecked.uncheckedSupplier(supplier);
        assertThat(methodResult.get())
            .isSameAs(supplierResult);
        verify(supplier, times(1)).get();
    }

    @Test
    void uncheckedConsumerMethodDoesntThrowExceptionForNullArg() {
        assertThat(Unchecked.uncheckedConsumer(null))
            .isNull();
    }

    @Test
    void uncheckedConsumerMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> consumer = mock(ThrowingConsumer.class);
        doThrow(throwable).when(consumer).accept(any());
        final Object input = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult =
            Unchecked.uncheckedConsumer(consumer);
        assertThatCode(() -> methodResult.accept(input))
            .isSameAs(throwable);
        verify(consumer).accept(refEq(input));
    }

    @Test
    void uncheckedConsumerMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> consumer = mock(ThrowingConsumer.class);
        final Object input = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult =
            Unchecked.uncheckedConsumer(consumer);
        methodResult.accept(input);
        verify(consumer).accept(refEq(input));
    }

    @Test
    void uncheckedFunctionMethodDoesntThrowExceptionForNullArg() {
        assertThat(Unchecked.uncheckedFunction(null))
            .isNull();
    }

    @Test
    void uncheckedFunctionMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, Throwable> function = mock(ThrowingFunction.class);
        doThrow(throwable).when(function).apply(any());
        final Object input = new Object();

        final ThrowingFunction<Object, Object, RuntimeException> methodResult =
            Unchecked.uncheckedFunction(function);
        assertThatCode(() -> methodResult.apply(input))
            .isSameAs(throwable);
        verify(function).apply(refEq(input));
    }

    @Test
    void uncheckedFunctionMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, Throwable> function = mock(ThrowingFunction.class);
        final Object functionResult = new Object();
        when(function.apply(any())).thenReturn(functionResult);
        final Object input = new Object();

        final ThrowingFunction<Object, Object, RuntimeException> methodResult =
            Unchecked.uncheckedFunction(function);
        assertThat(methodResult.apply(input))
            .isSameAs(functionResult);
        verify(function).apply(refEq(input));
    }
}