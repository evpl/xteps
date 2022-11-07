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

import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.checked.BiConsumerStep;
import com.plugatar.xteps.checked.BiFunctionStep;
import com.plugatar.xteps.checked.ConsumerStep;
import com.plugatar.xteps.checked.FunctionStep;
import com.plugatar.xteps.checked.chain.base.ACCtxStepsChain;
import com.plugatar.xteps.checked.chain.base.Base1CtxStepsChain;
import com.plugatar.xteps.checked.chain.base.Base2CtxStepsChain;
import com.plugatar.xteps.checked.chain.base.BaseCtxStepsChain;
import com.plugatar.xteps.checked.chain.base.MemStepsChain;

/**
 * Memorizing contextual steps chain.
 *
 * @param <C>  the context type
 * @param <P>  the previous context type
 * @param <PS> the previous context steps chain type
 */
public interface Mem1CtxStepsChain<C, P, PS extends BaseCtxStepsChain<?, ?>> extends
    BaseCtxStepsChain<C, Mem1CtxStepsChain<C, P, PS>>,
    Base1CtxStepsChain<C>,
    Base2CtxStepsChain<C, P>,
    MemStepsChain<PS>,
    ACCtxStepsChain<Mem1CtxStepsChain<C, P, PS>> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> withContext(
        ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> withContext(
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> supplyContext(
        ThrowingConsumer<? super C, ? extends E> consumer
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> supplyContext(
        ThrowingBiConsumer<? super C, ? super P, ? extends E> consumer
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> step(
        ConsumerStep<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> step(
        BiConsumerStep<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> step(
        String stepName,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem1CtxStepsChain<C, P, PS> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        BiFunctionStep<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;
}