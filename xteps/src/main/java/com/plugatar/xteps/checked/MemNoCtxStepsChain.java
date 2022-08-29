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
package com.plugatar.xteps.checked;

import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.checked.base.ACContextsStepsChain;
import com.plugatar.xteps.checked.base.BaseCtxStepsChain;
import com.plugatar.xteps.checked.base.BaseNoCtxStepsChain;
import com.plugatar.xteps.checked.base.MemorizingContextStepsChain;

/**
 * Memorizing no context steps chain.
 *
 * @param <P> the previous context steps chain type
 */
public interface MemNoCtxStepsChain<P extends BaseCtxStepsChain<?, ?>> extends
    BaseNoCtxStepsChain<MemNoCtxStepsChain<P>>,
    MemorizingContextStepsChain<MemNoCtxStepsChain<P>, P>,
    ACContextsStepsChain<MemNoCtxStepsChain<P>> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> MemCtxStepsChain<U, P> withContext(U context);

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> MemCtxStepsChain<U, P> withContext(
        ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> MemCtxStepsChain<U, P> stepToContext(
        String stepName,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    <U, E extends Throwable> MemCtxStepsChain<U, P> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingSupplier<? extends U, ? extends E> step
    ) throws E;
}