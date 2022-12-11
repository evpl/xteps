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

import com.plugatar.xteps.base.HookPriority;
import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.stepobject.BiConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.BiFunctionStep;

/**
 * Base double context steps chain.
 *
 * @param <C>  the context type
 * @param <C2> the second context type
 * @param <S>  the type of the steps chain implementing {@code Base2CtxSC}
 */
public interface Base2CtxSC<C, C2, S extends Base2CtxSC<C, C2, S>> {

    /**
     * Adds given hook to this steps chain. This hook will be called in case of any
     * exception in steps chain or in case of {@link BaseSC#callChainHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    S chainHook(
        ThrowingBiConsumer<? super C, ? super C2, ?> hook
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
        ThrowingBiConsumer<? super C, ? super C2, ?> hook
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
        ThrowingBiConsumer<? super C, ? super C2, ?> hook
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
        ThrowingBiConsumer<? super C, ? super C2, ?> hook
    );

    /**
     * Returns the second context.
     *
     * @return the second context
     */
    C2 ctx2();

    /**
     * Returns a context steps chain of the new context.
     *
     * @param function the context function
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code function} is null
     */
    <U> BaseCtxSC<?> withCtx(
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> function
    );

    /**
     * Performs given action and returns this steps chain.
     *
     * @param action the action
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     */
    S action(
        ThrowingBiConsumer<? super C, ? super C2, ?> action
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
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
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
        BiConsumerStep<? super C, ? super C2> step
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
        BiFunctionStep<? super C, ? super C2, ?> step
    );

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * this steps chain.
     *
     * @param keyword the keyword
     * @param step    the step
     * @return this steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String keyword,
        BiConsumerStep<? super C, ? super C2> step
    );

    /**
     * Performs and reports given step with given keyword in the step name and returns
     * this steps chain.
     *
     * @param keyword the keyword
     * @param step    the step
     * @return this steps chain
     * @throws XtepsException if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String keyword,
        BiFunctionStep<? super C, ? super C2, ?> step
    );

    /**
     * Performs and reports given step with empty name and returns this steps chain.
     *
     * @param action the step action
     * @return this steps chain
     * @throws XtepsException if {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        ThrowingBiConsumer<? super C, ? super C2, ?> action
    );

    /**
     * Performs and reports given step with given name and returns this steps chain.
     *
     * @param name   the step name
     * @param action the step action
     * @return this steps chain
     * @throws XtepsException if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    S step(
        String name,
        ThrowingBiConsumer<? super C, ? super C2, ?> action
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
        ThrowingBiConsumer<? super C, ? super C2, ?> action
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
        BiFunctionStep<? super C, ? super C2, ? extends U> step
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
        BiFunctionStep<? super C, ? super C2, ? extends U> step
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
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> action
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
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> action
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
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> action
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
        BiFunctionStep<? super C, ? super C2, ? extends R> step
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
        BiFunctionStep<? super C, ? super C2, ? extends R> step
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
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
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
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
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
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
    );
}
