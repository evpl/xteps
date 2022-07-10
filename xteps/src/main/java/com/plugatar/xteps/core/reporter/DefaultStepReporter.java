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
package com.plugatar.xteps.core.reporter;

import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.StepReporter;
import com.plugatar.xteps.core.XtepsException;
import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Default step reporter.
 */
public class DefaultStepReporter implements StepReporter {
    private final StepListener[] listeners;

    /**
     * Ctor.
     *
     * @param listeners the listeners list
     * @throws NullPointerException     if {@code listeners} is null or {@code listeners} array contains null element
     * @throws IllegalArgumentException if {@code listeners} array is empty
     */
    public DefaultStepReporter(final StepListener[] listeners) {
        if (listeners == null) { throw new NullPointerException("listeners arg is null"); }
        for (int idx = 0; idx < listeners.length; ++idx) {
            if (listeners[idx] == null) {
                throw new NullPointerException("Listener with index " + idx + " is null");
            }
        }
        this.listeners = listeners;
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }

    @Override
    public final void reportEmptyStep(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        this.reportFunctionStep(stepName, stepDescription, null, n -> null);
    }

    @Override
    public final <E extends Throwable> void reportFailedStep(
        final String stepName,
        final String stepDescription,
        final E exception
    ) throws E {
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (exception == null) { throwNullArgException("exception"); }
        this.reportFunctionStep(stepName, stepDescription, null, n -> { throw exception; });
    }

    @Override
    public final <E extends Throwable> void reportRunnableStep(
        final String stepName,
        final String stepDescription,
        final ThrowingRunnable<? extends E> runnable
    ) throws E {
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (runnable == null) { throwNullArgException("runnable"); }
        this.reportFunctionStep(stepName, stepDescription, null, n -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public final <T, E extends Throwable> void reportConsumerStep(
        final String stepName,
        final String stepDescription,
        final T input,
        final ThrowingConsumer<? super T, ? extends E> consumer
    ) throws E {
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (consumer == null) { throwNullArgException("consumer"); }
        this.reportFunctionStep(stepName, stepDescription, null, n -> {
            consumer.accept(input);
            return null;
        });
    }

    @Override
    public final <T, E extends Throwable> T reportSupplierStep(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends T, ? extends E> supplier
    ) throws E {
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (supplier == null) { throwNullArgException("supplier"); }
        return this.reportFunctionStep(stepName, stepDescription, null, n -> supplier.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T, R, E extends Throwable> R reportFunctionStep(
        final String stepName,
        final String stepDescription,
        final T input,
        final ThrowingFunction<? super T, ? extends R, ? extends E> function
    ) throws E {
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (function == null) { throwNullArgException("function"); }
        final String uuid = UUID.randomUUID().toString();
        XtepsException baseException = null;
        // step start
        for (final StepListener listener : this.listeners) {
            try {
                listener.stepStarted(uuid, stepName, stepDescription);
            } catch (final Throwable ex) {
                if (baseException == null) {
                    baseException = listenerException();
                }
                baseException.addSuppressed(ex);
            }
        }
        // step action
        E stepEx = null;
        R result = null;
        try {
            result = function.apply(input);
        } catch (final Throwable ex) {
            cleanStackTraceIfNotXtepsException(ex);
            if (baseException != null) {
                baseException.addSuppressed(ex);
            }
            stepEx = (E) ex;
        }
        // step finish
        for (final StepListener listener : this.listeners) {
            try {
                if (baseException != null) {
                    listener.stepFailed(uuid, baseException);
                } else if (stepEx != null) {
                    listener.stepFailed(uuid, stepEx);
                } else {
                    listener.stepPassed(uuid);
                }
            } catch (final Throwable ex) {
                if (baseException == null) {
                    baseException = listenerException();
                }
                baseException.addSuppressed(ex);
            }
        }
        if (baseException != null) {
            throw baseException;
        } else if (stepEx != null) {
            throw stepEx;
        } else {
            return result;
        }
    }

    private static XtepsException listenerException() {
        return new XtepsException("One or more listeners threw exceptions");
    }

    private static void cleanStackTraceIfNotXtepsException(final Throwable mainTh) {
        if (!(mainTh instanceof XtepsException)) {
            final Set<Throwable> throwables = new HashSet<>();
            addAllThrowables(throwables, mainTh);
            for (final Throwable currentTh : throwables) {
                if (!(currentTh instanceof XtepsException)) {
                    final StackTraceElement[] originST = currentTh.getStackTrace();
                    if (originST.length != 0) {
                        final StackTraceElement[] cleanST = Arrays.stream(originST)
                            .filter(element -> !element.getClassName().startsWith("com.plugatar.xteps"))
                            .toArray(StackTraceElement[]::new);
                        if (cleanST.length != originST.length) {
                            currentTh.setStackTrace(cleanST);
                        }
                    }
                }
            }
        }
    }

    private static void addAllThrowables(final Set<Throwable> throwables,
                                         final Throwable mainTh) {
        for (Throwable currentTh = mainTh; currentTh != null; currentTh = currentTh.getCause()) {
            if (throwables.contains(currentTh)) {
                break;
            }
            throwables.add(currentTh);
            for (final Throwable suppressedTh : currentTh.getSuppressed()) {
                addAllThrowables(throwables, suppressedTh);
            }
        }
    }
}
