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
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;

/**
 * Memorizing no contexts steps.
 *
 * @param <P> the previous steps type
 */
public interface MemorizingNoCtxSteps<P extends BaseCtxSteps<?>>
    extends BaseMemorizingSteps<P>, StepNameReplacementsSupplier {

    /**
     * Returns a memorizing context steps of the context. Non-reporting method.
     *
     * @param context the context
     * @param <U>     the context type
     * @return memorizing context steps
     */
    <U> MemorizingCtxSteps<U, P> toContext(U context);

    /**
     * Returns a memorizing context steps of the context. Non-reporting method.
     *
     * @param contextSupplier the context supplier
     * @param <U>             the context type
     * @param <TH>            the {@code contextSupplier} exception type
     * @return memorizing context steps
     * @throws TH                if {@code contextSupplier} threw exception
     * @throws ArgumentException if {@code contextSupplier} is null
     */
    <U, TH extends Throwable> MemorizingCtxSteps<U, P> toContext(
        ThrowingSupplier<? extends U, ? extends TH> contextSupplier
    ) throws TH;

    /**
     * Performs empty step with given name and returns a memorizing no context steps.
     *
     * @param stepName the step name
     * @return memorizing no context steps
     * @throws ArgumentException       if {@code stepName} is null or empty
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    MemorizingNoCtxSteps<P> emptyStep(String stepName);

    /**
     * Performs given step with given name and returns a
     * memorizing no context steps.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <TH>     the {@code step} exception type
     * @return memorizing no context steps
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    <TH extends Throwable> MemorizingNoCtxSteps<P> step(
        String stepName,
        ThrowingRunnable<? extends TH> step
    ) throws TH;

    /**
     * Performs given step with given name and returns a
     * memorizing context steps.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @param <TH>     the {@code step} exception type
     * @return memorizing context steps
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     */
    <U, TH extends Throwable> MemorizingCtxSteps<U, P> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ? extends TH> step
    ) throws TH;

    /**
     * Performs given step with given name and returns
     * the step result.
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
        ThrowingSupplier<? extends R, ? extends TH> step
    ) throws TH;

    /**
     * Performs the step with given name and nested steps and
     * returns a memorizing no context steps.
     *
     * @param stepName the step name
     * @param steps    the nested steps
     * @param <TH>     the {@code steps} exception type
     * @return memorizing no context steps
     * @throws TH                      if {@code steps} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code steps} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the steps
     */
    <TH extends Throwable> MemorizingNoCtxSteps<P> nestedSteps(
        String stepName,
        ThrowingConsumer<MemorizingNoCtxSteps<P>, ? extends TH> steps
    ) throws TH;

    /**
     * Performs step with given name and nested steps and
     * returns the nested steps result.
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
        ThrowingFunction<MemorizingNoCtxSteps<P>, ? extends R, ? extends TH> steps
    ) throws TH;

    /**
     * Performs separated steps and returns a memorizing no context steps.
     *
     * @param steps the separated step
     * @param <TH>  the {@code steps} exception
     * @return no context steps
     * @throws TH                 if {@code steps} threw exception
     * @throws ArgumentException  if {@code steps} is null
     * @throws StepWriteException if it's impossible to correctly report the steps
     */
    <TH extends Throwable> MemorizingNoCtxSteps<P> separatedSteps(
        ThrowingConsumer<MemorizingNoCtxSteps<P>, ? extends TH> steps
    ) throws TH;
}
