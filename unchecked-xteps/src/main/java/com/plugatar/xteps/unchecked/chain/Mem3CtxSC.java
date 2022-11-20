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
package com.plugatar.xteps.unchecked.chain;

import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.unchecked.chain.base.Base1CtxSC;
import com.plugatar.xteps.unchecked.chain.base.Base2CtxSC;
import com.plugatar.xteps.unchecked.chain.base.Base3CtxSC;
import com.plugatar.xteps.unchecked.chain.base.BaseCtxSC;
import com.plugatar.xteps.unchecked.chain.base.MemSC;
import com.plugatar.xteps.unchecked.stepobject.BiConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.BiFunctionStep;
import com.plugatar.xteps.unchecked.stepobject.ConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.FunctionStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;
import com.plugatar.xteps.unchecked.stepobject.TriConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.TriFunctionStep;

/**
 * Extended memorizing contextual steps chain.
 *
 * @param <C>  the context type
 * @param <C2> the second context type
 * @param <C3> the third context type
 * @param <PS> the previous context steps chain type
 */
public interface Mem3CtxSC<C, C2, C3, PS extends BaseCtxSC<?>> extends
    BaseCtxSC<Mem3CtxSC<C, C2, C3, PS>>,
    Base1CtxSC<C>,
    Base2CtxSC<C, C2>,
    Base3CtxSC<C, C2, C3>,
    MemSC<PS> {

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> hook(ThrowingConsumer<C, ?> hook);

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> hook(ThrowingBiConsumer<C, C2, ?> hook);

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> hook(ThrowingTriConsumer<C, C2, C3, ?> hook);

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingSupplier<? extends U, ?> contextSupplier
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingFunction<? super C, ? extends U, ?> contextFunction
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> contextFunction
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> contextFunction
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> supplyContext(
        ThrowingConsumer<? super C, ?> consumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> supplyContext(
        ThrowingBiConsumer<? super C, ? super C2, ?> consumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> supplyContext(
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> consumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        ConsumerStep<? super C> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        BiConsumerStep<? super C, ? super C2> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        TriConsumerStep<? super C, ? super C2, ? super C3> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String keyword,
        ConsumerStep<? super C> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String keyword,
        BiConsumerStep<? super C, ? super C2> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String keyword,
        TriConsumerStep<? super C, ? super C2, ? super C3> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String stepName,
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super C2, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String stepName,
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super C2, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    Mem3CtxSC<C, C2, C3, PS> step(
        String stepName,
        String stepDescription,
        ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        SupplierStep<? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        FunctionStep<? super C, ? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        BiFunctionStep<? super C, ? super C2, ? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        SupplierStep<? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        FunctionStep<? super C, ? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        BiFunctionStep<? super C, ? super C2, ? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> step
    );
}
