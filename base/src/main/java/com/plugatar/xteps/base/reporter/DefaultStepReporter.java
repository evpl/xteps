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
package com.plugatar.xteps.base.reporter;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.HookContainer;
import com.plugatar.xteps.base.StepListener;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;

import java.util.UUID;

/**
 * Default StepReporter.
 */
public class DefaultStepReporter implements StepReporter {
    private final StepListener[] listeners;

    /**
     * Ctor.
     *
     * @param listeners the listeners list
     * @throws NullPointerException     if {@code listeners} is null
     *                                  or {@code listeners} array contains null element
     * @throws IllegalArgumentException if {@code listeners} array is empty
     */
    public DefaultStepReporter(final StepListener[] listeners) {
        if (listeners == null) { throw new NullPointerException("listeners arg is null"); }
        if (listeners.length == 0) { throw new IllegalArgumentException("listeners arg array is empty"); }
        for (int idx = 0; idx < listeners.length; ++idx) {
            if (listeners[idx] == null) {
                throw new NullPointerException("listeners arg array element by index " + idx + " is null");
            }
        }
        this.listeners = listeners;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <R, E extends Throwable> R report(
        final HookContainer hookContainer,
        final ExceptionHandler exceptionHandler,
        final String name,
        final String description,
        final Object[] params,
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (hookContainer == null) { throwNullArgException("hookContainer"); }
        if (exceptionHandler == null) { throwNullArgException("exceptionHandler"); }
        if (name == null) { throwNullArgException("name"); }
        if (description == null) { throwNullArgException("description"); }
        if (params == null) { throwNullArgException("params"); }
        if (action == null) { throwNullArgException("action"); }
        /* Step start */
        final String uuid = UUID.randomUUID().toString();
        XtepsException listenerException = null;
        for (final StepListener listener : this.listeners) {
            try {
                listener.stepStarted(uuid, name, description, params);
            } catch (final Throwable ex) {
                if (listenerException == null) {
                    listenerException = listenerException();
                }
                listenerException.addSuppressed(ex);
            }
        }
        /* Step action */
        E stepException = null;
        R stepResult = null;
        try {
            stepResult = action.get();
        } catch (final Throwable ex) {
            stepException = (E) ex;
        }
        /* Step finish */
        for (final StepListener listener : this.listeners) {
            try {
                if (stepException == null) {
                    listener.stepPassed(uuid);
                } else {
                    listener.stepFailed(uuid, stepException);
                }
            } catch (final Throwable ex) {
                if (listenerException == null) {
                    listenerException = listenerException();
                }
                listenerException.addSuppressed(ex);
            }
        }
        /* Result */
        if (listenerException != null) {
            if (stepException != null) {
                listenerException.addSuppressed(stepException);
            }
            hookContainer.callHooks(listenerException);
            exceptionHandler.handle(listenerException);
            throw listenerException;
        } else if (stepException != null) {
            hookContainer.callHooks(stepException);
            exceptionHandler.handle(stepException);
            throw stepException;
        } else {
            return stepResult;
        }
    }

    private static XtepsException listenerException() {
        return new XtepsException("One or more listeners threw exceptions (see suppressed exceptions)");
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }
}
