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
    S callChainHooks();

    /**
     * Adds given hook to this steps chain. This hook will be called in case of any
     * exception in steps chain or in case of {@link #callChainHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    S chainHook(
        ThrowingRunnable<?> hook
    );

    /**
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    S threadHook(
        ThrowingRunnable<?> hook
    );

    /**
     * Returns a contextual steps chain of the new context.
     *
     * @param context the new context
     * @param <U>     the new context type
     * @return contextual steps chain
     */
    <U> BaseCtxSC<?> withCtx(U context);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param supplier the context supplier
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code supplier} is null
     */
    <U> BaseCtxSC<?> withCtx(
        ThrowingSupplier<? extends U, ?> supplier
    );

    /**
     * Performs given action and returns this steps chain.
     *
     * @param action the action
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     */
    S action(
        ThrowingRunnable<?> action
    );

    /**
     * Performs given action and returns the action result.
     *
     * @param action the action
     * @param <R>    the {@code action} result type
     * @return the {@code action} result
     * @throws XtepsException if {@code action} is null
     */
    <R> R actionTo(
        ThrowingSupplier<? extends R, ?> action
    );

    /**
     * Performs and reports empty step with given name and returns this steps chain.
     *
     * @param name the step name
     * @return this steps chain
     * @throws XtepsException if {@code name} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(String name);

    /**
     * Performs and reports empty step with given name and description and returns
     * this steps chain.
     *
     * @param name the step name
     * @param desc the step description
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code desc} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(String name,
           String desc);

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
     * Performs and reports given step and returns this steps chain.
     *
     * @param step the step
     * @return this steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        SupplierStep<?> step
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
        SupplierStep<?> step
    );

    /**
     * Performs and reports given step action with empty name and returns this steps chain.
     *
     * @param action the step action
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step action
     */
    S step(
        ThrowingRunnable<?> action
    );

    /**
     * Performs and reports given step action with given name and returns this steps chain.
     *
     * @param name   the step name
     * @param action the step action
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the action
     */
    S step(
        String name,
        ThrowingRunnable<?> action
    );

    /**
     * Performs and reports given step with given name and description and returns this steps chain.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String name,
        String desc,
        ThrowingRunnable<?> action
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
    <U> BaseCtxSC<?> stepToCtx(
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
    <U> BaseCtxSC<?> stepToCtx(
        String keyword,
        SupplierStep<? extends U> step
    );

    /**
     * Performs and reports given step with empty name and returns a contextual
     * steps chain of the new context.
     *
     * @param action the step action
     * @param <U>    the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToCtx(
        ThrowingSupplier<? extends U, ?> action
    );

    /**
     * Performs and reports given step with given name and returns a contextual
     * steps chain of the new context.
     *
     * @param name   the step name
     * @param action the step action
     * @param <U>    the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToCtx(
        String name,
        ThrowingSupplier<? extends U, ?> action
    );

    /**
     * Performs and reports given step with given name and description and returns
     * a contextual steps chain of the new context.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @param <U>    the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToCtx(
        String name,
        String desc,
        ThrowingSupplier<? extends U, ?> action
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
     * Performs and reports given step with empty name and returns the step result.
     *
     * @param action the step action
     * @param <R>    the result type
     * @return {@code action} result
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        ThrowingSupplier<? extends R, ?> action
    );

    /**
     * Performs and reports given step with given name and returns the step result.
     *
     * @param name   the step name
     * @param action the step action
     * @param <R>    the result type
     * @return {@code action} result
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String name,
        ThrowingSupplier<? extends R, ?> action
    );

    /**
     * Performs and reports given step with given name and description and returns the step result.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @param <R>    the result type
     * @return {@code action} result
     * @throws XtepsException if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String name,
        String desc,
        ThrowingSupplier<? extends R, ?> action
    );

    /**
     * Performs and reports the step with empty name and nested steps chain and returns
     * this steps chain.
     *
     * @param stepsChain the nested steps chain
     * @return this steps chain
     * @throws XtepsException if {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    S nestedSteps(
        ThrowingConsumer<S, ?> stepsChain
    );

    /**
     * Performs and reports the step with given name and nested steps chain and returns
     * this steps chain.
     *
     * @param name       the step name
     * @param stepsChain the nested steps chain
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    S nestedSteps(
        String name,
        ThrowingConsumer<S, ?> stepsChain
    );

    /**
     * Performs and reports the step with given name and description and nested steps chain
     * and returns this steps chain.
     *
     * @param name       the step name
     * @param desc       the step description
     * @param stepsChain the nested steps chain
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code desc} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    S nestedSteps(
        String name,
        String desc,
        ThrowingConsumer<S, ?> stepsChain
    );

    /**
     * Performs and reports given step with empty name and returns the steps chain result.
     *
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R nestedStepsTo(
        ThrowingFunction<S, ? extends R, ?> stepsChain
    );

    /**
     * Performs and reports given step with given name and returns the steps chain result.
     *
     * @param name       the step name
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code name} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R nestedStepsTo(
        String name,
        ThrowingFunction<S, ? extends R, ?> stepsChain
    );

    /**
     * Performs and reports given step with given name and description and returns
     * the steps chain result.
     *
     * @param name       the step name
     * @param desc       the step description
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code name} or {@code desc} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R nestedStepsTo(
        String name,
        String desc,
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
