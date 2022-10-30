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
package com.plugatar.xteps.unchecked.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;

/**
 * Base steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code BaseStepsChain}
 */
public interface BaseStepsChain<S extends BaseStepsChain<S>> {

    /**
     * Performs and reports empty step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @return this steps chain
     * @throws XtepsException if {@code stepName} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String)
     */
    S step(String stepName);

    /**
     * Performs and reports empty step with given name and description and returns
     * this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String)
     */
    S step(
        String stepName,
        String stepDescription
    );

    /**
     * Performs and reports the step with given name and nested steps chain and returns
     * this steps chain.
     *
     * @param stepName   the step name
     * @param stepsChain the nested steps chain
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @see #nestedSteps(String, String, ThrowingConsumer)
     */
    S nestedSteps(
        String stepName,
        ThrowingConsumer<S, ?> stepsChain
    );

    /**
     * Performs and reports the step with given name and description and nested steps chain
     * and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param stepsChain      the nested steps chain
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @see #nestedSteps(String, ThrowingConsumer)
     */
    S nestedSteps(
        String stepName,
        String stepDescription,
        ThrowingConsumer<S, ?> stepsChain
    );

    /**
     * Performs and reports given step with given name and returns the steps chain result.
     *
     * @param stepName   the step name
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepName} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @see #nestedStepsTo(String, String, ThrowingFunction)
     */
    <R> R nestedStepsTo(
        String stepName,
        ThrowingFunction<S, ? extends R, ?> stepsChain
    );

    /**
     * Performs and reports given step with given name and description and returns
     * the steps chain result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param stepsChain      the nested steps chain
     * @param <R>             the result type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @see #nestedStepsTo(String, ThrowingFunction)
     */
    <R> R nestedStepsTo(
        String stepName,
        String stepDescription,
        ThrowingFunction<S, ? extends R, ?> stepsChain
    );

    /**
     * Performs given steps chain and returns this steps chain.
     *
     * @param stepsChain the branch steps chain
     * @return this steps chain
     * @throws XtepsException if {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    S branchSteps(
        ThrowingConsumer<S, ?> stepsChain
    );
}
