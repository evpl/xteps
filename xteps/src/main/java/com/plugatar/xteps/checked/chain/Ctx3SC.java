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
import com.plugatar.xteps.checked.chain.base.BaseSC;
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
 * @param <PS> the previous steps chain type
 */
public interface Ctx3SC<C, C2, C3, PS extends BaseSC<PS>> extends
    BaseCtxSC<Ctx3SC<C, C2, C3, PS>>,
    Base1CtxSC<C, Ctx3SC<C, C2, C3, PS>>,
    Base2CtxSC<C, C2, Ctx3SC<C, C2, C3, PS>>,
    Base3CtxSC<C, C2, C3, Ctx3SC<C, C2, C3, PS>>,
    MemSC<PS> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        ThrowingFunction<? super C, ? extends U, ? extends E> function
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> function
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> function
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String keyword,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String keyword,
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String keyword,
        BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String keyword,
        TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        String desc,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        String desc,
        ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        String desc,
        ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        String name,
        String desc,
        ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
    ) throws E;
}
