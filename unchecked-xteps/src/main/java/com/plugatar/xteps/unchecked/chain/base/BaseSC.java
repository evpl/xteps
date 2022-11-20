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
package com.plugatar.xteps.unchecked.chain.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.stepobject.RunnableStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;

/**
 * Base steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code BaseSC}
 */
public interface BaseSC<S extends BaseSC<S>> {

    /**
     * Calls all hooks in this steps chain. Exceptions will be added to the base
     * exception as suppressed exceptions.
     *
     * @return this steps chain
     * @throws XtepsException if one or more hooks threw exceptions
     */
    S callHooks();

    /**
     * Adds given hook to this steps chain. This hook will be calls in case of any
     * exception in steps chain or in case of {@link #callHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    S hook(ThrowingRunnable<?> hook);

    /**
     * Returns a contextual steps chain of the new context.
     *
     * @param context the new context
     * @param <U>     the new context type
     * @return contextual steps chain
     */
    <U> BaseCtxSC<?> withContext(U context);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextSupplier the context supplier
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextSupplier} is null
     */
    <U> BaseCtxSC<?> withContext(
        ThrowingSupplier<? extends U, ?> contextSupplier
    );

    /**
     * Performs and reports empty step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @return this steps chain
     * @throws XtepsException if {@code stepName} is null
     *                        or if it's impossible to correctly report the step
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
     */
    S step(
        String stepName,
        String stepDescription
    );

    /**
     * Performs and reports given step and returns this steps chain.
     *
     * @param step the step
     * @return this steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        RunnableStep step
    );

    /**
     * Performs and reports given step with given keyword in the step name and returns this steps chain.
     *
     * @param keyword the keyword
     * @param step    the step
     * @return this steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String keyword,
        RunnableStep step
    );

    /**
     * Performs and reports given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String stepName,
        ThrowingRunnable<?> step
    );

    /**
     * Performs and reports given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String stepName,
        String stepDescription,
        ThrowingRunnable<?> step
    );

    /**
     * Performs and reports given step and returns a contextual steps chain of the new context.
     *
     * @param step the step
     * @param <U>  the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToContext(
        SupplierStep<? extends U> step
    );

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * a contextual steps chain of the new context.
     *
     * @param keyword the keyword
     * @param step    the step
     * @param <U>     the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToContext(
        String keyword,
        SupplierStep<? extends U> step
    );

    /**
     * Performs and reports given step with given name and returns a contextual
     * steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ?> step
    );

    /**
     * Performs and reports given step with given name and description and returns
     * a contextual steps chain of the new context.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ?> step
    );

    /**
     * Performs and reports given step and returns the step result.
     *
     * @param step the step
     * @param <R>  the result type
     * @return {@code step} result
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        SupplierStep<? extends R> step
    );

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * the step result.
     *
     * @param keyword the keyword
     * @param step    the step
     * @param <R>     the result type
     * @return {@code step} result
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String keyword,
        SupplierStep<? extends R> step
    );

    /**
     * Performs and reports given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String stepName,
        ThrowingSupplier<? extends R, ?> step
    );

    /**
     * Performs and reports given step with given name and description and returns the step result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends R, ?> step
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
