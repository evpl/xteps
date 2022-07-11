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
     * @param stepName        the step name
     * @param stepDescription the step description
     * @throws XtepsException if {@code stepName} or {@code stepDescription} is null
     *                        or if it's impossible to correctly report the step
     */
    void reportEmptyStep(
        String stepName,
        String stepDescription
    );

    /**
     * Reports a runnable step.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param runnable        the runnable
     * @param <E>             the {@code runnable} exception type
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code runnable} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code runnable} threw exception
     */
    <E extends Throwable> void reportRunnableStep(
        String stepName,
        String stepDescription,
        ThrowingRunnable<? extends E> runnable
    ) throws E;

    /**
     * Reports a consumer step.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param input           the {@code consumer} input
     * @param consumer        the consumer
     * @param <T>             the type of the {@code consumer} input argument
     * @param <E>             the {@code consumer} exception type
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code consumer} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code consumer} threw exception
     */
    <T, E extends Throwable> void reportConsumerStep(
        String stepName,
        String stepDescription,
        T input,
        ThrowingConsumer<? super T, ? extends E> consumer
    ) throws E;

    /**
     * Reports a supplier step and returns result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param supplier        the supplier
     * @param <T>             the type of the {@code supplier} result
     * @param <E>             the {@code supplier} exception type
     * @return the {@code supplier} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code supplier} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code supplier} threw exception
     */
    <T, E extends Throwable> T reportSupplierStep(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends T, ? extends E> supplier
    ) throws E;

    /**
     * Reports a function step and returns result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param input           the {@code function} input
     * @param function        the function
     * @param <T>             the type of the {@code function} input argument
     * @param <R>             the type of the {@code function} result
     * @param <E>             the {@code function} exception type
     * @return the {@code function} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code function} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code function} threw exception
     */
    <T, R, E extends Throwable> R reportFunctionStep(
        String stepName,
        String stepDescription,
        T input,
        ThrowingFunction<? super T, ? extends R, ? extends E> function
    ) throws E;
}
