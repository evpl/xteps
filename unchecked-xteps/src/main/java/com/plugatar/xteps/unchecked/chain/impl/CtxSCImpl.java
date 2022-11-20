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
import com.plugatar.xteps.base.HookContainer;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.chain.CtxSC;
import com.plugatar.xteps.unchecked.chain.Mem2CtxSC;
import com.plugatar.xteps.unchecked.chain.MemNoCtxSC;
import com.plugatar.xteps.unchecked.stepobject.ConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.FunctionStep;
import com.plugatar.xteps.unchecked.stepobject.RunnableStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;

import static com.plugatar.xteps.unchecked.chain.impl.StepsChainUtils.sneakyThrow;

/**
 * Contextual steps chain implementation.
 *
 * @param <C> the context type
 */
public class CtxSCImpl<C> implements CtxSC<C> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final HookContainer hookContainer;
    private final C context;

    /**
     * Ctor.
     *
     * @param stepReporter     the step reporter
     * @param exceptionHandler the exception handler
     * @param hookContainer    the hook container
     * @param context          the context
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code hookContainer} is null
     */
    public CtxSCImpl(final StepReporter stepReporter,
                     final ExceptionHandler exceptionHandler,
                     final HookContainer hookContainer,
                     final C context) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (hookContainer == null) { throw new NullPointerException("hookContainer arg is null"); }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.hookContainer = hookContainer;
        this.context = context;
    }

    @Override
    public final CtxSC<C> callHooks() {
        try {
            this.hookContainer.callHooks();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final CtxSC<C> hook(final ThrowingRunnable<?> hook) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(hook);
        return this;
    }

    @Override
    public final CtxSC<C> hook(final ThrowingConsumer<C, ?> hook) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context));
        return this;
    }

    @Override
    public final C context() {
        return this.context;
    }

    @Override
    public final MemNoCtxSC<CtxSC<C>> withoutContext() {
        return new MemNoCtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.hookContainer, this);
    }

    @Override
    public final CtxSC<C> supplyContext(
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
    public final <R> R applyContext(
        final ThrowingFunction<? super C, ? extends R, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> withContext(final U context) {
        return newMem1CtxStepsChain(context);
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> withContext(
        final ThrowingSupplier<? extends U, ?> contextSupplier
    ) {
        if (contextSupplier == null) { this.throwNullArgException("contextSupplier"); }
        return newMem1CtxStepsChain(this.execAction(contextSupplier));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> withContext(
        final ThrowingFunction<? super C, ? extends U, ?> contextFunction
    ) {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return newMem1CtxStepsChain(this.execAction(() -> contextFunction.apply(this.context)));
    }

    @Override
    public final CtxSC<C> step(
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
    public final CtxSC<C> step(
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
    public final CtxSC<C> step(
        final String keyword,
        final RunnableStep step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).run();
            return null;
        });
        return this;
    }

    @Override
    public final CtxSC<C> step(
        final String keyword,
        final ConsumerStep<? super C> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final CtxSC<C> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final CtxSC<C> step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final CtxSC<C> step(
        final String stepName,
        final ThrowingRunnable<?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final CtxSC<C> step(
        final String stepName,
        final ThrowingConsumer<? super C, ?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final CtxSC<C> step(
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
    public final CtxSC<C> step(
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
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final SupplierStep<? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final FunctionStep<? super C, ? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final String keyword,
        final SupplierStep<? extends U> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(step.withKeyword(keyword)));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final String keyword,
        final FunctionStep<? super C, ? extends U> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(() -> step.withKeyword(keyword).apply(this.context)));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.reportStep(stepName, stepDescription, step));
    }

    @Override
    public final <U> Mem2CtxSC<U, C, CtxSC<C>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.reportStep(stepName, stepDescription, () -> step.apply(this.context)));
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
        final String keyword,
        final SupplierStep<? extends R> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step.withKeyword(keyword));
    }

    @Override
    public final <R> R stepTo(
        final String keyword,
        final FunctionStep<? super C, ? extends R> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withKeyword(keyword).apply(this.context));
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
    public final CtxSC<C> nestedSteps(
        final String stepName,
        final ThrowingConsumer<CtxSC<C>, ?> stepsChain
    ) {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final CtxSC<C> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<CtxSC<C>, ?> stepsChain
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
        final ThrowingFunction<CtxSC<C>, ? extends R, ?> stepsChain
    ) {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<CtxSC<C>, ? extends R, ?> stepsChain
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final CtxSC<C> branchSteps(
        final ThrowingConsumer<CtxSC<C>, ?> stepsChain
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
        final ThrowingSupplier<R, ?> step
    ) {
        return this.stepReporter.report(this.hookContainer, this.exceptionHandler, stepName, stepDescription,
            new Object[]{this.context}, ThrowingSupplier.unchecked(step));
    }

    private <R> R execAction(
        final ThrowingSupplier<R, ?> action
    ) {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.hookContainer.callHooks(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
    }

    private <U> Mem2CtxSC<U, C, CtxSC<C>> newMem1CtxStepsChain(final U newContext) {
        return new Mem2CtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.hookContainer, newContext,
            this.context, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.hookContainer.callHooks(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
