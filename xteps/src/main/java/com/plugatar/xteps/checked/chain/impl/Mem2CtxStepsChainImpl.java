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
import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.base.autocloseable.AutoCloseableOf;
import com.plugatar.xteps.checked.chain.Mem2CtxStepsChain;
import com.plugatar.xteps.checked.chain.MemNoCtxStepsChain;
import com.plugatar.xteps.checked.chain.base.BaseCtxStepsChain;
import com.plugatar.xteps.checked.stepobject.BiConsumerStep;
import com.plugatar.xteps.checked.stepobject.BiFunctionStep;
import com.plugatar.xteps.checked.stepobject.ConsumerStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;
import com.plugatar.xteps.checked.stepobject.TriConsumerStep;
import com.plugatar.xteps.checked.stepobject.TriFunctionStep;

/**
 * Memorizing contextual steps chain implementation.
 *
 * @param <C>  the context type
 * @param <P1> the first previous context type
 * @param <P2> the second previous context type
 * @param <PS> the previous context steps chain type
 */
public class Mem2CtxStepsChainImpl<C, P1, P2, PS extends BaseCtxStepsChain<?, ?>> implements
    Mem2CtxStepsChain<C, P1, P2, PS> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final SafeACContainer safeACContainer;
    private final C context;
    private final P1 previousContext;
    private final P2 previousContext2;
    private final PS previousContextStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter              the step reporter
     * @param exceptionHandler          the exception handler
     * @param safeACContainer           the safe AutoCloseable container
     * @param context                   the context
     * @param previousContext           the first previous context
     * @param previousContext2          the second previous context
     * @param previousContextStepsChain the previous context steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code safeACContainer} or {@code previousContextStepsChain} is null
     */
    public Mem2CtxStepsChainImpl(final StepReporter stepReporter,
                                 final ExceptionHandler exceptionHandler,
                                 final SafeACContainer safeACContainer,
                                 final C context,
                                 final P1 previousContext,
                                 final P2 previousContext2,
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
        this.previousContext2 = previousContext2;
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
    public final Mem2CtxStepsChain<C, P1, P2, PS> contextIsCloseable() {
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
    public final Mem2CtxStepsChain<C, P1, P2, PS> contextIsCloseable(final ThrowingConsumer<? super C, ?> close) {
        if (close == null) { this.throwNullArgException("close"); }
        this.safeACContainer.add(new AutoCloseableOf(() -> close.accept(this.context)));
        return this;
    }

    @Override
    public final Mem2CtxStepsChain<C, P1, P2, PS> closeCloseableContexts() {
        try {
            this.safeACContainer.close();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> supplyContext(
        final ThrowingConsumer<? super C, ? extends E> consumer
    ) throws E {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> supplyContext(
        final ThrowingBiConsumer<? super C, ? super P1, ? extends E> consumer
    ) throws E {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> supplyContext(
        final ThrowingTriConsumer<? super C, ? super P1, ? super P2, ? extends E> consumer
    ) throws E {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context, this.previousContext, this.previousContext2);
            return null;
        });
        return this;
    }

    @Override
    public final <R, E extends Throwable> R applyContext(
        final ThrowingFunction<? super C, ? extends R, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R applyContext(
        final ThrowingBiFunction<? super C, ? super P1, ? extends R, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context, this.previousContext));
    }

    @Override
    public final <R, E extends Throwable> R applyContext(
        final ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context, this.previousContext, this.previousContext2));
    }

    @Override
    public final MemNoCtxStepsChain<Mem2CtxStepsChain<C, P1, P2, PS>> withoutContext() {
        return new MemNoCtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, this);
    }

    @Override
    public final <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(final U context) {
        return this.newMem2CtxStepsChain(context);
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        final ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E {
        if (contextSupplier == null) { this.throwNullArgException("contextSupplier"); }
        return this.newMem2CtxStepsChain(this.execAction(contextSupplier));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        final ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> contextFunction.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        final ThrowingBiFunction<? super C, ? super P1, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> contextFunction.apply(this.context, this.previousContext)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> withContext(
        final ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> contextFunction.apply(this.context, this.previousContext, this.previousContext2)));
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
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
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final ConsumerStep<? super C, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final BiConsumerStep<? super C, ? super P1, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final TriConsumerStep<? super C, ? super P1, ? super P2, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.previousContext, this.previousContext2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
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
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepNamePrefix,
        final ConsumerStep<? super C, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepNamePrefix,
        final BiConsumerStep<? super C, ? super P1, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).accept(this.context, this.previousContext);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepNamePrefix,
        final TriConsumerStep<? super C, ? super P1, ? super P2, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withNamePrefix(stepNamePrefix).accept(this.context, this.previousContext, this.previousContext2);
            return null;
        });
        return this;
    }

    @Override
    public final Mem2CtxStepsChain<C, P1, P2, PS> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final ThrowingConsumer<? super C, ? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final ThrowingBiConsumer<? super C, ? super P1, ? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final ThrowingTriConsumer<? super C, ? super P1, ? super P2, ? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
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
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<? super C, ? extends E> step
    ) throws E {
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
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingBiConsumer<? super C, ? super P1, ? extends E> step
    ) throws E {
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
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingTriConsumer<? super C, ? super P1, ? super P2, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.context, this.previousContext, this.previousContext2);
            return null;
        });
        return this;
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final BiFunctionStep<? super C, ? super P1, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context, this.previousContext)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final TriFunctionStep<? super C, ? super P1, ? super P2, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.apply(this.context, this.previousContext, this.previousContext2)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepNamePrefix,
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step.withNamePrefix(stepNamePrefix)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepNamePrefix,
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withNamePrefix(stepNamePrefix).apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepNamePrefix,
        final BiFunctionStep<? super C, ? super P1, ? extends U, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withNamePrefix(stepNamePrefix).apply(this.context, this.previousContext)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepNamePrefix,
        final TriFunctionStep<? super C, ? super P1, ? super P2, ? extends U, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withNamePrefix(stepNamePrefix)
                .apply(this.context, this.previousContext, this.previousContext2)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final ThrowingBiFunction<? super C, ? super P1, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription, step));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription, () -> step.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingBiFunction<? super C, ? super P1, ? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.previousContext)));
    }

    @Override
    public final <U, E extends Throwable> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.previousContext, this.previousContext2)));
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
        final FunctionStep<? super C, ? extends R, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final BiFunctionStep<? super C, ? super P1, ? extends R, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.previousContext));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final TriFunctionStep<? super C, ? super P1, ? super P2, ? extends R, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.previousContext, this.previousContext2));
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
        final String stepNamePrefix,
        final FunctionStep<? super C, ? extends R, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withNamePrefix(stepNamePrefix).apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepNamePrefix,
        final BiFunctionStep<? super C, ? super P1, ? extends R, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withNamePrefix(stepNamePrefix).apply(this.context, this.previousContext));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepNamePrefix,
        final TriFunctionStep<? super C, ? super P1, ? super P2, ? extends R, ? extends E> step
    ) throws E {
        if (stepNamePrefix == null) { this.throwNullArgException("stepNamePrefix"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() ->
            step.withNamePrefix(stepNamePrefix).apply(this.context, this.previousContext, this.previousContext2));
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
        final ThrowingFunction<? super C, ? extends R, ? extends E> step
    ) throws E {
        return this.stepTo(stepName, "", step);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingBiFunction<? super C, ? super P1, ? extends R, ? extends E> step
    ) throws E {
        return this.stepTo(stepName, "", step);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ? extends E> step
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
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<? super C, ? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription, () -> step.apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingBiFunction<? super C, ? super P1, ? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.previousContext));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.previousContext, this.previousContext2));
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> nestedSteps(
        final String stepName,
        final ThrowingConsumer<Mem2CtxStepsChain<C, P1, P2, PS>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<Mem2CtxStepsChain<C, P1, P2, PS>, ? extends E> stepsChain
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
        final ThrowingFunction<Mem2CtxStepsChain<C, P1, P2, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<Mem2CtxStepsChain<C, P1, P2, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final <E extends Throwable> Mem2CtxStepsChain<C, P1, P2, PS> branchSteps(
        final ThrowingConsumer<Mem2CtxStepsChain<C, P1, P2, PS>, ? extends E> stepsChain
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
        return this.stepReporter.report(this.safeACContainer, this.exceptionHandler, stepName, stepDescription,
            new Object[]{this.context, this.previousContext, this.previousContext2}, step);
    }

    private <R, E extends Throwable> R execAction(
        final ThrowingSupplier<R, E> action
    ) throws E {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.safeACContainer.close(ex);
            this.exceptionHandler.handle(ex);
            throw ex;
        }
    }

    private <U> Mem2CtxStepsChain<U, C, P1, Mem2CtxStepsChain<C, P1, P2, PS>> newMem2CtxStepsChain(final U newContext) {
        return new Mem2CtxStepsChainImpl<>(this.stepReporter, this.exceptionHandler, this.safeACContainer, newContext,
            this.context, this.previousContext, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.safeACContainer.close(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
