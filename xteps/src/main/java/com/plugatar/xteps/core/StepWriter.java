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

import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepWriteException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;

/**
 * Step writer.
 */
public interface StepWriter {

    /**
     * Writes an empty step.
     *
     * @param stepName the step name
     * @throws ArgumentException  if {@code stepName} is null
     * @throws StepWriteException if it's impossible to correctly report the step
     */
    void writeEmptyStep(String stepName);

    /**
     * Writes a runnable step.
     *
     * @param stepName the step name
     * @param runnable the runnable
     * @param <TH>     the {@code runnable} exception type
     * @throws TH                 if {@code step} threw exception
     * @throws ArgumentException  if {@code stepName} or {@code runnable} is null
     * @throws StepWriteException if it's impossible to correctly report the step
     */
    <TH extends Throwable> void writeRunnableStep(
        String stepName,
        ThrowingRunnable<? extends TH> runnable
    ) throws TH;

    /**
     * Writes a consumer step.
     *
     * @param stepName the step name
     * @param input    the {@code consumer} input
     * @param consumer the consumer
     * @param <T>      the type of the {@code consumer} input argument
     * @param <TH>     the {@code consumer} exception type
     * @throws TH                 if {@code step} threw exception
     * @throws ArgumentException  if {@code stepName} or {@code consumer} is null
     * @throws StepWriteException if it's impossible to correctly report the step
     */
    <T, TH extends Throwable> void writeConsumerStep(
        String stepName,
        T input,
        ThrowingConsumer<? super T, ? extends TH> consumer
    ) throws TH;

    /**
     * Writes a supplier step and returns result.
     *
     * @param stepName the step name
     * @param supplier the supplier
     * @param <T>      the type of the {@code supplier} result
     * @param <TH>     the {@code supplier} exception type
     * @return the {@code supplier} result
     * @throws TH                 if {@code step} threw exception
     * @throws ArgumentException  if {@code stepName} or {@code supplier} is null
     * @throws StepWriteException if it's impossible to correctly report the step
     */
    <T, TH extends Throwable> T writeSupplierStep(
        String stepName,
        ThrowingSupplier<? extends T, ? extends TH> supplier
    ) throws TH;

    /**
     * Writes a function step and returns result.
     *
     * @param stepName the step name
     * @param input    the {@code function} input
     * @param function the function
     * @param <T>      the type of the {@code function} input argument
     * @param <R>      the type of the {@code function} result
     * @param <TH>     the {@code function} exception type
     * @return the {@code function} result
     * @throws TH                 if {@code step} threw exception
     * @throws ArgumentException  if {@code stepName} or {@code function} is null
     * @throws StepWriteException if it's impossible to correctly report the step
     */
    <T, R, TH extends Throwable> R writeFunctionStep(
        String stepName,
        T input,
        ThrowingFunction<? super T, ? extends R, ? extends TH> function
    ) throws TH;
}
