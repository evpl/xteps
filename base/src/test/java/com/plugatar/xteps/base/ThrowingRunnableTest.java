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

/**
 * Tests for {@link ThrowingRunnable}.
 */
final class ThrowingRunnableTest {

    @Test
    void uncheckedMethodReturnsNullForNullArg() {
        assertThat(ThrowingRunnable.unchecked(null)).isNull();
    }

    @Test
    void uncheckedRunnableMethodReturnsNullForNullArg() {
        assertThat(ThrowingRunnable.uncheckedRunnable(null)).isNull();
    }

    @Test
    void uncheckedMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> originRunnable = mock(ThrowingRunnable.class);
        doThrow(throwable).when(originRunnable).run();

        final ThrowingRunnable<RuntimeException> methodResult = ThrowingRunnable.unchecked(originRunnable);
        assertThatCode(() -> methodResult.run())
            .isSameAs(throwable);
        verify(originRunnable, times(1)).run();
    }

    @Test
    void uncheckedRunnableMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> originRunnable = mock(ThrowingRunnable.class);
        doThrow(throwable).when(originRunnable).run();

        final ThrowingRunnable<RuntimeException> methodResult = ThrowingRunnable.uncheckedRunnable(originRunnable);
        assertThatCode(() -> methodResult.run())
            .isSameAs(throwable);
        verify(originRunnable, times(1)).run();
    }

    @Test
    void uncheckedMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> originRunnable = mock(ThrowingRunnable.class);

        final ThrowingRunnable<RuntimeException> methodResult = ThrowingRunnable.unchecked(originRunnable);
        methodResult.run();
        verify(originRunnable, times(1)).run();
    }

    @Test
    void uncheckedRunnableMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingRunnable<Throwable> originRunnable = mock(ThrowingRunnable.class);

        final ThrowingRunnable<RuntimeException> methodResult = ThrowingRunnable.uncheckedRunnable(originRunnable);
        methodResult.run();
        verify(originRunnable, times(1)).run();
    }
}
