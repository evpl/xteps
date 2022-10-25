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
 * The {@link java.util.function.Function} specialization for 3 input arguments that might throw an exception.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingTriFunction<T, U, V, R, E extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return the result
     * @throws E if function threw exception
     */
    R apply(T t, U u, V v) throws E;

    /**
     * Returns given {@code ThrowingTriFunction} as unchecked or null if {@code function} is null.
     *
     * @param function the function
     * @param <T>      the type of the {@code function} first input argument
     * @param <U>      the type of the {@code function} second input argument
     * @param <V>      the type of the {@code function} third input argument
     * @param <R>      the type of the {@code function} result
     * @return unchecked function or null
     * @see #uncheckedBiFunction(ThrowingTriFunction)
     */
    static <T, U, V, R> ThrowingTriFunction<T, U, V, R, RuntimeException> unchecked(
        final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> function
    ) {
        return uncheckedBiFunction(function);
    }

    /**
     * Returns given {@code ThrowingTriFunction} as unchecked or null if {@code function} is null.
     *
     * @param function the function
     * @param <T>      the type of the {@code function} first input argument
     * @param <U>      the type of the {@code function} second input argument
     * @param <V>      the type of the {@code function} third input argument
     * @param <R>      the type of the {@code function} result
     * @return unchecked function or null
     * @see #unchecked(ThrowingTriFunction)
     */
    @SuppressWarnings("unchecked")
    static <T, U, V, R> ThrowingTriFunction<T, U, V, R, RuntimeException> uncheckedBiFunction(
        final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> function
    ) {
        return (ThrowingTriFunction<T, U, V, R, RuntimeException>) function;
    }
}
