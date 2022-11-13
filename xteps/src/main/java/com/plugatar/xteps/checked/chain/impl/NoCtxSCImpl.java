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
package com.plugatar.xteps.checked.chain.impl;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.chain.CtxSC;
import com.plugatar.xteps.checked.chain.NoCtxSC;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * No context steps chain implementation. Entry point for other steps chains.
 */
public class NoCtxSCImpl implements NoCtxSC {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final ThrowingSupplier<? extends SafeACContainer, ? extends RuntimeException> safeACContainerGenerator;

    /**
     * Ctor.
     *
     * @param stepReporter             the step reporter
     * @param exceptionHandler         the exception handler
     * @param safeACContainerGenerator the new chain safe AutoCloseable container generator
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code safeACContainerGenerator} is null
     */
    public NoCtxSCImpl(
        final StepReporter stepReporter,
        final ExceptionHandler exceptionHandler,
        final ThrowingSupplier<? extends SafeACContainer, ? extends RuntimeException> safeACContainerGenerator
    ) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (safeACContainerGenerator == null) {
            throw new NullPointerException("safeACContainerGenerator arg is null");
        }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.safeACContainerGenerator = safeACContainerGenerator;
    }

    @Override
    public final <U> CtxSC<U> withContext(final U context) {
        return this.newCtxStepsChain(context);
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U> withContext(
        final ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E {
        if (contextSupplier == null) { this.throwNullArgException("contextSupplier"); }
        return this.newCtxStepsChain(this.execAction(contextSupplier));
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
        final RunnableStep<? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.run();
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
        final String stepNamePrefix,
        final RunnableStep<? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).run();
            return null;
        });
        return this;
    }

    @Override
    public final NoCtxSC step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final NoCtxSC step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
        final String stepName,
        final String stepDescription,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.run();
            return null;
        });
        return this;
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U> stepToContext(
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newCtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U> stepToContext(
        final String stepNamePrefix,
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newCtxStepsChain(this.execAction(step.withNamePrefix(stepNamePrefix)));
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newCtxStepsChain(this.reportStep(stepName, stepDescription, step));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final SupplierStep<? extends R, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepNamePrefix,
        final SupplierStep<? extends R, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step.withNamePrefix(stepNamePrefix));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        return this.stepTo(stepName, "", step);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription, step);
    }

    @Override
    public final <E extends Throwable> NoCtxSC nestedSteps(
        final String stepName,
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> NoCtxSC nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        this.reportStep(stepName, stepDescription, () -> {
            stepsChain.accept(this);
            return null;
        });
        return this;
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<NoCtxSC, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<NoCtxSC, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final <E extends Throwable> NoCtxSC branchSteps(
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
    ) throws E {
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        this.execAction(() -> {
            stepsChain.accept(this);
            return null;
        });
        return this;
    }

    private <R, E extends Throwable> R reportStep(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<R, E> step
    ) throws E {
        return this.stepReporter.report(this.exceptionHandler, stepName, stepDescription, new Object[]{}, step);
    }

    private <R, E extends Throwable> R execAction(
        final ThrowingSupplier<R, E> action
    ) throws E {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
    }

    private <U> CtxSC<U> newCtxStepsChain(final U newContext) {
        return new CtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainerGenerator.get(),
            newContext);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}