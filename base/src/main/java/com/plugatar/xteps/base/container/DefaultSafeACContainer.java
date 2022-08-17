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

import com.plugatar.xteps.base.CloseException;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.XtepsException;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Default SafeACContainer.
 */
public class DefaultSafeACContainer implements SafeACContainer {
    private final Deque<AutoCloseable> acDeque;
    private final Object closeLock;

    /**
     * Ctor.
     */
    public DefaultSafeACContainer() {
        this.acDeque = new ConcurrentLinkedDeque<>();
        this.closeLock = new Object();
    }

    @Override
    public final void add(final AutoCloseable autoCloseable) {
        if (autoCloseable == null) { this.throwNullArgException("autoCloseable"); }
        this.acDeque.addLast(autoCloseable);
    }

    @Override
    public final void close() {
        synchronized (this.closeLock) {
            if (!this.acDeque.isEmpty()) {
                CloseException baseEx = null;
                for (AutoCloseable ac = this.acDeque.pollLast(); ac != null; ac = this.acDeque.pollLast()) {
                    try {
                        ac.close();
                    } catch (final Throwable ex) {
                        if (baseEx == null) {
                            baseEx = new CloseException(
                                "One or more AutoCloseables cannot be closed (see suppressed exceptions)"
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
    public final void close(final Throwable baseException) {
        if (baseException == null) { this.throwNullArgException("baseException"); }
        synchronized (this.closeLock) {
            if (!this.acDeque.isEmpty()) {
                for (AutoCloseable ac = this.acDeque.pollLast(); ac != null; ac = this.acDeque.pollLast()) {
                    try {
                        ac.close();
                    } catch (final Throwable ex) {
                        baseException.addSuppressed(ex);
                    }
                }
            }
        }
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.close(baseEx);
        throw baseEx;
    }
}
