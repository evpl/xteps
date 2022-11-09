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
package com.plugatar.xteps.unchecked.chain.impl;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.RunnableStep;
import com.plugatar.xteps.unchecked.SupplierStep;
import com.plugatar.xteps.unchecked.chain.CtxStepsChain;
import com.plugatar.xteps.unchecked.chain.MemNoCtxStepsChain;
import com.plugatar.xteps.unchecked.chain.base.BaseCtxStepsChain;

import static com.plugatar.xteps.unchecked.chain.impl.StepsChainUtils.sneakyThrow;

/**
 * Memorizing no context steps chain implementation.
 *
 * @param <P> the previous context steps chain type
 */
public class MemNoCtxStepsChainImpl<P extends BaseCtxStepsChain<?, ?>> implements MemNoCtxStepsChain<P> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final SafeACContainer safeACContainer;
    private final P previousContextStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter              the step reporter
     * @param exceptionHandler          the exception handler
     * @param safeACContainer           the safe AutoCloseable container
     * @param previousContextStepsChain the previous context steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code safeACContainer} or {@code previousContextStepsChain} is null
     */
    public MemNoCtxStepsChainImpl(final StepReporter stepReporter,
                                  final ExceptionHandler exceptionHandler,
                                  final SafeACContainer safeACContainer,
                                  final P previousContextStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (safeACContainer == null) { throw new NullPointerException("safeACContainer arg is null"); }
        if (previousContextStepsChain == null) {
            throw new NullPointerException("previousContextStepsChain arg is null");
        }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.safeACContainer = safeACContainer;
        this.previousContextStepsChain = previousContextStepsChain;
    }

    @Override
    public final P previousContextStepsChain() {
        return this.previousContextStepsChain;
    }

    @Override
    public final MemNoCtxStepsChain<P> closeCloseableContexts() {
        try {
            this.safeACContainer.close();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return this;
    }

    @Override
    public final <U> CtxStepsChain<U> withContext(final U context) {
        return new CtxStepsChainImpl<>(
            this.stepReporter, this.exceptionHandler, this.safeACContainer, context
        );
    }

    @Override
    public final <U> CtxStepsChain<U> withContext(
        final ThrowingSupplier<? extends U, ?> contextSupplier
    ) {
        if (contextSupplier == null) { this.throwNullArgException("contextSupplier"); }
        final U context;
        try {
            context = contextSupplier.get();
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return new CtxStepsChainImpl<>(
            this.stepReporter, this.exceptionHandler, this.safeACContainer, context
        );
    }

    @Override
    public final MemNoCtxStepsChain<P> step(
        final RunnableStep step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        try {
            step.run();
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return this;
    }

    @Override
    public final MemNoCtxStepsChainImpl<P> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final MemNoCtxStepsChainImpl<P> step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final MemNoCtxStepsChainImpl<P> step(
        final String stepName,
        final ThrowingRunnable<?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final MemNoCtxStepsChainImpl<P> step(
        final String stepName,
        final String stepDescription,
        final ThrowingRunnable<?> step
    ) {
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
    public final <U> CtxStepsChain<U> stepToContext(
        final SupplierStep<? extends U> step
    ) {
        return this.withContext(step);
    }

    @Override
    public final <U> CtxStepsChain<U> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> CtxStepsChain<U> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        final U context = this.reportStep(stepName, stepDescription, step);
        return new CtxStepsChainImpl<>(
            this.stepReporter, this.exceptionHandler, this.safeACContainer, context
        );
    }

    @Override
    public final <R> R stepTo(
        final SupplierStep<? extends R> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        try {
            return step.get();
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
    }

    @Override
    public final <R> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ?> step
    ) {
        return this.stepTo(stepName, "", step);
    }

    @Override
    public final <R> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends R, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription, step);
    }

    @Override
    public final MemNoCtxStepsChain<P> nestedSteps(
        final String stepName,
        final ThrowingConsumer<MemNoCtxStepsChain<P>, ?> stepsChain
    ) {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final MemNoCtxStepsChain<P> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<MemNoCtxStepsChain<P>, ?> stepsChain
    ) {
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
    public final <R> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<MemNoCtxStepsChain<P>, ? extends R, ?> stepsChain
    ) {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<MemNoCtxStepsChain<P>, ? extends R, ?> stepsChain
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final MemNoCtxStepsChain<P> branchSteps(
        final ThrowingConsumer<MemNoCtxStepsChain<P>, ?> stepsChain
    ) {
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        try {
            stepsChain.accept(this);
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return this;
    }

    private <R> R reportStep(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends R, ?> step
    ) {
        return this.stepReporter.report(this.exceptionHandler, stepName, stepDescription, new Object[]{},
            ThrowingSupplier.unchecked(step));
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.safeACContainer.close(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
