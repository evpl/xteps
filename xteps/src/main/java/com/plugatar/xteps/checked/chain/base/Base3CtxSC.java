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
 */
public interface Base3CtxSC<C, C2, C3> {

    /**
     * Adds given hook to this steps chain. This hook will be calls in case of any
     * exception in steps chain or in case of {@link BaseSC#callHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    BaseCtxSC<?> hook(ThrowingTriConsumer<C, C2, C3, ?> hook);

    /**
     * Returns the third context.
     *
     * @return the third context
     */
    C3 context3();

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @param <E>             the {@code contextFunction} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     * @throws E              if {@code contextFunction} threw exception
     */
    <U, E extends Throwable> BaseCtxSC<?> withContext(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @param <E>      the {@code consumer} exception type
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     * @throws E              if {@code consumer} threw exception
     */
    <E extends Throwable> BaseCtxSC<?> supplyContext(
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> consumer
    ) throws E;

    /**
     * Apply context to given function and returns result.
     *
     * @param function the function
     * @param <E>      the {@code function} exception type
     * @param <R>      the {@code function} result type
     * @return the {@code function} result
     * @throws XtepsException if {@code function} is null
     * @throws E              if {@code function} threw exception
     */
    <R, E extends Throwable> R applyContext(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> function
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
    <E extends Throwable> BaseCtxSC<?> step(
        TriConsumerStep<? super C, ? super C2, ? super C3, ? extends E> step
    ) throws E;

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * this steps chain.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <E>            the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    <E extends Throwable> BaseCtxSC<?> step(
        String stepNamePrefix,
        TriConsumerStep<? super C, ? super C2, ? super C3, ? extends E> step
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
    <E extends Throwable> BaseCtxSC<?> step(
        String stepName,
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> step
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
    <E extends Throwable> BaseCtxSC<?> step(
        String stepName,
        String stepDescription,
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> step
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
    <U, E extends Throwable> BaseCtxSC<?> stepToContext(
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
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
    <U, E extends Throwable> BaseCtxSC<?> stepToContext(
        String stepNamePrefix,
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
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
    <U, E extends Throwable> BaseCtxSC<?> stepToContext(
        String stepName,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
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
    <U, E extends Throwable> BaseCtxSC<?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
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
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
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
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
    ) throws E;
}
