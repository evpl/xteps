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

import com.plugatar.xteps.core.BaseStepsChain;
import com.plugatar.xteps.core.CtxStepsChain;
import com.plugatar.xteps.core.NoCtxStepsChain;
import com.plugatar.xteps.core.StepReporter;
import com.plugatar.xteps.core.XtepsException;
import com.plugatar.xteps.util.function.ThrowingConsumer;
import com.plugatar.xteps.util.function.ThrowingFunction;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;

import java.util.Deque;

import static com.plugatar.xteps.core.chain.StepsChainUtils.closeAllAutoCloseables;
import static com.plugatar.xteps.core.chain.StepsChainUtils.closeAllAutoCloseablesAndSneakyRethrow;

/**
 * No context steps chain implementation.
 *
 * @param <P> the previous steps chain type
 */
public class NoCtxStepsChainImpl<P extends BaseStepsChain<?>> implements NoCtxStepsChain<P> {
    private final StepReporter stepReporter;
    private final P previousStepsChain;
    private final Deque<AutoCloseable> acContextsDeque;

    /**
     * Ctor.
     *
     * @param stepReporter       the step reporter
     * @param previousStepsChain the previous steps chain
     * @param acContextsDeque    the {@code AutoCloseable} contexts deque
     */
    public NoCtxStepsChainImpl(final StepReporter stepReporter,
                               final P previousStepsChain,
                               final Deque<AutoCloseable> acContextsDeque) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (previousStepsChain == null) { throw new NullPointerException("previousStepsChain arg is null"); }
        if (acContextsDeque == null) { throw new NullPointerException("acContextsDeque arg is null"); }
        this.stepReporter = stepReporter;
        this.previousStepsChain = previousStepsChain;
        this.acContextsDeque = acContextsDeque;
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }

    @Override
    public final P previousStepsChain() {
        return this.previousStepsChain;
    }

    @Override
    public final <U> CtxStepsChain<U, NoCtxStepsChain<P>> withContext(final U context) {
        try {
            return new CtxStepsChainImpl<>(this.stepReporter, context, this, this.acContextsDeque);
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, NoCtxStepsChain<P>> withContext(
        final ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E {
        try {
            if (contextSupplier == null) { throwNullArgException("contextSupplier"); }
            return new CtxStepsChainImpl<>(this.stepReporter, contextSupplier.get(), this, this.acContextsDeque);
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }

    @Override
    public final NoCtxStepsChain<P> closeAutoCloseableContexts() {
        closeAllAutoCloseables(this.acContextsDeque);
        return this;
    }

    @Override
    public final NoCtxStepsChain<P> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final NoCtxStepsChain<P> step(
        final String stepName,
        final String stepDescription
    ) {
        try {
            if (stepName == null) { throwNullArgException("stepName"); }
            if (stepDescription == null) { throwNullArgException("stepDescription"); }
            this.stepReporter.reportEmptyStep(stepName, stepDescription);
            return this;
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }

    @Override
    public final <E extends Throwable> NoCtxStepsChain<P> step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> NoCtxStepsChain<P> step(
        final String stepName,
        final String stepDescription,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        try {
            if (stepName == null) { throwNullArgException("stepName"); }
            if (stepDescription == null) { throwNullArgException("stepDescription"); }
            if (step == null) { throwNullArgException("step"); }
            this.stepReporter.reportRunnableStep(stepName, stepDescription, step);
            return this;
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, NoCtxStepsChain<P>> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, NoCtxStepsChain<P>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        try {
            if (stepName == null) { throwNullArgException("stepName"); }
            if (stepDescription == null) { throwNullArgException("stepDescription"); }
            if (step == null) { throwNullArgException("step"); }
            return new CtxStepsChainImpl<>(
                this.stepReporter,
                this.stepReporter.reportSupplierStep(stepName, stepDescription, step),
                this,
                this.acContextsDeque
            );
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
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
        try {
            if (stepName == null) { throwNullArgException("stepName"); }
            if (stepDescription == null) { throwNullArgException("stepDescription"); }
            if (step == null) { throwNullArgException("step"); }
            return this.stepReporter.reportSupplierStep(stepName, stepDescription, step);
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }

    @Override
    public final <E extends Throwable> NoCtxStepsChain<P> nestedSteps(
        final String stepName,
        final ThrowingConsumer<NoCtxStepsChain<P>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> NoCtxStepsChain<P> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<NoCtxStepsChain<P>, ? extends E> stepsChain
    ) throws E {
        try {
            if (stepName == null) { throwNullArgException("stepName"); }
            if (stepDescription == null) { throwNullArgException("stepDescription"); }
            if (stepsChain == null) { throwNullArgException("stepsChain"); }
            this.stepReporter.reportConsumerStep(stepName, stepDescription, this, stepsChain);
            return this;
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<NoCtxStepsChain<P>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<NoCtxStepsChain<P>, ? extends R, ? extends E> stepsChain
    ) throws E {
        try {
            if (stepName == null) { throwNullArgException("stepName"); }
            if (stepDescription == null) { throwNullArgException("stepDescription"); }
            if (stepsChain == null) { throwNullArgException("stepsChain"); }
            return this.stepReporter.reportFunctionStep(stepName, stepDescription, this, stepsChain);
        } catch (final Throwable ex) {
            throw closeAllAutoCloseablesAndSneakyRethrow(this.acContextsDeque, ex);
        }
    }
}
