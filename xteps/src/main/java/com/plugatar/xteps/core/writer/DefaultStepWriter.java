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
package com.plugatar.xteps.core.writer;

import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.StepWriter;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepWriteException;
import com.plugatar.xteps.core.exception.XtepsException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Default step writer.
 */
public class DefaultStepWriter implements StepWriter {
    private final StepListener listener;
    private final boolean cleanStackTrace;

    /**
     * Ctor.
     *
     * @param listener        the listener
     * @param cleanStackTrace the clean stack trace flag
     * @throws ArgumentException if {@code listener} is null
     */
    public DefaultStepWriter(final StepListener listener, final boolean cleanStackTrace) {
        if (listener == null) { throw new ArgumentException("listener arg is null"); }
        this.listener = listener;
        this.cleanStackTrace = cleanStackTrace;
    }

    @Override
    public final void writeEmptyStep(final String stepName) {
        if (stepName == null) { throw new ArgumentException("stepName arg is null"); }
        this.writeFunctionStep(stepName, null, n -> null);
    }

    @Override
    public final <TH extends Throwable> void writeRunnableStep(
        final String stepName,
        final ThrowingRunnable<? extends TH> runnable
    ) throws TH {
        if (stepName == null) { throw new ArgumentException("stepName arg is null"); }
        if (runnable == null) { throw new ArgumentException("runnable arg is null"); }
        this.writeFunctionStep(stepName, null, n -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public final <T, TH extends Throwable> void writeConsumerStep(
        final String stepName,
        final T input,
        final ThrowingConsumer<? super T, ? extends TH> consumer
    ) throws TH {
        if (stepName == null) { throw new ArgumentException("stepName arg is null"); }
        if (consumer == null) { throw new ArgumentException("consumer arg is null"); }
        this.writeFunctionStep(stepName, null, n -> {
            consumer.accept(input);
            return null;
        });
    }

    @Override
    public final <T, TH extends Throwable> T writeSupplierStep(
        String stepName,
        ThrowingSupplier<? extends T, ? extends TH> supplier
    ) throws TH {
        if (stepName == null) { throw new ArgumentException("stepName arg is null"); }
        if (supplier == null) { throw new ArgumentException("supplier arg is null"); }
        return this.writeFunctionStep(stepName, null, n -> supplier.get());
    }

    @Override
    public final <T, R, TH extends Throwable> R writeFunctionStep(
        final String stepName,
        final T input,
        final ThrowingFunction<? super T, ? extends R, ? extends TH> function
    ) throws TH {
        if (stepName == null) { throw new ArgumentException("stepName arg is null"); }
        if (function == null) { throw new ArgumentException("function arg is null"); }
        final String uuid = UUID.randomUUID().toString();
        try {
            this.listener.stepStarted(uuid, stepName);
        } catch (final Exception listenerEx) {
            throw new StepWriteException("Listener stepStarted method threw exception " + listenerEx, listenerEx);
        }
        final R result;
        try {
            result = function.apply(input);
        } catch (final Throwable stepEx) {
            if (this.cleanStackTrace) {
                cleanStackTrace(stepEx);
            }
            try {
                this.listener.stepFailed(uuid, stepName, stepEx);
            } catch (final Exception listenerEx) {
                final StepWriteException stepWritingEx = new StepWriteException(
                    "Listener stepFailed method threw exception " + listenerEx, listenerEx);
                stepWritingEx.addSuppressed(stepEx);
                throw stepWritingEx;
            }
            throw stepEx;
        }
        try {
            this.listener.stepPassed(uuid, stepName);
        } catch (final Exception listenerEx) {
            throw new StepWriteException("Listener stepPassed method threw exception " + listenerEx, listenerEx);
        }
        return result;
    }

    private static void cleanStackTrace(final Throwable mainTh) {
        final Set<Throwable> uniqueThrowables = new HashSet<>();
        addCauses(uniqueThrowables, mainTh);
        final String ignoredClassesStartWith = "com.plugatar.xteps";
        for (final Throwable th : uniqueThrowables) {
            if (!(th instanceof XtepsException)) {
                final StackTraceElement[] originStackTrace = th.getStackTrace();
                final StackTraceElement[] cleanStackTrace = Arrays.stream(originStackTrace)
                    .filter(element -> !element.getClassName().startsWith(ignoredClassesStartWith))
                    .toArray(StackTraceElement[]::new);
                if (cleanStackTrace.length != originStackTrace.length) {
                    th.setStackTrace(cleanStackTrace);
                }
            }
        }
    }

    private static void addCauses(final Set<Throwable> uniqueThrowables, Throwable mainTh) {
        for (Throwable currentTh = mainTh; currentTh != null; currentTh = currentTh.getCause()) {
            if (uniqueThrowables.contains(currentTh)) {
                break;
            }
            uniqueThrowables.add(currentTh);
            addSuppressed(uniqueThrowables, currentTh);
        }
    }

    private static void addSuppressed(final Set<Throwable> uniqueThrowables, Throwable mainTh) {
        for (final Throwable currentTh : mainTh.getSuppressed()) {
            if (uniqueThrowables.contains(currentTh)) {
                break;
            }
            uniqueThrowables.add(currentTh);
            addCauses(uniqueThrowables, currentTh);
        }
    }
}
