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
package com.plugatar.xteps.unchecked.chain.base;

import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.unchecked.chain.CtxSC;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;

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
    <U> CtxSC<U> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxSC<U> withContext(
        ThrowingSupplier<? extends U, ?> contextSupplier
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxSC<U> stepToContext(
        SupplierStep<? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxSC<U> stepToContext(
        String keyword,
        SupplierStep<? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxSC<U> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxSC<U> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ?> step
    );
}
