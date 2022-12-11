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
package com.plugatar.xteps.base.hook;

import com.plugatar.xteps.base.HooksContainer;
import com.plugatar.xteps.base.HooksOrder;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.base.HookPriority.MAX_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.MIN_HOOK_PRIORITY;

/**
 * Fake hooks container.
 */
public class FakeHooksContainer implements HooksContainer {

    /**
     * Ctor.
     */
    public FakeHooksContainer() {
    }

    @Override
    public final void addHook(final int priority,
                              final ThrowingRunnable<?> hook) {
        if (hook == null) { throwNullArgException("hook"); }
        if (priority < MIN_HOOK_PRIORITY || priority > MAX_HOOK_PRIORITY) {
            throw new XtepsException("priority arg not in the range " + MIN_HOOK_PRIORITY + " to " + MAX_HOOK_PRIORITY);
        }
    }

    @Override
    public final void setOrder(final HooksOrder order) {
        if (order == null) { throwNullArgException("order"); }
    }

    @Override
    public final void callHooks() {
    }

    @Override
    public final void callHooks(final Throwable baseException) {
        if (baseException == null) { throwNullArgException("baseException"); }
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }
}
