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
package com.plugatar.xteps.checked.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingPredicate;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.MemCtxStepsChain;
import com.plugatar.xteps.checked.MemNoCtxStepsChain;

/**
 * Base contextual steps chain.
 *
 * @param <C> the context type
 * @param <S> the type of the steps chain implementing {@code BaseCtxStepsChain}
 */
public interface BaseCtxStepsChain<C, S extends BaseCtxStepsChain<C, S>> extends
    BaseStepsChain<S>,
    ACContextsStepsChain<S> {

    /**
     * Returns the context.
     *
     * @return the context
     * @see #contextIsAutoCloseable()
     * @see #closeAutoCloseableContexts()
     */
    C context();

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @param <E>      the {@code consumer} exception type
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     * @throws E              if {@code consumer} threw exception
     * @see #context()
     * @see #applyContext(ThrowingFunction)
     * @see #testContext(ThrowingPredicate)
     */
    <E extends Throwable> S supplyContext(
        ThrowingConsumer<? super C, ? extends E> consumer
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
     * @see #context()
     * @see #supplyContext(ThrowingConsumer)
     * @see #testContext(ThrowingPredicate)
     */
    <R, E extends Throwable> R applyContext(
        ThrowingFunction<? super C, ? extends R, ? extends E> function
    ) throws E;

    /**
     * Apply context to given predicate and returns result.
     *
     * @param predicate the predicate
     * @param <E>       the {@code predicate} exception type
     * @return the {@code predicate} result
     * @throws XtepsException if {@code predicate} is null
     * @throws E              if {@code predicate} threw exception
     * @see #context()
     * @see #supplyContext(ThrowingConsumer)
     * @see #applyContext(ThrowingFunction)
     */
    <E extends Throwable> boolean testContext(
        ThrowingPredicate<? super C, ? extends E> predicate
    ) throws E;

    /**
     * Returns a contextual steps chain of the new context.
     *
     * @param context the new context
     * @param <U>     the new context type
     * @return contextual steps chain
     * @see #withContext(ThrowingFunction)
     * @see #withoutContext()
     */
    <U> MemCtxStepsChain<U, S> withContext(U context);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @param <E>             the exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     * @throws E              if {@code contextFunction} threw exception
     * @see #withContext(Object)
     * @see #withoutContext()
     */
    <U, E extends Throwable> MemCtxStepsChain<U, S> withContext(
        ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * Returns a no context steps chain.
     *
     * @return no context steps chain
     */
    MemNoCtxStepsChain<S> withoutContext();

    /**
     * Append the current context to the cleanup queue. This context will be closed in case
     * of any exception in steps chain or in case of {@link #closeAutoCloseableContexts()}
     * method invocation.
     *
     * @return this steps chain
     * @throws XtepsException if the current context is not an {@link AutoCloseable} instance
     * @see #context()
     * @see #closeAutoCloseableContexts()
     */
    S contextIsAutoCloseable();

    /**
     * Performs given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <E>      the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #step(String, String, ThrowingConsumer)
     */
    <E extends Throwable> S step(
        String stepName,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <E>             the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #step(String, ThrowingConsumer)
     */
    <E extends Throwable> S step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and returns a contextual steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @param <E>      the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #stepToContext(String, String, ThrowingFunction)
     */
    <U, E extends Throwable> MemCtxStepsChain<U, S> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and description and returns a contextual steps chain of the new context.
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
     * @see #stepToContext(String, ThrowingFunction)
     */
    <U, E extends Throwable> MemCtxStepsChain<U, S> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @param <E>      the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #stepTo(String, String, ThrowingFunction)
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        ThrowingFunction<? super C, ? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and description and returns the step result.
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
     * @see #stepTo(String, ThrowingFunction)
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends R, ? extends E> step
    ) throws E;
}
