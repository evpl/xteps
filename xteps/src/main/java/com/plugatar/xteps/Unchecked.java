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
package com.plugatar.xteps;

import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;

/**
 * Utility class. Part of Xteps API. Allows to make functions unchecked.
 */
public final class Unchecked {

    /**
     * Utility class ctor.
     */
    private Unchecked() {
    }

    /**
     * Returns given {@link ThrowingRunnable} as unchecked or null if {@code runnable} is null.<br>
     * Code example:
     * <pre>{@code
     * stepsChain()
     *     .step("Step 1", uncheckedRunnable(() -> {
     *         ...
     *     }));
     * }</pre>
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
     * Returns given {@link ThrowingConsumer} as as unchecked or null if {@code consumer} is null.<br>
     * Code example:
     * <pre>{@code
     * stepsChainOf("context")
     *     .step("Step 1", uncheckedConsumer(ctx -> {
     *         ...
     *     }));
     * }</pre>
     *
     * @param consumer the consumer
     * @param <T>      the type of the {@code consumer} input argument
     * @return unchecked consumer
     */
    @SuppressWarnings("unchecked")
    public static <T> ThrowingConsumer<T, RuntimeException> uncheckedConsumer(
        final ThrowingConsumer<? super T, ?> consumer
    ) {
        return (ThrowingConsumer<T, RuntimeException>) consumer;
    }

    /**
     * Returns given {@link ThrowingSupplier} as unchecked or null if {@code supplier} is null.<br>
     * Code example:
     * <pre>{@code
     * stepsChain()
     *     .stepToContext("Step 1", uncheckedSupplier(() -> {
     *         ...
     *         return "context";
     *     }));
     * }</pre>
     *
     * @param supplier the supplier
     * @param <T>      the type of the {@code supplier} result
     * @return unchecked supplier
     */
    @SuppressWarnings("unchecked")
    public static <T> ThrowingSupplier<T, RuntimeException> uncheckedSupplier(
        final ThrowingSupplier<? extends T, ?> supplier
    ) {
        return (ThrowingSupplier<T, RuntimeException>) supplier;
    }

    /**
     * Returns given {@link ThrowingFunction} as unchecked or null if {@code function} is null.<br>
     * Code example:
     * <pre>{@code
     * stepsChainOf("context1")
     *     .stepToContext("Step 1", uncheckedFunction(ctx -> {
     *         ...
     *         return "context2";
     *     }));
     * }</pre>
     *
     * @param function the function
     * @param <T>      the type of the {@code function} input argument
     * @param <R>      the type of the {@code function} result
     * @return unchecked function
     */
    @SuppressWarnings("unchecked")
    public static <T, R> ThrowingFunction<T, R, RuntimeException> uncheckedFunction(
        final ThrowingFunction<? super T, ? extends R, ?> function
    ) {
        return (ThrowingFunction<T, R, RuntimeException>) function;
    }
}
