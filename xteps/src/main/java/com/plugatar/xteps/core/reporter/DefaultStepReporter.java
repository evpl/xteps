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
                throw new NullPointerException("listener with index " + idx + " is null");
            }
        }
        this.listeners = listeners;
    }

    private void throwNullArgException(final String methodName,
                                       final String argName) {
        final String message = "StepReporter " + methodName + " method " + argName + " arg is null";
        this.reportFailedStep(message, new XtepsException(message));
    }

    @Override
    public final void reportEmptyStep(final String stepName) {
        if (stepName == null) { throwNullArgException("reportEmptyStep", "stepName"); }
        this.reportFunctionStep(stepName, null, n -> null);
    }

    @Override
    public final <TH extends Throwable> void reportFailedStep(
        final String stepName,
        final TH exception
    ) throws TH {
        if (stepName == null) { throwNullArgException("reportFailedStep", "stepName"); }
        if (exception == null) { throwNullArgException("reportFailedStep", "exception"); }
        this.reportFunctionStep(stepName, null, n -> { throw exception; });
    }

    @Override
    public final <TH extends Throwable> void reportRunnableStep(
        final String stepName,
        final ThrowingRunnable<? extends TH> runnable
    ) throws TH {
        if (stepName == null) { throwNullArgException("reportRunnableStep", "stepName"); }
        if (runnable == null) { throwNullArgException("reportRunnableStep", "runnable"); }
        this.reportFunctionStep(stepName, null, n -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public final <T, TH extends Throwable> void reportConsumerStep(
        final String stepName,
        final T input,
        final ThrowingConsumer<? super T, ? extends TH> consumer
    ) throws TH {
        if (stepName == null) { throwNullArgException("reportConsumerStep", "stepName"); }
        if (consumer == null) { throwNullArgException("reportConsumerStep", "consumer"); }
        this.reportFunctionStep(stepName, null, n -> {
            consumer.accept(input);
            return null;
        });
    }

    @Override
    public final <T, TH extends Throwable> T reportSupplierStep(
        String stepName,
        ThrowingSupplier<? extends T, ? extends TH> supplier
    ) throws TH {
        if (stepName == null) { throwNullArgException("reportSupplierStep", "stepName"); }
        if (supplier == null) { throwNullArgException("reportSupplierStep", "supplier"); }
        return this.reportFunctionStep(stepName, null, n -> supplier.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T, R, TH extends Throwable> R reportFunctionStep(
        final String stepName,
        final T input,
        final ThrowingFunction<? super T, ? extends R, ? extends TH> function
    ) throws TH {
        if (stepName == null) { throwNullArgException("reportFunctionStep", "stepName"); }
        if (function == null) { throwNullArgException("reportFunctionStep", "function"); }
        final String uuid = UUID.randomUUID().toString();
        XtepsException baseException = null;
        // step started
        for (final StepListener listener : this.listeners) {
            try {
                listener.stepStarted(uuid, stepName);
            } catch (final Throwable ex) {
                if (baseException == null) {
                    baseException = listenerException();
                }
                baseException.addSuppressed(ex);
            }
        }
        // step action
        TH stepEx = null;
        R result = null;
        try {
            result = function.apply(input);
        } catch (final Throwable ex) {
            cleanStackTraceIfNotXtepsException(ex);
            if (baseException != null) {
                baseException.addSuppressed(ex);
            }
            stepEx = (TH) ex;
        }
        // step passed or failed
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
