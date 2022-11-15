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

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Default HookContainer.
 */
public class DefaultHookContainer implements HookContainer {
    private final Deque<ThrowingRunnable<?>> deque;
    private final Object lock;

    /**
     * Ctor.
     */
    public DefaultHookContainer() {
        this.deque = new ConcurrentLinkedDeque<>();
        this.lock = new Object();
    }

    @Override
    public final void add(final ThrowingRunnable<?> hook) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.deque.addLast(hook);
    }

    @Override
    public final void callHooks() {
        synchronized (this.lock) {
            if (!this.deque.isEmpty()) {
                XtepsException baseEx = null;
                for (ThrowingRunnable<?> hook = this.deque.pollLast(); hook != null; hook = this.deque.pollLast()) {
                    try {
                        hook.run();
                    } catch (final Throwable ex) {
                        if (baseEx == null) {
                            baseEx = new XtepsException(
                                "One or more hooks threw exceptions (see suppressed exceptions)"
                            );
                        }
                        baseEx.addSuppressed(ex);
                    }
                }
                if (baseEx != null) {
                    throw baseEx;
                }
            }
        }
    }

    @Override
    public final void callHooks(final Throwable baseException) {
        if (baseException == null) { this.throwNullArgException("baseException"); }
        synchronized (this.lock) {
            if (!this.deque.isEmpty()) {
                for (ThrowingRunnable<?> hook = this.deque.pollLast(); hook != null; hook = this.deque.pollLast()) {
                    try {
                        hook.run();
                    } catch (final Throwable ex) {
                        baseException.addSuppressed(ex);
                    }
                }
            }
        }
    }

    private void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }
}
