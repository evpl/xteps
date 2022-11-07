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
 * The {@link java.util.function.Consumer} specialization for 3 input arguments that might throw an exception.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 * @param <E> the type of the throwing exception
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ThrowingTriConsumer<T, U, V, E extends Throwable> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @throws E if consumer threw exception
     */
    void accept(T t, U u, V v) throws E;

    /**
     * Returns given {@code ThrowingTriConsumer} as unchecked or null if {@code consumer} is null.
     *
     * @param consumer the consumer
     * @param <T>      the type of the {@code consumer} first input argument
     * @param <U>      the type of the {@code consumer} second input argument
     * @param <V>      the type of the {@code consumer} third input argument
     * @return unchecked consumer or null
     */
    @SuppressWarnings("unchecked")
    static <T, U, V> ThrowingTriConsumer<T, U, V, RuntimeException> unchecked(
        final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> consumer
    ) {
        return (ThrowingTriConsumer<T, U, V, RuntimeException>) consumer;
    }
}
