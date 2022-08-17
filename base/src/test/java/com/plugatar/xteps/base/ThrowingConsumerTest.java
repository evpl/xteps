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

/**
 * Tests for {@link ThrowingConsumer}.
 */
final class ThrowingConsumerTest {

    @Test
    void uncheckedMethodReturnsNullForNullArg() {
        assertThat(ThrowingConsumer.unchecked(null)).isNull();
    }

    @Test
    void uncheckedConsumerMethodReturnsNullForNullArg() {
        assertThat(ThrowingConsumer.uncheckedConsumer(null)).isNull();
    }

    @Test
    void uncheckedMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> originConsumer = mock(ThrowingConsumer.class);
        final Object consumerInput = new Object();
        doThrow(throwable).when(originConsumer).accept(same(consumerInput));

        final ThrowingConsumer<Object, RuntimeException> methodResult = ThrowingConsumer.unchecked(originConsumer);
        assertThatCode(() -> methodResult.accept(consumerInput))
            .isSameAs(throwable);
        verify(originConsumer, times(1)).accept(same(consumerInput));
    }

    @Test
    void uncheckedConsumerMethodExceptionLambdaResult() throws Throwable {
        final Throwable throwable = new Throwable();
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> originConsumer = mock(ThrowingConsumer.class);
        final Object consumerInput = new Object();
        doThrow(throwable).when(originConsumer).accept(same(consumerInput));

        final ThrowingConsumer<Object, RuntimeException> methodResult = ThrowingConsumer.uncheckedConsumer(originConsumer);
        assertThatCode(() -> methodResult.accept(consumerInput))
            .isSameAs(throwable);
        verify(originConsumer, times(1)).accept(same(consumerInput));
    }

    @Test
    void uncheckedMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> originConsumer = mock(ThrowingConsumer.class);
        final Object consumerInput = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult = ThrowingConsumer.unchecked(originConsumer);
        methodResult.accept(consumerInput);
        verify(originConsumer, times(1)).accept(same(consumerInput));
    }

    @Test
    void uncheckedConsumerMethodLambdaResult() throws Throwable {
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, Throwable> originConsumer = mock(ThrowingConsumer.class);
        final Object consumerInput = new Object();

        final ThrowingConsumer<Object, RuntimeException> methodResult = ThrowingConsumer.uncheckedConsumer(originConsumer);
        methodResult.accept(consumerInput);
        verify(originConsumer, times(1)).accept(same(consumerInput));
    }
}
