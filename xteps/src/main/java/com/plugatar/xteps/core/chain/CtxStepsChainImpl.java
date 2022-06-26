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

public class CtxStepsChainImpl<C, P extends BaseStepsChain<?>> implements CtxStepsChain<C, P> {
    private final StepReporter stepReporter;
    private final C context;
    private final P previousStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter       the step reporter
     * @param context            the context
     * @param previousStepsChain the previous steps chain
     */
    public CtxStepsChainImpl(final StepReporter stepReporter,
                             final C context,
                             final P previousStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (previousStepsChain == null) { throw new NullPointerException("previousStepsChain arg is null"); }
        this.stepReporter = stepReporter;
        this.context = context;
        this.previousStepsChain = previousStepsChain;
    }

    private void throwNullArgException(final String methodName,
                                       final String argName) {
        final String message = "CtxStepsChain " + methodName + " method " + argName + " arg is null";
        this.stepReporter.reportFailedStep(message, new XtepsException(message));
    }

    private <R> R throwExecutionException(final String methodName,
                                          final Throwable throwable) {
        final String message = "CtxStepsChain " + methodName + " method throws exception";
        this.stepReporter.reportFailedStep(
            message,
            new XtepsException(message + " " + throwable, throwable)
        );
        return null;
    }

    @Override
    public final P previousStepsChain() {
        return this.previousStepsChain;
    }

    @Override
    public final C context() {
        return this.context;
    }

    @Override
    public final <E extends Throwable> CtxStepsChain<C, P> supplyContextTo(
        final ThrowingConsumer<? super C, ? extends E> consumer
    ) throws E {
        if (consumer == null) { throwNullArgException("supplyContextTo", "consumer"); }
        try {
            consumer.accept(this.context);
        } catch (final Throwable throwable) {
            throwExecutionException("supplyContextTo", throwable);
        }
        return this;
    }

    @Override
    public final <R, E extends Throwable> R applyContextTo(
        final ThrowingFunction<? super C, ? extends R, ? extends E> function
    ) throws E {
        if (function == null) { throwNullArgException("applyContextTo", "function"); }
        try {
            return function.apply(this.context);
        } catch (final Throwable throwable) {
            return throwExecutionException("applyContextTo", throwable);
        }
    }

    @Override
    public final <U> CtxStepsChain<U, CtxStepsChain<C, P>> withContext(final U context) {
        return new CtxStepsChainImpl<>(this.stepReporter, context, this);
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, CtxStepsChain<C, P>> withContext(
        final ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { throwNullArgException("withContext", "contextFunction"); }
        final U newContext;
        try {
            newContext = contextFunction.apply(this.context);
        } catch (final Throwable throwable) {
            return throwExecutionException("withContext", throwable);
        }
        return new CtxStepsChainImpl<>(this.stepReporter, newContext, this);
    }

    @Override
    public final NoCtxStepsChain<CtxStepsChain<C, P>> withoutContext() {
        return new NoCtxStepsChainImpl<>(this.stepReporter, this);
    }

    @Override
    public final CtxStepsChain<C, P> step(final String stepName) {
        if (stepName == null) { throwNullArgException("step", "stepName"); }
        this.stepReporter.reportEmptyStep(stepName);
        return this;
    }

    @Override
    public final <E extends Throwable> CtxStepsChain<C, P> step(
        final String stepName,
        final ThrowingConsumer<? super C, ? extends E> step
    ) throws E {
        if (stepName == null) { throwNullArgException("step", "stepName"); }
        if (step == null) { throwNullArgException("step", "step"); }
        this.stepReporter.reportConsumerStep(stepName, this.context, step);
        return this;
    }

    @Override
    public final <U, E extends Throwable> CtxStepsChain<U, CtxStepsChain<C, P>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { throwNullArgException("stepToContext", "stepName"); }
        if (step == null) { throwNullArgException("stepToContext", "step"); }
        return new CtxStepsChainImpl<>(
            this.stepReporter,
            this.stepReporter.reportFunctionStep(stepName, this.context, step),
            this
        );
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingFunction<? super C, ? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { throwNullArgException("stepTo", "stepName"); }
        if (step == null) { throwNullArgException("stepTo", "step"); }
        return this.stepReporter.reportFunctionStep(stepName, this.context, step);
    }

    @Override
    public final <E extends Throwable> CtxStepsChain<C, P> nestedSteps(
        final String stepName,
        final ThrowingConsumer<CtxStepsChain<C, P>, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { throwNullArgException("nestedSteps", "stepName"); }
        if (stepsChain == null) { throwNullArgException("nestedSteps", "stepsChain"); }
        this.stepReporter.reportConsumerStep(stepName, this, stepsChain);
        return this;
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<CtxStepsChain<C, P>, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { throwNullArgException("nestedStepsTo", "stepName"); }
        if (stepsChain == null) { throwNullArgException("nestedStepsTo", "stepsChain"); }
        return this.stepReporter.reportFunctionStep(stepName, this, stepsChain);
    }
}
