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
 * The {@link java.util.function.Predicate} specialization that might throw an exception.
 *
 * @param <T> the type of the input argument
 * @param <E> the type of the throwing exception
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface ThrowingPredicate<T, E extends Throwable> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     * @throws E if predicate threw exception
     */
    boolean test(T t) throws E;

    /**
     * Returns given {@code ThrowingPredicate} as unchecked or null if {@code predicate} is null.
     *
     * @param predicate the predicate
     * @param <T>       the type of the {@code predicate} input argument
     * @return unchecked predicate or null
     * @see #uncheckedPredicate(ThrowingPredicate)
     */
    static <T> ThrowingPredicate<T, RuntimeException> unchecked(
        final ThrowingPredicate<? super T, ?> predicate
    ) {
        return uncheckedPredicate(predicate);
    }

    /**
     * Returns given {@code ThrowingPredicate} as unchecked or null if {@code predicate} is null.
     *
     * @param predicate the predicate
     * @param <T>       the type of the {@code predicate} input argument
     * @return unchecked predicate or null
     * @see #unchecked(ThrowingPredicate)
     */
    @SuppressWarnings("unchecked")
    static <T> ThrowingPredicate<T, RuntimeException> uncheckedPredicate(
        final ThrowingPredicate<? super T, ?> predicate
    ) {
        return (ThrowingPredicate<T, RuntimeException>) predicate;
    }
}
