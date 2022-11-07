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
package com.plugatar.xteps.base;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ThrowingSupplier}.
 */
final class ThrowingSupplierTest {

    @Test
    void uncheckedMethodReturnsNullForNullArg() {
        assertThat(ThrowingSupplier.unchecked(null)).isNull();
    }

    @Test
    void uncheckedMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> originSupplier = mock(ThrowingSupplier.class);
        doThrow(throwable).when(originSupplier).get();

        final ThrowingSupplier<Object, RuntimeException> methodResult = ThrowingSupplier.unchecked(originSupplier);
        assertThatCode(() -> methodResult.get())
            .isSameAs(throwable);
        verify(originSupplier, times(1)).get();
    }

    @Test
    void uncheckedMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, Throwable> originSupplier = mock(ThrowingSupplier.class);
        final Object supplierResult = new Object();
        when(originSupplier.get()).thenReturn(supplierResult);

        final ThrowingSupplier<Object, RuntimeException> methodResult = ThrowingSupplier.unchecked(originSupplier);
        assertThat(methodResult.get()).isSameAs(supplierResult);
        verify(originSupplier, times(1)).get();
    }
}
