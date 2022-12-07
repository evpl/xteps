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

import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.checked.chain.base.Base1CtxSC;
import com.plugatar.xteps.checked.chain.base.BaseCtxSC;
import com.plugatar.xteps.checked.chain.base.BaseSC;
import com.plugatar.xteps.checked.chain.base.MemSC;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * Contextual steps chain.
 *
 * @param <C>  the context type
 * @param <PS> the previous steps chain type
 */
public interface CtxSC<C, PS extends BaseSC<PS>> extends
    BaseCtxSC<CtxSC<C, PS>>,
    Base1CtxSC<C, CtxSC<C, PS>>,
    MemSC<PS> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> Ctx2SC<U, C, CtxSC<C, PS>> withCtx(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> withCtx(
        ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> withCtx(
        ThrowingFunction<? super C, ? extends U, ? extends E> function
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        String keyword,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        String keyword,
        FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        String name,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        String name,
        ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        String name,
        String desc,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        String name,
        String desc,
        ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E;
}
