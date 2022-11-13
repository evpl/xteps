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
package com.plugatar.xteps.checked.chain;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.checked.chain.base.Base1CtxStepsChain;
import com.plugatar.xteps.checked.chain.base.BaseCtxStepsChain;
import com.plugatar.xteps.checked.stepobject.ConsumerStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * Contextual steps chain.
 *
 * @param <C> the context type
 */
public interface CtxStepsChain<C> extends
    BaseCtxStepsChain<C, CtxStepsChain<C>>,
    Base1CtxStepsChain<C> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> withContext(
        ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> withContext(
        ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> CtxStepsChain<C> supplyContext(
        ThrowingConsumer<? super C, ? extends E> consumer
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> CtxStepsChain<C> step(
        ConsumerStep<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> CtxStepsChain<C> step(
        String stepNamePrefix,
        ConsumerStep<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> CtxStepsChain<C> step(
        String stepName,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> CtxStepsChain<C> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        String stepNamePrefix,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        String stepNamePrefix,
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem1CtxStepsChain<U, C, CtxStepsChain<C>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;
}
