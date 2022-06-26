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

/**
 * Base steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code BaseStepsChain}
 */
public interface BaseStepsChain<S extends BaseStepsChain<S>> {

    /**
     * Returns a contextual steps chain of the new context.
     *
     * @param context the new context
     * @param <U>     the new context type
     * @return contextual steps chain
     */
    <U> CtxStepsChain<U, S> withContext(U context);

    /**
     * Performs empty step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @return this steps chain
     * @throws XtepsException if {@code stepName} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(String stepName);

    /**
     * Performs the step with given name and nested steps chain and returns this steps chain.
     *
     * @param stepName   the step name
     * @param stepsChain the nested steps chain
     * @param <E>        the {@code stepsChain} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <E extends Throwable> S nestedSteps(
        String stepName,
        ThrowingConsumer<S, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs given step with given name and returns the steps chain result.
     *
     * @param stepName   the step name
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @param <E>        the {@code step} exception type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepName} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <R, E extends Throwable> R nestedStepsTo(
        String stepName,
        ThrowingFunction<S, ? extends R, ? extends E> stepsChain
    ) throws E;
}
