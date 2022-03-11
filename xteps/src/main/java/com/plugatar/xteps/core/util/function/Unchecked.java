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

/**
 * Utility class. Allows to make functions unchecked.
 */
public final class Unchecked {

    /**
     * Utility class ctor.
     */
    private Unchecked() {
    }

    /**
     * Returns given runnable as unchecked runnable.
     *
     * @param runnable the runnable
     * @return unchecked runnable
     */
    @SuppressWarnings("unchecked")
    public static ThrowingRunnable<RuntimeException> uncheckedRunnable(
        final ThrowingRunnable<?> runnable
    ) {
        return (ThrowingRunnable<RuntimeException>) runnable;
    }

    /**
     * Returns given consumer as unchecked consumer.
     *
     * @param consumer the consumer
     * @return unchecked consumer
     */
    @SuppressWarnings("unchecked")
    public static <T> ThrowingConsumer<T, RuntimeException> uncheckedConsumer(
        final ThrowingConsumer<? super T, ?> consumer
    ) {
        return (ThrowingConsumer<T, RuntimeException>) consumer;
    }

    /**
     * Returns given supplier as unchecked supplier.
     *
     * @param supplier the supplier
     * @return unchecked supplier
     */
    @SuppressWarnings("unchecked")
    public static <T> ThrowingSupplier<T, RuntimeException> uncheckedSupplier(
        final ThrowingSupplier<? extends T, ?> supplier
    ) {
        return (ThrowingSupplier<T, RuntimeException>) supplier;
    }

    /**
     * Returns given function as unchecked function.
     *
     * @param function the function
     * @return unchecked function
     */
    @SuppressWarnings("unchecked")
    public static <T, R> ThrowingFunction<T, R, RuntimeException> uncheckedFunction(
        final ThrowingFunction<? super T, ? extends R, ?> function
    ) {
        return (ThrowingFunction<T, R, RuntimeException>) function;
    }
}
