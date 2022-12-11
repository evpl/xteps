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
import com.plugatar.xteps.base.HooksContainer;
import com.plugatar.xteps.base.HooksOrder;
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
import com.plugatar.xteps.unchecked.chain.Ctx3SC;
import com.plugatar.xteps.unchecked.chain.MemNoCtxSC;
import com.plugatar.xteps.unchecked.chain.base.BaseSC;
import com.plugatar.xteps.unchecked.stepobject.BiConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.BiFunctionStep;
import com.plugatar.xteps.unchecked.stepobject.ConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.FunctionStep;
import com.plugatar.xteps.unchecked.stepobject.RunnableStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;
import com.plugatar.xteps.unchecked.stepobject.TriConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.TriFunctionStep;

import static com.plugatar.xteps.base.HookPriority.MAX_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.MIN_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.NORM_HOOK_PRIORITY;
import static com.plugatar.xteps.unchecked.chain.impl.StepsChainUtils.sneakyThrow;

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
    private final HooksContainer hooksContainer;
    private final C context;
    private final C2 context2;
    private final C3 context3;
    private final PS previousStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter       the step reporter
     * @param exceptionHandler   the exception handler
     * @param hooksContainer     the hooks container
     * @param context            the context
     * @param context2           the second context
     * @param context3           the third context
     * @param previousStepsChain the previous steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code hooksContainer} or {@code previousStepsChain} is null
     */
    public Ctx3SCOf(final StepReporter stepReporter,
                    final ExceptionHandler exceptionHandler,
                    final HooksContainer hooksContainer,
                    final C context,
                    final C2 context2,
                    final C3 context3,
                    final PS previousStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (hooksContainer == null) { throw new NullPointerException("hooksContainer arg is null"); }
        if (previousStepsChain == null) { throw new NullPointerException("previousStepsChain arg is null"); }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.hooksContainer = hooksContainer;
        this.context = context;
        this.context2 = context2;
        this.context3 = context3;
        this.previousStepsChain = previousStepsChain;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> callChainHooks() {
        try {
            this.hooksContainer.callHooks();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHooksOrder(final HooksOrder order) {
        if (order == null) { this.throwNullArgException("order"); }
        this.hooksContainer.setOrder(order);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHooksOrder(final HooksOrder order) {
        if (order == null) { this.throwNullArgException("order"); }
        ThreadHooks.setOrder(order);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingRunnable<?> hook
    ) {
        return this.chainHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final int priority,
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        this.hooksContainer.addHook(priority, hook);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        return this.chainHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final int priority,
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        this.hooksContainer.addHook(priority, () -> hook.accept(this.context));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        return this.chainHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final int priority,
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        this.hooksContainer.addHook(priority, () -> hook.accept(this.context, this.context2));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        return this.chainHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> chainHook(
        final int priority,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        this.hooksContainer.addHook(priority, () -> hook.accept(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingRunnable<?> hook
    ) {
        return this.threadHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final int priority,
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        ThreadHooks.addHook(priority, hook);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        return this.threadHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final int priority,
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        ThreadHooks.addHook(priority, () -> hook.accept(this.context));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        return this.threadHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final int priority,
        final ThrowingBiConsumer<? super C, ? super C2, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        ThreadHooks.addHook(priority, () -> hook.accept(this.context, this.context2));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        return this.threadHook(NORM_HOOK_PRIORITY, hook);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> threadHook(
        final int priority,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.checkPriorityArg(priority);
        ThreadHooks.addHook(priority, () -> hook.accept(this.context, this.context2, this.context3));
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
        return new MemNoCtxSCOf<>(this.stepReporter, this.exceptionHandler, this.hooksContainer, this);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(final U context) {
        return this.newMem2CtxStepsChain(context);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingSupplier<? extends U, ?> supplier
    ) {
        if (supplier == null) { this.throwNullArgException("supplier"); }
        return this.newMem2CtxStepsChain(this.execAction(supplier));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingFunction<? super C, ? extends U, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> function.apply(this.context)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> function.apply(this.context, this.context2)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> withCtx(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> function
    ) {
        if (function == null) { this.throwNullArgException("function"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> function.apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> action(
        final ThrowingRunnable<?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.run();
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> action(
        final ThrowingConsumer<? super C, ?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> action(
        final ThrowingBiConsumer<? super C, ? super C2, ?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> action(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        this.execAction(() -> {
            action.accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final <R> R actionTo(
        final ThrowingSupplier<? extends R, ?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(action);
    }

    @Override
    public final <R> R actionTo(
        final ThrowingFunction<? super C, ? extends R, ?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(() -> action.apply(this.context));
    }

    @Override
    public final <R> R actionTo(
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
    ) {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(() -> action.apply(this.context, this.context2));
    }

    @Override
    public final <R> R actionTo(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ?> action
    ) {
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
    public final Ctx3SC<C, C2, C3, PS> step(
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final SupplierStep<?> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final FunctionStep<? super C, ?> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final BiConsumerStep<? super C, ? super C2> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final BiFunctionStep<? super C, ? super C2, ?> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context, this.context2));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final TriConsumerStep<? super C, ? super C2, ? super C3> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ?> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final SupplierStep<?> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final FunctionStep<? super C, ?> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final BiConsumerStep<? super C, ? super C2> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).accept(this.context, this.context2);
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ?> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final TriConsumerStep<? super C, ? super C2, ? super C3> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> {
            step.withKeyword(keyword).accept(this.context, this.context2, this.context3);
            return null;
        });
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ?> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2, this.context3));
        return this;
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final ThrowingRunnable<?> action
    ) {
        return this.step("", "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final ThrowingConsumer<? super C, ?> action
    ) {
        return this.step("", "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final ThrowingBiConsumer<? super C, ? super C2, ?> action
    ) {
        return this.step("", "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> action
    ) {
        return this.step("", "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingRunnable<?> action
    ) {
        return this.step(name, "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingConsumer<? super C, ?> action
    ) {
        return this.step(name, "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingBiConsumer<? super C, ? super C2, ?> action
    ) {
        return this.step(name, "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> action
    ) {
        return this.step(name, "", action);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingRunnable<?> action
    ) {
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingConsumer<? super C, ?> action
    ) {
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingBiConsumer<? super C, ? super C2, ?> action
    ) {
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
    public final Ctx3SC<C, C2, C3, PS> step(
        final String name,
        final String desc,
        final ThrowingTriConsumer<? super C, ? super C2, ? super C3, ?> action
    ) {
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
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final SupplierStep<? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final FunctionStep<? super C, ? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final BiFunctionStep<? super C, ? super C2, ? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(() -> step.apply(this.context, this.context2)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final SupplierStep<? extends U> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(step.withKeyword(keyword)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final FunctionStep<? super C, ? extends U> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword).apply(this.context)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ? extends U> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword).apply(this.context, this.context2)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends U> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.execAction(
            () -> step.withKeyword(keyword)
                .apply(this.context, this.context2, this.context3)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingSupplier<? extends U, ?> action
    ) {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingFunction<? super C, ? extends U, ?> step
    ) {
        return this.stepToCtx("", "", step);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> action
    ) {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> action
    ) {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingSupplier<? extends U, ?> action
    ) {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingFunction<? super C, ? extends U, ?> action
    ) {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> action
    ) {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> action
    ) {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends U, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc, action));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingFunction<? super C, ? extends U, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc, () -> action.apply(this.context)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingBiFunction<? super C, ? super C2, ? extends U, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2)));
    }

    @Override
    public final <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends U, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.newMem2CtxStepsChain(this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2, this.context3)));
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
        final BiFunctionStep<? super C, ? super C2, ? extends R> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.context2));
    }

    @Override
    public final <R> R stepTo(
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R> step
    ) {
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.apply(this.context, this.context2, this.context3));
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
        final String keyword,
        final BiFunctionStep<? super C, ? super C2, ? extends R> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() -> step.withKeyword(keyword).apply(this.context, this.context2));
    }

    @Override
    public final <R> R stepTo(
        final String keyword,
        final TriFunctionStep<? super C, ? super C2, ? super C3, ? extends R> step
    ) {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(() ->
            step.withKeyword(keyword).apply(this.context, this.context2, this.context3));
    }

    @Override
    public final <R> R stepTo(
        final ThrowingSupplier<? extends R, ?> action
    ) {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R> R stepTo(
        final ThrowingFunction<? super C, ? extends R, ?> action
    ) {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R> R stepTo(
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
    ) {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R> R stepTo(
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ?> action
    ) {
        return this.stepTo("", "", action);
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final ThrowingSupplier<? extends R, ?> action
    ) {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final ThrowingFunction<? super C, ? extends R, ?> action
    ) {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
    ) {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ?> action
    ) {
        return this.stepTo(name, "", action);
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends R, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc, action);
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final String desc,
        final ThrowingFunction<? super C, ? extends R, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc, () -> action.apply(this.context));
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final String desc,
        final ThrowingBiFunction<? super C, ? super C2, ? extends R, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2));
    }

    @Override
    public final <R> R stepTo(
        final String name,
        final String desc,
        final ThrowingTriFunction<? super C, ? super C2, ? super C3, ? extends R, ?> action
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("step"); }
        return this.reportStep(name, desc,
            () -> action.apply(this.context, this.context2, this.context3));
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> nestedSteps(
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ?> stepsChain
    ) {
        return this.nestedSteps("", "", stepsChain);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> nestedSteps(
        final String name,
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ?> stepsChain
    ) {
        return this.nestedSteps(name, "", stepsChain);
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> nestedSteps(
        final String name,
        final String desc,
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ?> stepsChain
    ) {
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
    public final <R> R nestedStepsTo(
        final ThrowingFunction<Ctx3SC<C, C2, C3, PS>, ? extends R, ?> stepsChain
    ) {
        return this.nestedStepsTo("", "", stepsChain);
    }

    @Override
    public final <R> R nestedStepsTo(
        final String name,
        final ThrowingFunction<Ctx3SC<C, C2, C3, PS>, ? extends R, ?> stepsChain
    ) {
        return this.nestedStepsTo(name, "", stepsChain);
    }

    @Override
    public final <R> R nestedStepsTo(
        final String name,
        final String desc,
        final ThrowingFunction<Ctx3SC<C, C2, C3, PS>, ? extends R, ?> stepsChain
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(name, desc, () -> stepsChain.apply(this));
    }

    @Override
    public final Ctx3SC<C, C2, C3, PS> branchSteps(
        final ThrowingConsumer<Ctx3SC<C, C2, C3, PS>, ?> stepsChain
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
        return this.stepReporter.report(this.hooksContainer, this.exceptionHandler, stepName, stepDescription,
            new Object[]{this.context, this.context2, this.context3}, ThrowingSupplier.unchecked(step));
    }

    private <R> R execAction(
        final ThrowingSupplier<R, ?> action
    ) {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.hooksContainer.callHooks(ex);
            this.exceptionHandler.handle(ex);
            throw sneakyThrow(ex);
        }
    }

    private <U> Ctx3SC<U, C, C2, Ctx3SC<C, C2, C3, PS>> newMem2CtxStepsChain(final U newContext) {
        return new Ctx3SCOf<>(this.stepReporter, this.exceptionHandler, this.hooksContainer, newContext,
            this.context, this.context2, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.hooksContainer.callHooks(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }

    private void checkPriorityArg(final int priority) {
        if (priority < MIN_HOOK_PRIORITY || priority > MAX_HOOK_PRIORITY) {
            final XtepsException baseEx = new XtepsException("priority arg not in the range " + MIN_HOOK_PRIORITY +
                " to " + MAX_HOOK_PRIORITY);
            this.hooksContainer.callHooks(baseEx);
            this.exceptionHandler.handle(baseEx);
            throw baseEx;
        }
    }
}
