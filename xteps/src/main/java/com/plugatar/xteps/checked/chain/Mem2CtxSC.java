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
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.checked.chain.base.ACCtxSC;
import com.plugatar.xteps.checked.chain.base.Base1CtxSC;
import com.plugatar.xteps.checked.chain.base.Base2CtxSC;
import com.plugatar.xteps.checked.chain.base.BaseCtxSC;
import com.plugatar.xteps.checked.chain.base.MemSC;
import com.plugatar.xteps.checked.stepobject.BiConsumerStep;
import com.plugatar.xteps.checked.stepobject.BiFunctionStep;
import com.plugatar.xteps.checked.stepobject.ConsumerStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * Memorizing contextual steps chain.
 *
 * @param <C>  the context type
 * @param <P>  the previous context type
 * @param <PS> the previous context steps chain type
 */
public interface Mem2CtxSC<C, P, PS extends BaseCtxSC<?, ?>> extends
    BaseCtxSC<C, Mem2CtxSC<C, P, PS>>,
    Base1CtxSC<C>,
    Base2CtxSC<C, P>,
    MemSC<PS>,
    ACCtxSC<Mem2CtxSC<C, P, PS>> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(
        ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(
        ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> supplyContext(
        ThrowingConsumer<? super C, ? extends E> consumer
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> supplyContext(
        ThrowingBiConsumer<? super C, ? super P, ? extends E> consumer
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        ConsumerStep<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        BiConsumerStep<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        String stepNamePrefix,
        ConsumerStep<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        String stepNamePrefix,
        BiConsumerStep<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        String stepName,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Throwable> Mem2CtxSC<C, P, PS> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        BiFunctionStep<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepNamePrefix,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepNamePrefix,
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepNamePrefix,
        BiFunctionStep<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;
}