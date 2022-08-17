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
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ThrowingFunction}.
 */
final class ThrowingPredicateTest {

    @Test
    void uncheckedMethodReturnsNullForNullArg() {
        assertThat(ThrowingPredicate.unchecked(null)).isNull();
    }

    @Test
    void uncheckedSupplierMethodReturnsNullForNullArg() {
        assertThat(ThrowingPredicate.uncheckedPredicate(null)).isNull();
    }

    @Test
    void uncheckedMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingPredicate<Object, Throwable> originFunction = mock(ThrowingPredicate.class);
        final Object predicateInput = new Object();
        doThrow(throwable).when(originFunction).test(same(predicateInput));

        final ThrowingPredicate<Object, RuntimeException> methodResult = ThrowingPredicate.unchecked(originFunction);
        assertThatCode(() -> methodResult.test(predicateInput))
            .isSameAs(throwable);
        verify(originFunction, times(1)).test(same(predicateInput));
    }

    @Test
    void uncheckedSupplierMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingPredicate<Object, Throwable> originFunction = mock(ThrowingPredicate.class);
        final Object predicateInput = new Object();
        doThrow(throwable).when(originFunction).test(same(predicateInput));

        final ThrowingPredicate<Object, RuntimeException> methodResult = ThrowingPredicate.uncheckedPredicate(originFunction);
        assertThatCode(() -> methodResult.test(predicateInput))
            .isSameAs(throwable);
        verify(originFunction, times(1)).test(same(predicateInput));
    }

    @Test
    void uncheckedMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingPredicate<Object, Throwable> originFunction = mock(ThrowingPredicate.class);
        final Object predicateInput = new Object();
        final boolean predicateResult = true;
        when(originFunction.test(predicateInput)).thenReturn(predicateResult);

        final ThrowingPredicate<Object, RuntimeException> methodResult = ThrowingPredicate.unchecked(originFunction);
        assertThat(methodResult.test(predicateInput)).isEqualTo(predicateResult);
        verify(originFunction, times(1)).test(same(predicateInput));
    }

    @Test
    void uncheckedSupplierMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingPredicate<Object, Throwable> originFunction = mock(ThrowingPredicate.class);
        final Object predicateInput = new Object();
        final boolean predicateResult = true;
        when(originFunction.test(predicateInput)).thenReturn(predicateResult);

        final ThrowingPredicate<Object, RuntimeException> methodResult = ThrowingPredicate.uncheckedPredicate(originFunction);
        assertThat(methodResult.test(predicateInput)).isEqualTo(predicateResult);
        verify(originFunction, times(1)).test(same(predicateInput));
    }
}
