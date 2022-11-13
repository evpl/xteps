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

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.chain.MemNoCtxSC;

/**
 * Base contextual steps chain.
 *
 * @param <C> the context type
 * @param <S> the type of the steps chain implementing {@code BaseCtxStepsChain}
 */
public interface BaseCtxSC<C, S extends BaseCtxSC<C, S>> extends
    BaseSC<S>,
    ACCtxSC<S> {

    /**
     * Returns the context.
     *
     * @return the context
     */
    C context();

    /**
     * Returns a no context steps chain.
     *
     * @return no context steps chain
     */
    MemNoCtxSC<S> withoutContext();

    /**
     * Append the current context to the cleanup queue. This context will be closed in case
     * of any exception in steps chain or in case of {@link #closeCloseableContexts()}
     * method invocation.
     *
     * @return this steps chain
     * @throws XtepsException if the current context is not an {@link AutoCloseable} instance
     */
    S contextIsCloseable();

    /**
     * Append the current context with given close action to the cleanup queue. This context will be
     * closed in case of any exception in steps chain or in case of {@link #closeCloseableContexts()}
     * method invocation.
     *
     * @param close the close action
     * @return this steps chain
     * @throws XtepsException if {@code close} is null
     */
    S contextIsCloseable(ThrowingConsumer<? super C, ?> close);
}
