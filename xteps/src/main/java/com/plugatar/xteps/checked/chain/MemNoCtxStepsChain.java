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

import com.plugatar.xteps.checked.chain.base.ACCtxStepsChain;
import com.plugatar.xteps.checked.chain.base.BaseCtxStepsChain;
import com.plugatar.xteps.checked.chain.base.BaseNoCtxStepsChain;
import com.plugatar.xteps.checked.chain.base.MemStepsChain;

/**
 * Memorizing no context steps chain.
 *
 * @param <PS> the previous context steps chain type
 */
public interface MemNoCtxStepsChain<PS extends BaseCtxStepsChain<?, ?>> extends
    BaseNoCtxStepsChain<MemNoCtxStepsChain<PS>>,
    MemStepsChain<PS>,
    ACCtxStepsChain<MemNoCtxStepsChain<PS>> {
}
