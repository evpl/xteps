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
package com.plugatar.xteps.core;

import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;

/**
 * Step reporter.
 */
public interface StepReporter {

    /**
     * Reports an empty step.
     *
     * @param stepName the step name
     * @throws XtepsException if {@code stepName} is null
     *                        or if it's impossible to correctly report the step
     */
    void reportEmptyStep(String stepName);

    /**
     * Reports an failed step.
     *
     * @param stepName  the step name
     * @param exception the exception
     * @param <TH>      the exception type
     * @throws XtepsException if {@code stepName} or {@code exception} is null
     *                        or if it's impossible to correctly report the step
     * @throws TH             in any other case
     */
    <TH extends Throwable> void reportFailedStep(
        final String stepName,
        final TH exception
    ) throws TH;

    /**
     * Reports a runnable step.
     *
     * @param stepName the step name
     * @param runnable the runnable
     * @param <TH>     the {@code runnable} exception type
     * @throws XtepsException if {@code stepName} or {@code runnable} is null
     *                        or if it's impossible to correctly report the step
     * @throws TH             if {@code runnable} threw exception
     */
    <TH extends Throwable> void reportRunnableStep(
        String stepName,
        ThrowingRunnable<? extends TH> runnable
    ) throws TH;

    /**
     * Reports a consumer step.
     *
     * @param stepName the step name
     * @param input    the {@code consumer} input
     * @param consumer the consumer
     * @param <T>      the type of the {@code consumer} input argument
     * @param <TH>     the {@code consumer} exception type
     * @throws XtepsException if {@code stepName} or {@code consumer} is null
     *                        or if it's impossible to correctly report the step
     * @throws TH             if {@code consumer} threw exception
     */
    <T, TH extends Throwable> void reportConsumerStep(
        String stepName,
        T input,
        ThrowingConsumer<? super T, ? extends TH> consumer
    ) throws TH;

    /**
     * Reports a supplier step and returns result.
     *
     * @param stepName the step name
     * @param supplier the supplier
     * @param <T>      the type of the {@code supplier} result
     * @param <TH>     the {@code supplier} exception type
     * @return the {@code supplier} result
     * @throws XtepsException if {@code stepName} or {@code supplier} is null
     *                        or if it's impossible to correctly report the step
     * @throws TH             if {@code supplier} threw exception
     */
    <T, TH extends Throwable> T reportSupplierStep(
        String stepName,
        ThrowingSupplier<? extends T, ? extends TH> supplier
    ) throws TH;

    /**
     * Reports a function step and returns result.
     *
     * @param stepName the step name
     * @param input    the {@code function} input
     * @param function the function
     * @param <T>      the type of the {@code function} input argument
     * @param <R>      the type of the {@code function} result
     * @param <TH>     the {@code function} exception type
     * @return the {@code function} result
     * @throws XtepsException if {@code stepName} or {@code function} is null
     *                        or if it's impossible to correctly report the step
     * @throws TH             if {@code function} threw exception
     */
    <T, R, TH extends Throwable> R reportFunctionStep(
        String stepName,
        T input,
        ThrowingFunction<? super T, ? extends R, ? extends TH> function
    ) throws TH;
}
