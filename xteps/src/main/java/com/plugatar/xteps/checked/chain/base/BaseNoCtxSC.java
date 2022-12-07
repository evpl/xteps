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
package com.plugatar.xteps.checked.chain.base;

import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.checked.chain.CtxSC;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * Base no context steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code BaseNoCtxSC}
 */
public interface BaseNoCtxSC<S extends BaseNoCtxSC<S>> extends BaseSC<S> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxSC<U, S> withCtx(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> CtxSC<U, S> withCtx(
        ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> CtxSC<U, S> stepToCtx(
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> CtxSC<U, S> stepToCtx(
        String keyword,
        SupplierStep<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> CtxSC<U, S> stepToCtx(
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> CtxSC<U, S> stepToCtx(
        String name,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> CtxSC<U, S> stepToCtx(
        String name,
        String desc,
        ThrowingSupplier<? extends U, ? extends E> action
    ) throws E;
}
