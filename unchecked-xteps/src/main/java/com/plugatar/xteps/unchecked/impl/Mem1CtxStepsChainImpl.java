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
package com.plugatar.xteps.unchecked.impl;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.OptionalValue;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.base.autocloseable.AutoCloseableOf;
import com.plugatar.xteps.unchecked.Mem1CtxStepsChain;
import com.plugatar.xteps.unchecked.Mem2CtxStepsChain;
import com.plugatar.xteps.unchecked.MemNoCtxStepsChain;
import com.plugatar.xteps.unchecked.base.BaseCtxStepsChain;

import static com.plugatar.xteps.unchecked.impl.StepsChainUtils.sneakyThrow;

/**
 * Memorizing contextual steps chain implementation.
 *
 * @param <C>  the context type
 * @param <PS> the previous context steps chain type
 */
public class Mem1CtxStepsChainImpl<C, P, PS extends BaseCtxStepsChain<?, ?>> implements Mem1CtxStepsChain<C, P, PS> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final SafeACContainer safeACContainer;
    private final OptionalValue<C> optionalContext;
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
    public Mem1CtxStepsChainImpl(final StepReporter stepReporter,
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
        this.optionalContext = OptionalValue.of(context);
        this.previousContext = previousContext;
        this.previousContextStepsChain = previousContextStepsChain;
    }

    @Override
    public final C context() {
        return this.optionalContext.value();
    }

    @Override
    public final PS previousContextStepsChain() {
        return this.previousContextStepsChain;
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> contextIsCloseable() {
        final C value = this.optionalContext.value();
        if (value instanceof AutoCloseable) {
            this.safeACContainer.add((AutoCloseable) value);
            return this;
        } else {
            final XtepsException baseEx = new XtepsException("The current context is not an AutoCloseable instance");
            this.safeACContainer.close(baseEx);
            this.exceptionHandler.handle(baseEx);
            throw baseEx;
        }
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> contextIsCloseable(final ThrowingConsumer<? super C, ?> close) {
        if (close == null) { this.throwNullArgException("close"); }
        this.safeACContainer.add(new AutoCloseableOf(() -> close.accept(this.optionalContext.value())));
        return this;
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> closeCloseableContexts() {
        try {
            this.safeACContainer.close();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return this;
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> supplyContext(
        final ThrowingConsumer<? super C, ?> consumer
    ) {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        try {
            consumer.accept(this.optionalContext.value());
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return this;
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> supplyContext(
        final ThrowingBiConsumer<? super C, ? super P, ?> consumer
    ) {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        try {
            consumer.accept(this.optionalContext.value(), this.previousContext);
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return this;
    }

    @Override
    public final <R> R applyContext(
        final ThrowingFunction<? super C, ? extends R, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        try {
            return function.apply(this.optionalContext.value());
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
    }

    @Override
    public final <R> R applyContext(
        final ThrowingBiFunction<? super C, ? super P, ? extends R, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        try {
            return function.apply(this.optionalContext.value(), this.previousContext);
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
    }

    @Override
    public final MemNoCtxStepsChain<Mem1CtxStepsChain<C, P, PS>> withoutContext() {
        return new MemNoCtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, this);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> withContext(final U context) {
        return new Mem2CtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, context,
            this.optionalContext.value(), this.previousContext, this);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> withContext(
        final ThrowingFunction<? super C, ? extends U, ?> contextFunction
    ) {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        final U newContext;
        try {
            newContext = contextFunction.apply(this.optionalContext.value());
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return new Mem2CtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, newContext,
            this.optionalContext.value(), this.previousContext, this);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> withContext(
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> contextFunction
    ) {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        final U newContext;
        try {
            newContext = contextFunction.apply(this.optionalContext.value(), this.previousContext);
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
        return new Mem2CtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, newContext,
            this.optionalContext.value(), this.previousContext, this);
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final ThrowingConsumer<? super C, ?> step
    ) {
        return this.supplyContext(step);
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final ThrowingBiConsumer<? super C, ? super P, ?> step
    ) {
        return this.supplyContext(step);
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final String stepName,
        final ThrowingConsumer<? super C, ?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final String stepName,
        final ThrowingBiConsumer<? super C, ? super P, ?> step
    ) {
        return this.step(stepName, "", step);
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<? super C, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.optionalContext.value());
            return null;
        });
        return this;
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingBiConsumer<? super C, ? super P, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.optionalContext.value(), this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        return this.withContext(step);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    ) {
        return this.withContext(step);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        final String stepName,
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    ) {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        final U newContext = this.reportStep(stepName, stepDescription, () ->
            step.apply(this.optionalContext.value()));
        return new Mem2CtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, newContext,
            this.optionalContext.value(), this.previousContext, this);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P, Mem1CtxStepsChain<C, P, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        final U newContext = this.reportStep(stepName, stepDescription, () ->
            step.apply(this.optionalContext.value(), this.previousContext));
        return new Mem2CtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, newContext,
            this.optionalContext.value(), this.previousContext, this);
    }

    @Override
    public final <R> R stepTo(
        final ThrowingFunction<? super C, ? extends R, ?> step
    ) {
        return this.applyContext(step);
    }

    @Override
    public final <R> R stepTo(
        final ThrowingBiFunction<? super C, ? super P, ? extends R, ?> step
    ) {
        return this.applyContext(step);
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
        final ThrowingFunction<? super C, ? extends R, ?> step
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription, () -> step.apply(this.optionalContext.value()));
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
        return this.reportStep(stepName, stepDescription, () ->
            step.apply(this.optionalContext.value(), this.previousContext));
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> nestedSteps(
        final String stepName,
        final ThrowingConsumer<Mem1CtxStepsChain<C, P, PS>, ?> stepsChain
    ) {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<Mem1CtxStepsChain<C, P, PS>, ?> stepsChain
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
        final ThrowingFunction<Mem1CtxStepsChain<C, P, PS>, ? extends R, ?> stepsChain
    ) {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<Mem1CtxStepsChain<C, P, PS>, ? extends R, ?> stepsChain
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final Mem1CtxStepsChain<C, P, PS> branchSteps(
        final ThrowingConsumer<Mem1CtxStepsChain<C, P, PS>, ?> stepsChain
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
        return this.stepReporter.report(this.safeACContainer, this.exceptionHandler, stepName, stepDescription,
            this.optionalContext, ThrowingSupplier.unchecked(step));
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.safeACContainer.close(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
