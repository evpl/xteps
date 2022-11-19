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
package com.plugatar.xteps.checked.chain.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

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
    <U> BaseCtxSC<U, ?> withContext(U context);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextSupplier the context supplier
     * @param <U>             the context type
     * @param <E>             the {@code contextSupplier} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextSupplier} is null
     * @throws E              if {@code contextSupplier} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<U, ?> withContext(
        ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E;

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
     * @param <E>  the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> S step(
        RunnableStep<? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given prefix in the step name and returns this steps chain.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <E>            the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> S step(
        String stepNamePrefix,
        RunnableStep<? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <E>      the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> S step(
        String stepName,
        ThrowingRunnable<? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <E>             the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> S step(
        String stepName,
        String stepDescription,
        ThrowingRunnable<? extends E> step
    ) throws E;

    /**
     * Performs and reports given step and returns a contextual steps chain of the new context.
     *
     * @param step the step
     * @param <U>  the context type
     * @param <E>  the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<U, ?> stepToContext(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * a contextual steps chain of the new context.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <U>            the context type
     * @param <E>            the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<U, ?> stepToContext(
        String stepNamePrefix,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and returns a contextual
     * steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @param <E>      the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<U, ?> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns
     * a contextual steps chain of the new context.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <U>             the context type
     * @param <E>             the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<U, ?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step and returns the step result.
     *
     * @param step the step
     * @param <R>  the result type
     * @param <E>  the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R stepTo(
        SupplierStep<? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * the step result.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <R>            the result type
     * @param <E>            the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R stepTo(
        String stepNamePrefix,
        SupplierStep<? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @param <E>      the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        ThrowingSupplier<? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns the step result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @param <E>             the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs and reports the step with given name and nested steps chain and returns
     * this steps chain.
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
     * Performs and reports the step with given name and description and nested steps chain
     * and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param stepsChain      the nested steps chain
     * @param <E>             the {@code stepsChain} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <E extends Throwable> S nestedSteps(
        String stepName,
        String stepDescription,
        ThrowingConsumer<S, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports given step with given name and returns the steps chain result.
     *
     * @param stepName   the step name
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @param <E>        the {@code stepsChain} exception type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepName} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <R, E extends Throwable> R nestedStepsTo(
        String stepName,
        ThrowingFunction<S, ? extends R, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns
     * the steps chain result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param stepsChain      the nested steps chain
     * @param <R>             the result type
     * @param <E>             the {@code stepsChain} exception type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <R, E extends Throwable> R nestedStepsTo(
        String stepName,
        String stepDescription,
        ThrowingFunction<S, ? extends R, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs given steps chain and returns this steps chain.
     *
     * @param stepsChain the branch steps chain
     * @param <E>        the {@code stepsChain} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <E extends Throwable> S branchSteps(
        ThrowingConsumer<S, ? extends E> stepsChain
    ) throws E;
}
