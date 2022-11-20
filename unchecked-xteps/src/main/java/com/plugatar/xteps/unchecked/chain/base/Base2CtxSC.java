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
 */
public interface Base2CtxSC<C, C2> {

    /**
     * Adds given hook to this steps chain. This hook will be calls in case of any
     * exception in steps chain or in case of {@link BaseSC#callHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    BaseCtxSC<?> hook(ThrowingBiConsumer<C, C2, ?> hook);

    /**
     * Returns the second context.
     *
     * @return the second context
     */
    C2 context2();

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     */
    <U> BaseCtxSC<?> withContext(
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> contextFunction
    );

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     */
    BaseCtxSC<?> supplyContext(
        ThrowingBiConsumer<? super C, ? super C2, ?> consumer
    );

    /**
     * Apply context to given function and returns result.
     *
     * @param function the function
     * @param <R>      the {@code function} result type
     * @return the {@code function} result
     * @throws XtepsException if {@code function} is null
     */
    <R> R applyContext(
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> function
    );

    /**
     * Performs and reports given step and returns this steps chain.
     *
     * @param step the step
     * @return this steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    BaseCtxSC<?> step(
        BiConsumerStep<? super C, ? super C2> step
    );

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * this steps chain.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @return this steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    BaseCtxSC<?> step(
        String stepNamePrefix,
        BiConsumerStep<? super C, ? super C2> step
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
    BaseCtxSC<?> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super C2, ?> step
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
    BaseCtxSC<?> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super C2, ?> step
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
        BiFunctionStep<? super C, ? super C2, ? extends U> step
    );

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * a contextual steps chain of the new context.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <U>            the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<?> stepToContext(
        String stepNamePrefix,
        BiFunctionStep<? super C, ? super C2, ? extends U> step
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
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> step
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
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> step
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
     * Performs and reports given step with given prefix in the step name and returns
     * the step result.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <R>            the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String stepNamePrefix,
        BiFunctionStep<? super C, ? super C2, ? extends R> step
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
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> step
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
        ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> step
    );
}
