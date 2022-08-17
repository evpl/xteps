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
package com.plugatar.xteps.unchecked.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingPredicate;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.MemCtxStepsChain;
import com.plugatar.xteps.unchecked.MemNoCtxStepsChain;

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
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     * @see #context()
     * @see #applyContext(ThrowingFunction)
     * @see #testContext(ThrowingPredicate)
     */
    S supplyContext(ThrowingConsumer<? super C, ?> consumer);

    /**
     * Apply context to given function and returns result.
     *
     * @param function the function
     * @param <R>      the {@code function} result type
     * @return the {@code function} result
     * @throws XtepsException if {@code function} is null
     * @see #context()
     * @see #supplyContext(ThrowingConsumer)
     * @see #testContext(ThrowingPredicate)
     */
    <R> R applyContext(ThrowingFunction<? super C, ? extends R, ?> function);

    /**
     * Apply context to given predicate and returns result.
     *
     * @param predicate the predicate
     * @return the {@code predicate} result
     * @throws XtepsException if {@code predicate} is null
     * @see #context()
     * @see #supplyContext(ThrowingConsumer)
     * @see #applyContext(ThrowingFunction)
     */
    boolean testContext(ThrowingPredicate<? super C, ?> predicate);

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
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     * @see #withContext(Object)
     * @see #withoutContext()
     */
    <U> MemCtxStepsChain<U, S> withContext(ThrowingFunction<? super C, ? extends U, ?> contextFunction);

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
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String, ThrowingConsumer)
     */
    S step(String stepName,
           ThrowingConsumer<? super C, ?> step);

    /**
     * Performs given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, ThrowingConsumer)
     */
    S step(String stepName,
           String stepDescription,
           ThrowingConsumer<? super C, ?> step);

    /**
     * Performs given step with given name and returns a contextual steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepToContext(String, String, ThrowingFunction)
     */
    <U> MemCtxStepsChain<U, S> stepToContext(String stepName,
                                             ThrowingFunction<? super C, ? extends U, ?> step);

    /**
     * Performs given step with given name and description and returns a contextual steps chain of the new context.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepToContext(String, ThrowingFunction)
     */
    <U> MemCtxStepsChain<U, S> stepToContext(String stepName,
                                             String stepDescription,
                                             ThrowingFunction<? super C, ? extends U, ?> step);

    /**
     * Performs given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, String, ThrowingFunction)
     */
    <R> R stepTo(String stepName,
                 ThrowingFunction<? super C, ? extends R, ?> step);

    /**
     * Performs given step with given name and description and returns the step result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, ThrowingFunction)
     */
    <R> R stepTo(String stepName,
                 String stepDescription,
                 ThrowingFunction<? super C, ? extends R, ?> step);
}
