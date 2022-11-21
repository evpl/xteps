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

import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.checked.chain.base.Base1CtxSC;
import com.plugatar.xteps.checked.chain.base.Base2CtxSC;
import com.plugatar.xteps.checked.chain.base.Base3CtxSC;
import com.plugatar.xteps.checked.chain.base.BaseCtxSC;
import com.plugatar.xteps.checked.chain.base.MemSC;
import com.plugatar.xteps.checked.stepobject.BiFunctionStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;
import com.plugatar.xteps.checked.stepobject.TriFunctionStep;

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
    Base1CtxSC<C, Mem3CtxSC<C, C2, C3, PS>>,
    Base2CtxSC<C, C2, Mem3CtxSC<C, C2, C3, PS>>,
    Base3CtxSC<C, C2, C3, Mem3CtxSC<C, C2, C3, PS>>,
    MemSC<PS> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String keyword,
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;
}
