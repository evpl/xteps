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
package com.plugatar.xteps.core.chain;

import com.plugatar.xteps.core.CtxStepsChain;
import com.plugatar.xteps.core.InitialStepsChain;
import com.plugatar.xteps.core.StepReporter;
import com.plugatar.xteps.core.XtepsException;
import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;

public class InitialStepsChainImpl implements InitialStepsChain {
    private final StepReporter stepReporter;

    /**
     * Ctor.
     *
     * @param stepReporter the step reporter
     */
    public InitialStepsChainImpl(final StepReporter stepReporter) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        this.stepReporter = stepReporter;
    }

    private void throwNullArgException(final String methodName,
                                       final String argName) {
        final String message = "InitialStepsChain " + methodName + " method " + argName + " arg is null";
        this.stepReporter.reportFailedStep(message, new XtepsException(message));
    }

    private <R> R throwExecutionException(final String methodName,
                                          final Throwable throwable) {
        final String message = "InitialStepsChain " + methodName + " method throws exception";
        this.stepReporter.reportFailedStep(
            message,
            new XtepsException(message + " " + throwable, throwable)
        );
        return null;
    }

    @Override
    public final <U> CtxStepsChain<U, InitialStepsChain> withContext(final U context) {
        return new CtxStepsChainImpl<>(this.stepReporter, context, this);
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, InitialStepsChain> withContext(
        final ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E {
        if (contextSupplier == null) { throwNullArgException("withContext", "contextSupplier"); }
        final U newContext;
        try {
            newContext = contextSupplier.get();
        } catch (final Throwable throwable) {
            return throwExecutionException("withContext", throwable);
        }
        return new CtxStepsChainImpl<>(this.stepReporter, newContext, this);
    }

    @Override
    public final <E extends Throwable> void failedStep(final String stepName,
                                                       final E exception) throws E {
        if (stepName == null) { throwNullArgException("failedStep", "stepName"); }
        if (exception == null) { throwNullArgException("failedStep", "exception"); }
        this.stepReporter.reportFailedStep(stepName, exception);
    }

    @Override
    public final InitialStepsChain step(final String stepName) {
        if (stepName == null) { throwNullArgException("step", "stepName"); }
        this.stepReporter.reportEmptyStep(stepName);
        return this;
    }

    @Override
    public final <E extends Throwable> InitialStepsChain step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        if (stepName == null) { throwNullArgException("step", "stepName"); }
        if (step == null) { throwNullArgException("step", "step"); }
        this.stepReporter.reportRunnableStep(stepName, step);
        return this;
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, InitialStepsChain> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { throwNullArgException("stepToContext", "stepName"); }
        if (step == null) { throwNullArgException("stepToContext", "step"); }
        return new CtxStepsChainImpl<>(
            this.stepReporter,
            this.stepReporter.reportSupplierStep(stepName, step),
            this
        );
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { throwNullArgException("stepTo", "stepName"); }
        if (step == null) { throwNullArgException("stepTo", "step"); }
        return this.stepReporter.reportSupplierStep(stepName, step);
    }

    @Override
    public final <E extends Throwable> InitialStepsChain nestedSteps(
        final String stepName,
        final ThrowingConsumer<InitialStepsChain, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { throwNullArgException("nestedSteps", "stepName"); }
        if (stepsChain == null) { throwNullArgException("nestedSteps", "stepsChain"); }
        this.stepReporter.reportConsumerStep(stepName, this, stepsChain);
        return this;
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<InitialStepsChain, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { throwNullArgException("nestedStepsTo", "stepName"); }
        if (stepsChain == null) { throwNullArgException("nestedStepsTo", "stepsChain"); }
        return this.stepReporter.reportFunctionStep(stepName, this, stepsChain);
    }
}
