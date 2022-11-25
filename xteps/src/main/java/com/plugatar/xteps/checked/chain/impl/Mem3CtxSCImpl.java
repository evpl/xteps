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
import com.plugatar.xteps.base.HookContainer;
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
import com.plugatar.xteps.base.hook.ThreadHooks;
import com.plugatar.xteps.checked.chain.Mem3CtxSC;
import com.plugatar.xteps.checked.chain.MemNoCtxSC;
import com.plugatar.xteps.checked.chain.base.BaseCtxSC;
import com.plugatar.xteps.checked.stepobject.BiConsumerStep;
import com.plugatar.xteps.checked.stepobject.BiFunctionStep;
import com.plugatar.xteps.checked.stepobject.ConsumerStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;
import com.plugatar.xteps.checked.stepobject.TriConsumerStep;
import com.plugatar.xteps.checked.stepobject.TriFunctionStep;

/**
 * Memorizing triple context steps chain implementation.
 *
 * @param <C>  the context type
 * @param <C2> the second context type
 * @param <C3> the third context type
 * @param <PS> the previous context steps chain type
 */
public class Mem3CtxSCImpl<C, C2, C3, PS extends BaseCtxSC<?>> implements Mem3CtxSC<C, C2, C3, PS> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final HookContainer hookContainer;
    private final C context;
    private final C2 context2;
    private final C3 context3;
    private final PS previousContextStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter              the step reporter
     * @param exceptionHandler          the exception handler
     * @param hookContainer             the hook container
     * @param context                   the context
     * @param context2                  the first previous context
     * @param context3                  the second previous context
     * @param previousContextStepsChain the previous context steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code hookContainer} or {@code previousContextStepsChain} is null
     */
    public Mem3CtxSCImpl(final StepReporter stepReporter,
                         final ExceptionHandler exceptionHandler,
                         final HookContainer hookContainer,
                         final C context,
                         final C2 context2,
                         final C3 context3,
                         final PS previousContextStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (hookContainer == null) { throw new NullPointerException("hookContainer arg is null"); }
        if (previousContextStepsChain == null) {
            throw new NullPointerException("previousContextStepsChain arg is null");
        }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.hookContainer = hookContainer;
        this.context = context;
        this.context2 = context2;
        this.context3 = context3;
        this.previousContextStepsChain = previousContextStepsChain;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> callChainHooks() {
        try {
            this.hookContainer.callHooks();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> chainHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(hook);
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> chainHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context));
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> chainHook(
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context, this.context2));
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> chainHook(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> threadHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingRunnable.unchecked(hook).run());
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> threadHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingConsumer.unchecked(hook).accept(this.context));
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> threadHook(
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingBiConsumer.unchecked(hook).accept(this.context, this.context2));
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> threadHook(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() ->
            ThrowingTriConsumer.unchecked(hook).accept(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final C context() {
        return this.context;
    }

    @Override
    public final C2 context2() {
        return this.context2;
    }

    @Override
    public final C3 context3() {
        return this.context3;
    }

    @Override
    public final PS previousContextStepsChain() {
        return this.previousContextStepsChain;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> supplyContext(
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
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> supplyContext(
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> consumer
    ) throws E {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> supplyContext(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> consumer
    ) throws E {
        if (consumer == null) { this.throwNullArgException("consumer"); }
        this.execAction(() -> {
            consumer.accept(this.context, this.context2, this.context3);
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
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context, this.context2));
    }

    @Override
    public final <R, E extends Throwable> R applyContext(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.execAction(() -> function.apply(this.context, this.context2, this.context3));
    }

    @Override
    public final MemNoCtxSC<Mem3CtxSC<C, C2, C3, PS>> withoutContext() {
        return new MemNoCtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.hookContainer, this);
    }

    @Override
    public final <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(final U context) {
        return this.newMem2CtxStepsChain(context);
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        final ThrowingSupplier<? extends U, ? extends E> contextSupplier
    ) throws E {
        if (contextSupplier == null) { this.throwNullArgException("contextSupplier"); }
        return this.newMem2CtxStepsChain(this.execAction(contextSupplier));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        final ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> contextFunction.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> contextFunction.apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> withContext(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> contextFunction
    ) throws E {
        if (contextFunction == null) { this.throwNullArgException("contextFunction"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> contextFunction.apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final FunctionStep<? super C, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context));
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final BiConsumerStep<? super C, ? super C2, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final BiFunctionStep<? super C, ? super C2, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context, this.context2));
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final TriConsumerStep<? super C, ? super C2, ? super C3, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final RunnableStep<? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).run();
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final ConsumerStep<? super C, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final FunctionStep<? super C, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context));
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final BiConsumerStep<? super C, ? super C2, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2));
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final TriConsumerStep<? super C, ? super C2, ? super C3, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> step(final String stepName) {
        return this.step(stepName, "");
    }

    @Override
    public final Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final String stepDescription
    ) {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        this.reportStep(stepName, stepDescription, () -> null);
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final ThrowingConsumer<? super C, ? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> step
    ) throws E {
        return this.step(stepName, "", step);
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> step(
        final String stepName,
        final String stepDescription,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.reportStep(stepName, stepDescription, () -> {
            step.accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String keyword,
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step.withKeyword(keyword)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String keyword,
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword).apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword).apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword)
                .apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String stepName,
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String stepName,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToContext(stepName, "", step);
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
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
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
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
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> stepToContext(
        final String stepName,
        final String stepDescription,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.context2, this.context3)));
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
        final BiFunctionStep<? super C, ? super C2, ? extends R, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.context2));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.context2, this.context3));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String keyword,
        final SupplierStep<? extends R, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step.withKeyword(keyword));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String keyword,
        final FunctionStep<? super C, ? extends R, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withKeyword(keyword).apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ? extends R, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() ->
            step.withKeyword(keyword).apply(this.context, this.context2, this.context3));
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
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> step
    ) throws E {
        return this.stepTo(stepName, "", step);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
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
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.context2));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> step
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.reportStep(stepName, stepDescription,
            () -> step.apply(this.context, this.context2, this.context3));
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> nestedSteps(
        final String stepName,
        final ThrowingConsumer<Mem3CtxSC<C, C2, C3, PS>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(stepName, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> nestedSteps(
        final String stepName,
        final String stepDescription,
        final ThrowingConsumer<Mem3CtxSC<C, C2, C3, PS>, ? extends E> stepsChain
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
        final ThrowingFunction<Mem3CtxSC<C, C2, C3, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(stepName, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String stepName,
        final String stepDescription,
        final ThrowingFunction<Mem3CtxSC<C, C2, C3, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (stepName == null) { this.throwNullArgException("stepName"); }
        if (stepDescription == null) { this.throwNullArgException("stepDescription"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(stepName, stepDescription, () -> stepsChain.apply(this));
    }

    @Override
    public final <E extends Throwable> Mem3CtxSC<C, C2, C3, PS> branchSteps(
        final ThrowingConsumer<Mem3CtxSC<C, C2, C3, PS>, ? extends E> stepsChain
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
        return this.stepReporter.report(this.hookContainer, this.exceptionHandler, stepName, stepDescription,
            new Object[]{this.context, this.context2, this.context3}, step);
    }

    private <R, E extends Throwable> R execAction(
        final ThrowingSupplier<R, E> action
    ) throws E {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.hookContainer.callHooks(ex);
            this.exceptionHandler.handle(ex);
            throw ex;
        }
    }

    private <U> Mem3CtxSC<U, C, C2, Mem3CtxSC<C, C2, C3, PS>> newMem2CtxStepsChain(final U newContext) {
        return new Mem3CtxSCImpl<>(this.stepReporter, this.exceptionHandler, this.hookContainer, newContext,
            this.context, this.context2, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.hookContainer.callHooks(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
