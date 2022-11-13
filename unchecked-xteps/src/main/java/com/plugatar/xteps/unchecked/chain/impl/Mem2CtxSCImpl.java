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
import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.base.autocloseable.AutoCloseableOf;
import com.plugatar.xteps.unchecked.chain.Mem2CtxSC;
import com.plugatar.xteps.unchecked.chain.Mem3CtxSC;
import com.plugatar.xteps.unchecked.chain.MemNoCtxSC;
import com.plugatar.xteps.unchecked.chain.base.BaseCtxSC;
import com.plugatar.xteps.unchecked.stepobject.BiConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.BiFunctionStep;
import com.plugatar.xteps.unchecked.stepobject.ConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.FunctionStep;
import com.plugatar.xteps.unchecked.stepobject.RunnableStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;

import static com.plugatar.xteps.unchecked.chain.impl.StepsChainUtils.sneakyThrow;

/**
 * Memorizing contextual steps chain implementation.
 *
 * @param <C>  the context type
 * @param <P>  the previous context type
 * @param <PS> the previous context steps chain type
 */
public class Mem2CtxSCImpl<C, P, PS extends BaseCtxSC<?, ?>> implements Mem2CtxSC<C, P, PS> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final SafeACContainer safeACContainer;
    private final C context;
    private final P previousContext;
    private final PS previousContextStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter              the step reporter
     * @param exceptionHandler          the exception handler
     * @param safeACContainer           the safe AutoCloseable container
     * @param context                   the context
     * @param previousContext           the previous context
     * @param previousContextStepsChain the previous context steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code safeACContainer} or {@code previousContextStepsChain} is null
     */
    public Mem2CtxSCImpl(final StepReporter stepReporter,
                         final ExceptionHandler exceptionHandler,
                         final SafeACContainer safeACContainer,
                         final C context,
                         final P previousContext,
                         final PS previousContextStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (safeACContainer == null) { throw new NullPointerException("safeACContainer arg is null"); }
        if (previousContextStepsChain == null) {
            throw new NullPointerException("previousContextStepsChain arg is null");
        }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.safeACContainer = safeACContainer;
        this.context = context;
        this.previousContext = previousContext;
        this.previousContextStepsChain = previousContextStepsChain;
    }

    @Override
    public final C context() {
        return this.context;
    }

    @Override
    public final PS previousContextStepsChain() {
        return this.previousContextStepsChain;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> contextIsCloseable() {
        if (this.context instanceof AutoCloseable) {
            this.safeACContainer.add((AutoCloseable) this.context);
            return this;
        } else {
            final XtepsException baseEx = new XtepsException("The current context is not an AutoCloseable instance");
            this.safeACContainer.close(baseEx);
            this.exceptionHandler.handle(baseEx);
            throw baseEx;
        }
    }

    @Override
    public final Mem2CtxSC<C, P, PS> contextIsCloseable(final ThrowingConsumer<? super C, ?> close) {
        if (close == null) { this.throwNullArgException("close"); }
        this.safeACContainer.add(new AutoCloseableOf(() -> close.accept(this.context)));
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> closeCloseableContexts() {
        try {
            this.safeACContainer.close();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> supplyContext(
        final ThrowingConsumer<? super C, ?> consumer
    ) {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> supplyContext(
        final ThrowingBiConsumer<? super C, ? super P, ?> consumer
    ) {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final <R> R applyContext(
        final ThrowingFunction<? super C, ? extends R, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context));
    }

    @Override
    public final <R> R applyContext(
        final ThrowingBiFunction<? super C, ? super P, ? extends R, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context, this.previousContext));
    }

    @Override
    public final MemNoCtxSC<Mem2CtxSC<C, P, PS>> withoutContext() {
        return new MemNoCtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, this);
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(final U context) {
        return this.newMem2CtxStepsChain(context);
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(
        final ThrowingSupplier<? extends U, ?> contextSupplier
    ) {
        if (contextSupplier == null) { this.throwNullArgException("contextSupplier"); }
        return this.newMem2CtxStepsChain(this.execAction(contextSupplier));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(
        final ThrowingFunction<? super C, ? extends U, ?> contextFunction
    ) {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> contextFunction.apply(this.context)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> withContext(
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> contextFunction
    ) {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(
            this.execAction(() -> contextFunction.apply(this.context, this.previousContext)));
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final RunnableStep step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.run();
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final ConsumerStep<? super C> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final BiConsumerStep<? super C, ? super P> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepNamePrefix,
        final RunnableStep step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).run();
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepNamePrefix,
        final ConsumerStep<? super C> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepNamePrefix,
        final BiConsumerStep<? super C, ? super P> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepName,
        final ThrowingRunnable<?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepName,
        final ThrowingConsumer<? super C, ?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepName,
        final ThrowingBiConsumer<? super C, ? super P, ?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
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
    public final Mem2CtxSC<C, P, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<? super C, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxSC<C, P, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingBiConsumer<? super C, ? super P, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final SupplierStep<? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final FunctionStep<? super C, ? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final BiFunctionStep<? super C, ? super P, ? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context, this.previousContext)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepNamePrefix,
        final SupplierStep<? extends U> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step.withNamePrefix(stepNamePrefix)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepNamePrefix,
        final FunctionStep<? super C, ? extends U> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withNamePrefix(stepNamePrefix).apply(this.context)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepNamePrefix,
        final BiFunctionStep<? super C, ? super P, ? extends U> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withNamePrefix(stepNamePrefix).apply(this.context, this.previousContext)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepName,
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription, step));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription, () -> step.apply(this.context)));
    }

    @Override
    public final <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.previousContext)));
    }

    @Override
    public final <R> R stepTo(
        final SupplierStep<? extends R> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step);
    }

    @Override
    public final <R> R stepTo(
        final FunctionStep<? super C, ? extends R> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context));
    }

    @Override
    public final <R> R stepTo(
        final BiFunctionStep<? super C, ? super P, ? extends R> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.previousContext));
    }

    @Override
    public final <R> R stepTo(
        final String stepNamePrefix,
        final SupplierStep<? extends R> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step.withNamePrefix(stepNamePrefix));
    }

    @Override
    public final <R> R stepTo(
        final String stepNamePrefix,
        final FunctionStep<? super C, ? extends R> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withNamePrefix(stepNamePrefix).apply(this.context));
    }

    @Override
    public final <R> R stepTo(
        final String stepNamePrefix,
        final BiFunctionStep<? super C, ? super P, ? extends R> step
    ) {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withNamePrefix(stepNamePrefix).apply(this.context, this.previousContext));
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
        final ThrowingFunction<? super C, ? extends R, ?> step
    ) {
        return this.stepTo(stepName, "", step);
    }

    @Override
    public final <R> R stepTo(
        final String stepName,
        final ThrowingBiFunction<? super C, ? super P, ? extends R, ?> step
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
    public final <R> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<? super C, ? extends R, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription, () -> step.apply(this.context));
    }

    @Override
    public final <R> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingBiFunction<? super C, ? super P, ? extends R, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription, () -> step.apply(this.context, this.previousContext));
    }

    @Override
    public final Mem2CtxSC<C, P, PS> nestedSteps(
        final String stepName,
        final ThrowingConsumer<Mem2CtxSC<C, P, PS>, ?> stepsChain
    ) {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final Mem2CtxSC<C, P, PS> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<Mem2CtxSC<C, P, PS>, ?> stepsChain
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
        final ThrowingFunction<Mem2CtxSC<C, P, PS>, ? extends R, ?> stepsChain
    ) {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<Mem2CtxSC<C, P, PS>, ? extends R, ?> stepsChain
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final Mem2CtxSC<C, P, PS> branchSteps(
        final ThrowingConsumer<Mem2CtxSC<C, P, PS>, ?> stepsChain
    ) {
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        this.execAction(() -> {
            stepsChain.accept(this);
            return null;
        });
        return this;
    }

    private <R> R reportStep(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends R, ?> step
    ) {
        return this.stepReporter.report(this.safeACContainer, this.exceptionHandler, stepName, stepDescription,
            new Object[]{this.context, this.previousContext}, ThrowingSupplier.unchecked(step));
    }

    private <R> R execAction(
        final ThrowingSupplier<R, ?> action
    ) {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
    }

    private <U> Mem3CtxSC<U, C, P, Mem2CtxSC<C, P, PS>> newMem2CtxStepsChain(final U newContext) {
        return new Mem3CtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, newContext,
            this.context, this.previousContext, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.safeACContainer.close(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
