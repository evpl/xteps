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
import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.stepobject.TriConsumerStep;
import com.plugatar.xteps.checked.stepobject.TriFunctionStep;

/**
 * Base triple context steps chain.
 *
 * @param <C>  the context type
 * @param <C2> the second context type
 * @param <C3> the third context type
 * @param <S>  the type of the steps chain implementing {@code Base3CtxSC}
 */
public interface Base3CtxSC<C, C2, C3, S extends Base3CtxSC<C, C2, C3, S>> {

    /**
     * Adds given hook to this steps chain. This hook will be called in case of any
     * exception in steps chain or in case of {@link BaseSC#callChainHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    S chainHook(
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    );

    /**
     * Adds given hook to this steps chain. This hook will be called in case of any
     * exception in steps chain or in case of {@link BaseSC#callChainHooks()} method call.
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
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
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
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
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
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    );

    /**
     * Returns the third context.
     *
     * @return the third context
     */
    C3 ctx3();

    /**
     * Returns a context steps chain of the new context.
     *
     * @param function the context function
     * @param <U>      the context type
     * @param <E>      the {@code function} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code function} is null
     * @throws E              if {@code function} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> withCtx(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> function
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
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
    ) throws E;

    /**
     * Performs given action and returns the action result.
     *
     * @param action the action
     * @param <E>    the {@code action} exception type
     * @param <R>    the {@code action} result type
     * @return the {@code action} result
     * @throws XtepsException if {@code action} is null
     * @throws E              if {@code action} threw exception
     */
    <R, E extends Throwable> R actionTo(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
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
        TriConsumerStep<? super C, ? super C2, ? super C3, ? extends E> step
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ?, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * this steps chain.
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
        TriConsumerStep<? super C, ? super C2, ? super C3, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * this steps chain.
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ?, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with empty name and returns this steps chain.
     *
     * @param action the step action
     * @param <E>    the {@code action} exception type
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <E extends Throwable> S step(
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
    ) throws E;

    /**
     * Performs and reports given step with given name and returns this steps chain.
     *
     * @param name   the step name
     * @param action the step action
     * @param <E>    the {@code action} exception type
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <E extends Throwable> S step(
        String name,
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
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
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with empty name and returns a contextual
     * steps chain of the new context.
     *
     * @param action the step action
     * @param <U>    the context type
     * @param <E>    the {@code action} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code action} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> stepToCtx(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
    ) throws E;
}
