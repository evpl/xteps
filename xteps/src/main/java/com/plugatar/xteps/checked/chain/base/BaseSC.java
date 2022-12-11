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

import com.plugatar.xteps.base.HookPriority;
import com.plugatar.xteps.base.HooksOrder;
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
    S callChainHooks();

    /**
     * Sets given hooks order for this steps chain.
     *
     * @param order the hooks order
     * @return this steps chain
     * @throws XtepsException if {@code order} is null
     */
    S chainHooksOrder(HooksOrder order);

    /**
     * Sets given hooks order for the current thread.
     *
     * @param order the hooks order
     * @return this steps chain
     * @throws XtepsException if {@code order} is null
     */
    S threadHooksOrder(HooksOrder order);

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
     * Adds given hook to this steps chain. This hook will be called in case of any
     * exception in steps chain or in case of {@link #callChainHooks()} method call.
     *
     * @param priority the priority
     * @param hook     the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     *                        or if {@code priority} is not in the range {@link HookPriority#MIN_HOOK_PRIORITY} to
     *                        {@link HookPriority#MAX_HOOK_PRIORITY}
     */
    S chainHook(
        int priority,
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
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param priority the priority
     * @param hook     the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     *                        or if {@code priority} is not in the range {@link HookPriority#MIN_HOOK_PRIORITY} to
     *                        {@link HookPriority#MAX_HOOK_PRIORITY}
     */
    S threadHook(
        int priority,
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
     * @param <E>      the {@code supplier} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code supplier} is null
     * @throws E              if {@code supplier} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> withCtx(
        ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E;

    /**
     * Performs given action and returns this steps chain.
     *
     * @param action the action
     * @param <E>    the {@code action} exception type
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     * @throws E              if {@code action} threw exception
     */
    <E extends Throwable> S action(
        ThrowingRunnable<? extends E> action
    ) throws E;

    /**
     * Performs given action and returns the action result.
     *
     * @param action the action
     * @param <R>    the {@code action} result type
     * @param <E>    the {@code action} exception type
     * @return the {@code action} result
     * @throws XtepsException if {@code action} is null
     * @throws E              if {@code action} threw exception
     */
    <R, E extends Throwable> R actionTo(
        ThrowingSupplier<? extends R, ? extends E> action
    ) throws E;

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
        SupplierStep<?, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given keyword in the step name and returns this steps chain.
     *
     * @param keyword the keyword
     * @param step    the step
     * @param <E>     the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> S step(
        String keyword,
        RunnableStep<? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given keyword in the step name and returns this steps chain.
     *
     * @param keyword the keyword
     * @param step    the step
     * @param <E>     the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> S step(
        String keyword,
        SupplierStep<?, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step action with empty name and returns this steps chain.
     *
     * @param action the step action
     * @param <E>    the {@code action} exception type
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step action
     * @throws E              if {@code action} threw exception
     */
    <E extends Throwable> S step(
        ThrowingRunnable<? extends E> action
    ) throws E;

    /**
     * Performs and reports given step action with given name and returns this steps chain.
     *
     * @param name   the step name
     * @param action the step action
     * @param <E>    the {@code action} exception type
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the action
     * @throws E              if {@code action} threw exception
     */
    <E extends Throwable> S step(
        String name,
        ThrowingRunnable<? extends E> action
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns this steps chain.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @param <E>    the {@code action} exception type
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <E extends Throwable> S step(
        String name,
        String desc,
        ThrowingRunnable<? extends E> action
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
    <U, E extends Throwable> BaseCtxSC<?> stepToCtx(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * a contextual steps chain of the new context.
     *
     * @param keyword the keyword
     * @param step    the step
     * @param <U>     the context type
     * @param <E>     the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> stepToCtx(
        String keyword,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with empty name and returns a contextual
     * steps chain of the new context.
     *
     * @param action the step action
     * @param <U>    the context type
     * @param <E>    the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> stepToCtx(
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * Performs and reports given step with given name and returns a contextual
     * steps chain of the new context.
     *
     * @param name   the step name
     * @param action the step action
     * @param <U>    the context type
     * @param <E>    the {@code action} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> stepToCtx(
        String name,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns
     * a contextual steps chain of the new context.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @param <U>    the context type
     * @param <E>    the {@code action} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> stepToCtx(
        String name,
        String desc,
        ThrowingSupplier<? extends U, ? extends E> action
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
     * Performs and reports given step with given keyword in the step name and returns
     * the step result.
     *
     * @param keyword the keyword
     * @param step    the step
     * @param <R>     the result type
     * @param <E>     the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R stepTo(
        String keyword,
        SupplierStep<? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with empty name and returns the step result.
     *
     * @param action the step action
     * @param <R>    the result type
     * @param <E>    the {@code action} exception type
     * @return {@code action} result
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <R, E extends Throwable> R stepTo(
        ThrowingSupplier<? extends R, ? extends E> action
    ) throws E;

    /**
     * Performs and reports given step with given name and returns the step result.
     *
     * @param name   the step name
     * @param action the step action
     * @param <R>    the result type
     * @param <E>    the {@code action} exception type
     * @return {@code action} result
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <R, E extends Throwable> R stepTo(
        String name,
        ThrowingSupplier<? extends R, ? extends E> action
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns the step result.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @param <R>    the result type
     * @param <E>    the {@code action} exception type
     * @return {@code action} result
     * @throws XtepsException if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <R, E extends Throwable> R stepTo(
        String name,
        String desc,
        ThrowingSupplier<? extends R, ? extends E> action
    ) throws E;

    /**
     * Performs and reports the step with empty name and nested steps chain and returns
     * this steps chain.
     *
     * @param stepsChain the nested steps chain
     * @param <E>        the {@code stepsChain} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <E extends Throwable> S nestedSteps(
        ThrowingConsumer<S, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports the step with given name and nested steps chain and returns
     * this steps chain.
     *
     * @param name       the step name
     * @param stepsChain the nested steps chain
     * @param <E>        the {@code stepsChain} exception type
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <E extends Throwable> S nestedSteps(
        String name,
        ThrowingConsumer<S, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports the step with given name and description and nested steps chain
     * and returns this steps chain.
     *
     * @param name       the step name
     * @param desc       the step description
     * @param stepsChain the nested steps chain
     * @param <E>        the {@code stepsChain} exception type
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code desc} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <E extends Throwable> S nestedSteps(
        String name,
        String desc,
        ThrowingConsumer<S, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports given step with empty name and returns the steps chain result.
     *
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @param <E>        the {@code stepsChain} exception type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <R, E extends Throwable> R nestedStepsTo(
        ThrowingFunction<S, ? extends R, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports given step with given name and returns the steps chain result.
     *
     * @param name       the step name
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @param <E>        the {@code stepsChain} exception type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code name} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <R, E extends Throwable> R nestedStepsTo(
        String name,
        ThrowingFunction<S, ? extends R, ? extends E> stepsChain
    ) throws E;

    /**
     * Performs and reports given step with given name and description and returns
     * the steps chain result.
     *
     * @param name       the step name
     * @param desc       the step description
     * @param stepsChain the nested steps chain
     * @param <R>        the result type
     * @param <E>        the {@code stepsChain} exception type
     * @return {@code stepsChain} result
     * @throws XtepsException if {@code name} or {@code desc} or {@code stepsChain} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code stepsChain} threw exception
     */
    <R, E extends Throwable> R nestedStepsTo(
        String name,
        String desc,
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
