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
package com.plugatar.xteps.core;

/**
 * Steps chain memorizing a previous steps chain.
 *
 * @param <S> the type of the steps chain implementing {@code MemorizingStepsChain}
 * @param <P> the previous steps chain type
 */
public interface MemorizingStepsChain<S extends BaseStepsChain<S>, P extends BaseStepsChain<?>>
    extends BaseStepsChain<S> {

    /**
     * Returns the previous steps chain.
     *
     * @return previous steps chain
     */
    P previousStepsChain();
}
