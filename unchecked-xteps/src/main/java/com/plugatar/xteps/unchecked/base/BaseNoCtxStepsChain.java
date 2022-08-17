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

import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;

/**
 * Base no context steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code BaseNoCtxStepsChain}
 */
public interface BaseNoCtxStepsChain<S extends BaseNoCtxStepsChain<S>> extends BaseStepsChain<S> {

    /**
     * Returns a contextual steps chain of the new context.
     *
     * @param context the new context
     * @param <U>     the new context type
     * @return contextual steps chain
     * @see #withContext(ThrowingSupplier)
     */
    <U> BaseCtxStepsChain<U, ?> withContext(U context);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextSupplier the context supplier
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextSupplier} is null
     * @see #withContext(Object)
     */
    <U> BaseCtxStepsChain<U, ?> withContext(ThrowingSupplier<? extends U, ?> contextSupplier);

    /**
     * Performs given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String, ThrowingRunnable)
     */
    S step(String stepName,
           ThrowingRunnable<?> step);

    /**
     * Performs given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, ThrowingRunnable)
     */
    S step(String stepName,
           String stepDescription,
           ThrowingRunnable<?> step);

    /**
     * Performs given step with given name and returns a contextual steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepToContext(String, String, ThrowingSupplier)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(String stepName,
                                              ThrowingSupplier<? extends U, ?> step);

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
     * @see #stepToContext(String, ThrowingSupplier)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(String stepName,
                                              String stepDescription,
                                              ThrowingSupplier<? extends U, ?> step);

    /**
     * Performs given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, String, ThrowingSupplier)
     */
    <R> R stepTo(String stepName,
                 ThrowingSupplier<? extends R, ?> step);

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
     * @see #stepTo(String, ThrowingSupplier)
     */
    <R> R stepTo(String stepName,
                 String stepDescription,
                 ThrowingSupplier<? extends R, ?> step);
}
