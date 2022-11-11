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
import com.plugatar.xteps.unchecked.SupplierStep;
import com.plugatar.xteps.unchecked.chain.CtxStepsChain;

/**
 * Base no context steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code BaseNoCtxStepsChain}
 */
public interface BaseNoCtxStepsChain<S extends BaseNoCtxStepsChain<S>> extends BaseStepsChain<S> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxStepsChain<U> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxStepsChain<U> withContext(
        ThrowingSupplier<? extends U, ?> contextSupplier
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxStepsChain<U> stepToContext(
        SupplierStep<? extends U> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxStepsChain<U> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ?> step
    );

    /**
     * {@inheritDoc}
     */
    @Override
    <U> CtxStepsChain<U> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ?> step
    );
}
