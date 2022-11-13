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

import com.plugatar.xteps.base.CloseException;

/**
 * Steps chain containing an {@link AutoCloseable} contexts.
 *
 * @param <S> the type of the steps chain implementing {@code SC}
 */
public interface ACCtxSC<S extends BaseSC<S>> extends BaseSC<S> {

    /**
     * Close all {@link AutoCloseable} contexts.
     *
     * @return this steps chain
     * @throws CloseException if at least one of {@link AutoCloseable#close()} methods
     *                        invocation throws any exception
     */
    S closeCloseableContexts();
}