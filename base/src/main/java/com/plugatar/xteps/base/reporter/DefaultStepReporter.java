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
import com.plugatar.xteps.base.OptionalValue;
import com.plugatar.xteps.base.SafeACContainer;
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
    private final SafeACContainer fakeSafeACContainer;

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
        this.fakeSafeACContainer = new FakeSafeACContainer();
    }

    @Override
    public <R, E extends Throwable> R report(
        final ExceptionHandler exceptionHandler,
        final String stepName,
        final String stepDescription,
        final OptionalValue<?> optionalContext,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        return this.report(
            this.fakeSafeACContainer, exceptionHandler, stepName, stepDescription, optionalContext, step
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <R, E extends Throwable> R report(
        final SafeACContainer safeACContainer,
        final ExceptionHandler exceptionHandler,
        final String stepName,
        final String stepDescription,
        final OptionalValue<?> optionalContext,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        if (safeACContainer == null) { throwNullArgException("safeACContainer"); }
        if (exceptionHandler == null) { throwNullArgException("exceptionHandler"); }
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (optionalContext == null) { throwNullArgException("optionalContext"); }
        if (step == null) { throwNullArgException("step"); }
        /* Step start */
        final String uuid = UUID.randomUUID().toString();
        XtepsException listenerException = null;
        for (final StepListener listener : this.listeners) {
            try {
                listener.stepStarted(uuid, stepName, stepDescription, optionalContext);
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
            stepResult = step.get();
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
            safeACContainer.close(listenerException);
            exceptionHandler.handle(listenerException);
            throw listenerException;
        } else if (stepException != null) {
            safeACContainer.close(stepException);
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

    private static final class FakeSafeACContainer implements SafeACContainer {

        private FakeSafeACContainer() {
        }

        @Override
        public void add(final AutoCloseable autoCloseable) {
        }

        @Override
        public void close() {
        }

        @Override
        public void close(final Throwable baseException) {
        }
    }
}
