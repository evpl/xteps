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
package com.plugatar.xteps.unchecked;

import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.unchecked.base.ACCtxStepsChain;
import com.plugatar.xteps.unchecked.base.Base1CtxStepsChain;
import com.plugatar.xteps.unchecked.base.Base2CtxStepsChain;
import com.plugatar.xteps.unchecked.base.Base3CtxStepsChain;
import com.plugatar.xteps.unchecked.base.BaseCtxStepsChain;
import com.plugatar.xteps.unchecked.base.MemStepsChain;

/**
 * Extended memorizing contextual steps chain.
 *
 * @param <C>  the context type
 * @param <P1> the previous context type
 * @param <P2> the previous context type
 * @param <PS> the previous context steps chain type
 */
public interface Mem2CtxStepsChain<C, P1, P2, PS extends BaseCtxStepsChain<?, ?>> extends
    BaseCtxStepsChain<C, Mem2CtxStepsChain<C, P1, P2, PS>>,
    Base1CtxStepsChain<C>,
    Base2CtxStepsChain<C, P1>,
    Base3CtxStepsChain<C, P1, P2>,
    MemStepsChain<PS>,
    ACCtxStepsChain<Mem2CtxStepsChain<C, P1, P2, PS>> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        ThrowingFunction<? super C, ? extends U, ?> contextFunction
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        ThrowingBiFunction<? super C, ? super P1, ? extends U, ?> contextFunction
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> contextFunction
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> supplyContext(
        ThrowingConsumer<? super C, ?> consumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> supplyContext(
        ThrowingBiConsumer<? super C, ? super P1, ?> consumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> supplyContext(
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> consumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        ThrowingBiConsumer<? super C, ? super P1, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        String stepName,
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super P1, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        String stepName,
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super P1, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem2CtxStepsChain<C, P1, P2, PS> step(
        String stepName,
        String stepDescription,
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        ThrowingBiFunction<? super C, ? super P1, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super P1, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        String stepName,
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P1, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
    );
}
