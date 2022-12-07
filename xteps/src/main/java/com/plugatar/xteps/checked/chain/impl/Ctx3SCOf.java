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
import com.plugatar.xteps.checked.chain.Ctx3SC;
import com.plugatar.xteps.checked.chain.MemNoCtxSC;
import com.plugatar.xteps.checked.chain.base.BaseSC;
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
 * @param <PS> the previous steps chain type
 */
public class Ctx3SCOf<C, C2, C3, PS extends BaseSC<PS>> implements Ctx3SC<C, C2, C3, PS> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final HookContainer hookContainer;
    private final C context;
    private final C2 context2;
    private final C3 context3;
    private final PS previousStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter       the step reporter
     * @param exceptionHandler   the exception handler
     * @param hookContainer      the hook container
     * @param context            the context
     * @param context2           the second context
     * @param context3           the third context
     * @param previousStepsChain the previous steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code hookContainer} or {@code previousStepsChain} is null
     */
    public Ctx3SCOf(final StepReporter stepReporter,
                    final ExceptionHandler exceptionHandler,
                    final HookContainer hookContainer,
                    final C context,
                    final C2 context2,
                    final C3 context3,
                    final PS previousStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (hookContainer == null) { throw new NullPointerException("hookContainer arg is null"); }
        if (previousStepsChain == null) { throw new NullPointerException("previousStepsChain arg is null"); }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.hookContainer = hookContainer;
        this.context = context;
        this.context2 = context2;
        this.context3 = context3;
        this.previousStepsChain = previousStepsChain;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> callChainHooks() {
        try {
            this.hookContainer.callHooks();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(hook);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context, this.context2));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingRunnable.unchecked(hook).run());
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingConsumer.unchecked(hook).accept(this.context));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingBiConsumer.unchecked(hook).accept(this.context, this.context2));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() ->
            ThrowingTriConsumer.unchecked(hook).accept(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final C ctx() {
        return this.context;
    }

    @Override
    public final C2 ctx2() {
        return this.context2;
    }

    @Override
    public final C3 ctx3() {
        return this.context3;
    }

    @Override
    public final PS previousStepsChain() {
        return this.previousStepsChain;
    }

    @Override
    public final MemNoCtxSC<Ctx3SC<C, C2, C3, PS>> withoutContext() {
        return new MemNoCtxSCOf<>(this.stepReporter, this.exceptionHandler, this.hookContainer, this);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(final U context) {
        return this.newMem2CtxStepsChain(context);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E {
        if (supplier == null) { this.throwNullArgException("supplier"); }
        return this.newMem2CtxStepsChain(this.execAction(supplier));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingFunction<? super C, ? extends U, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> function.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> function.apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> function.apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> action(
        final ThrowingRunnable<? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.run();
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> action(
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> action(
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> action(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final <R, E extends Throwable> R actionTo(
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(action);
    }

    @Override
    public final <R, E extends Throwable> R actionTo(
        final ThrowingFunction<? super C, ? extends R, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(() -> action.apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R actionTo(
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(() -> action.apply(this.context, this.context2));
    }

    @Override
    public final <R, E extends Throwable> R actionTo(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(() -> action.apply(this.context, this.context2, this.context3));
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(final String name) {
        return this.step(name, "");
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        this.reportStep(name, desc, () -> null);
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final FunctionStep<? super C, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context));
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final BiFunctionStep<? super C, ? super C2, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context, this.context2));
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final FunctionStep<? super C, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context));
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2));
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
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
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final ThrowingRunnable<? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingRunnable<? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingRunnable<? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        this.reportStep(name, desc, () -> {
            action.run();
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        this.reportStep(name, desc, () -> {
            action.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingBiConsumer<? super C, ? super C2, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        this.reportStep(name, desc, () -> {
            action.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        this.reportStep(name, desc, () -> {
            action.accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step.withKeyword(keyword)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword).apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword).apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
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
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToCtx("", "", step);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc, action));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc, () -> action.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2)));
    }

    @Override
    public final <U, E extends Throwable> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2, this.context3)));
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
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final ThrowingFunction<? super C, ? extends R, ? extends E> action
    ) throws E {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> action
    ) throws E {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
    ) throws E {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final ThrowingFunction<? super C, ? extends R, ? extends E> action
    ) throws E {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> action
    ) throws E {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
    ) throws E {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc, action);
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final String desc,
        final ThrowingFunction<? super C, ? extends R, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc, () -> action.apply(this.context));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final String desc,
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final String name,
        final String desc,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2, this.context3));
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> nestedSteps(
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps("", "", stepsChain);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> nestedSteps(
        final String name,
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(name, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> nestedSteps(
        final String name,
        final String desc,
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ? extends E> stepsChain
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        this.reportStep(name, desc, () -> {
            stepsChain.accept(this);
            return null;
        });
        return this;
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final ThrowingFunction<Ctx3SC<C, C2, C3, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo("", "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String name,
        final ThrowingFunction<Ctx3SC<C, C2, C3, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(name, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String name,
        final String desc,
        final ThrowingFunction<Ctx3SC<C, C2, C3, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(name, desc, () -> stepsChain.apply(this));
    }

    @Override
    public final <E extends Throwable> Ctx3SC<C, C2, C3, PS> branchSteps(
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ? extends E> stepsChain
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

    private <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> newMem2CtxStepsChain(final U newContext) {
        return new Ctx3SCOf<>(this.stepReporter, this.exceptionHandler, this.hookContainer, newContext,
            this.context, this.context2, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.hookContainer.callHooks(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
