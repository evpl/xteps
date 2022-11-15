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
package com.plugatar.xteps.base.container;

import com.plugatar.xteps.base.HookContainer;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsException;

/**
 * Fake HookContainer.
 */
public class FakeHookContainer implements HookContainer {

    /**
     * Ctor.
     */
    public FakeHookContainer() {
    }

    @Override
    public final void add(final ThrowingRunnable<?> hook) {
        if (hook == null) { this.throwNullArgException("hook"); }
    }

    @Override
    public final void callHooks() {
    }

    @Override
    public final void callHooks(final Throwable baseException) {
        if (baseException == null) { this.throwNullArgException("baseException"); }
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.callHooks(baseEx);
        throw baseEx;
    }
}
