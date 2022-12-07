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
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.stepobject.ConsumerStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;

/**
 * Base single context steps chain.
 *
 * @param <C> the context type
 * @param <S> the type of the steps chain implementing {@code Base1CtxSC}
 */
public interface Base1CtxSC<C, S extends Base1CtxSC<C, S>> {

    /**
     * Adds given hook to this steps chain. This hook will be called in case of any
     * exception in steps chain or in case of {@link BaseSC#callChainHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    S chainHook(
        ThrowingConsumer<? super C, ?> hook
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
        ThrowingConsumer<? super C, ?> hook
    );

    /**
     * Returns the context.
     *
     * @return the context
     */
    C ctx();

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
        ThrowingFunction<? super C, ? extends U, ? extends E> function
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
        ThrowingConsumer<? super C, ? extends E> action
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
        ThrowingFunction<? super C, ? extends R, ? extends E> action
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
        ConsumerStep<? super C, ? extends E> step
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
        FunctionStep<? super C, ?, ? extends E> step
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
        ConsumerStep<? super C, ? extends E> step
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
        FunctionStep<? super C, ?, ? extends E> step
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
        ThrowingConsumer<? super C, ? extends E> action
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
        ThrowingConsumer<? super C, ? extends E> action
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
        ThrowingConsumer<? super C, ? extends E> action
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
        FunctionStep<? super C, ? extends U, ? extends E> step
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
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with empty name and returns a contextual steps chain
     * of the new context.
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
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given name and returns a contextual steps chain
     * of the new context.
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
        ThrowingFunction<? super C, ? extends U, ? extends E> action
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
        ThrowingFunction<? super C, ? extends U, ? extends E> action
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
        FunctionStep<? super C, ? extends R, ? extends E> step
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
        FunctionStep<? super C, ? extends R, ? extends E> step
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
        ThrowingFunction<? super C, ? extends R, ? extends E> action
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
        ThrowingFunction<? super C, ? extends R, ? extends E> action
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
        ThrowingFunction<? super C, ? extends R, ? extends E> action
    ) throws E;
}
