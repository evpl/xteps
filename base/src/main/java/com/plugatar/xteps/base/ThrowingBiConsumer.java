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

/**
 * The {@link java.util.function.BiConsumer} specialization that might throw an exception.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <E> the type of the throwing exception
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Throwable> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @throws E if consumer threw exception
     */
    void accept(T t, U u) throws E;

    /**
     * Returns given {@code ThrowingBiConsumer} as unchecked or null if {@code consumer} is null.
     *
     * @param consumer the consumer
     * @param <T>      the type of the {@code consumer} first input argument
     * @param <U>      the type of the {@code consumer} second input argument
     * @return unchecked consumer or null
     */
    @SuppressWarnings("unchecked")
    static <T, U> ThrowingBiConsumer<T, U, RuntimeException> unchecked(
        final ThrowingBiConsumer<? super T, ? super U, ?> consumer
    ) {
        return (ThrowingBiConsumer<T, U, RuntimeException>) consumer;
    }
}
