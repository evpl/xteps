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
import com.plugatar.xteps.core.exception.StepNameFormatException;
import com.plugatar.xteps.core.exception.StepWriteException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;

/**
 * Context steps.
 *
 * @param <T> the context type
 */
public interface CtxSteps<T> extends BaseCtxSteps<T>, StepNameReplacementsSupplier {

    /**
     * Supply context to given consumer and returns a context steps. Non-reporting method.
     *
     * @param consumer the consumer
     * @param <TH>     the {@code consumer} exception type
     * @return context steps
     * @throws TH                if {@code consumer} threw exception
     * @throws ArgumentException if {@code consumer} is null
     */
    <TH extends Throwable> CtxSteps<T> supplyContextTo(
        ThrowingConsumer<? super T, ? extends TH> consumer
    ) throws TH;

    /**
     * Apply context to given function and returns result. Non-reporting method.
     *
     * @param function the function
     * @param <TH>     the {@code function} exception type
     * @return {@code function} result
     * @throws TH                if {@code function} threw exception
     * @throws ArgumentException if {@code function} is null
     */
    <R, TH extends Throwable> R applyContextTo(
        ThrowingFunction<? super T, ? extends R, ? extends TH> function
    ) throws TH;

    /**
     * Returns a memorizing no context steps. Non-reporting method.
     *
     * @return memorizing no context steps
     */
    MemorizingNoCtxSteps<CtxSteps<T>> noContextSteps();

    /**
     * Returns a memorizing context steps of the new context. Non-reporting method.
     *
     * @param context the new context
     * @param <U>     the new context type
     * @return memorizing context steps
     */
    <U> MemorizingCtxSteps<U, CtxSteps<T>> toContext(U context);

    /**
     * Returns a memorizing context steps of the new context. Non-reporting method.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @param <TH>            the {@code contextFunction} exception type
     * @return memorizing context steps
     * @throws TH                if {@code contextFunction} threw exception
     * @throws ArgumentException if {@code contextFunction} is null
     */
    <U, TH extends Throwable> MemorizingCtxSteps<U, CtxSteps<T>> toContext(
        ThrowingFunction<? super T, ? extends U, ? extends TH> contextFunction
    ) throws TH;

    /**
     * Performs empty step with given name and returns a context steps.
     *
     * @param stepName the step name
     * @return context steps
     * @throws ArgumentException       if {@code stepName} is null or empty
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    CtxSteps<T> emptyStep(String stepName);

    /**
     * Performs given step with given name and returns a context steps.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <TH>     the {@code step} exception type
     * @return context steps
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    <TH extends Throwable> CtxSteps<T> step(
        String stepName,
        ThrowingConsumer<? super T, ? extends TH> step
    ) throws TH;

    /**
     * Performs given step with given name and returns a context steps of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the new context type
     * @param <TH>     the {@code step} exception type
     * @return context steps
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    <U, TH extends Throwable> MemorizingCtxSteps<U, CtxSteps<T>> stepToContext(
        String stepName,
        ThrowingFunction<? super T, ? extends U, ? extends TH> step
    ) throws TH;

    /**
     * Performs given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @param <TH>     the {@code step} exception type
     * @return {@code step} result
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    <R, TH extends Throwable> R stepTo(
        String stepName,
        ThrowingFunction<? super T, ? extends R, ? extends TH> step
    ) throws TH;

    /**
     * Performs the step with given name and nested steps and returns a context steps.
     *
     * @param stepName the step name
     * @param steps    the nested steps
     * @param <TH>     the {@code steps} exception type
     * @return context steps
     * @throws TH                      if {@code steps} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code steps} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the steps
     */
    <TH extends Throwable> CtxSteps<T> nestedSteps(
        String stepName,
        ThrowingConsumer<CtxSteps<T>, ? extends TH> steps
    ) throws TH;

    /**
     * Performs step with given name and nested steps and returns the nested steps result.
     *
     * @param stepName the step name
     * @param steps    the nested steps
     * @param <R>      the result type
     * @param <TH>     the {@code steps} exception type
     * @return {@code steps} result
     * @throws TH                      if {@code steps} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code steps} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the steps
     */
    <R, TH extends Throwable> R nestedStepsTo(
        String stepName,
        ThrowingFunction<CtxSteps<T>, ? extends R, ? extends TH> steps
    ) throws TH;

    /**
     * Performs separated steps and returns a context steps.
     *
     * @param steps the separated step
     * @param <TH>  the {@code steps} exception
     * @return context steps
     * @throws TH                 if {@code steps} threw exception
     * @throws ArgumentException  if {@code steps} is null
     * @throws StepWriteException if it's impossible to correctly report the steps
     */
    <TH extends Throwable> CtxSteps<T> separatedSteps(
        ThrowingConsumer<CtxSteps<T>, ? extends TH> steps
    ) throws TH;
}
