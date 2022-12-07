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
import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.base.hook.ThreadHooks;
import com.plugatar.xteps.checked.chain.Ctx2SC;
import com.plugatar.xteps.checked.chain.CtxSC;
import com.plugatar.xteps.checked.chain.MemNoCtxSC;
import com.plugatar.xteps.checked.chain.base.BaseSC;
import com.plugatar.xteps.checked.stepobject.ConsumerStep;
import com.plugatar.xteps.checked.stepobject.FunctionStep;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * Contextual steps chain implementation.
 *
 * @param <C>  the context type
 * @param <PS> the previous steps chain type
 */
public class CtxSCOf<C, PS extends BaseSC<PS>> implements CtxSC<C, PS> {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final HookContainer hookContainer;
    private final C context;
    private final PS previousStepsChain;

    /**
     * Ctor.
     *
     * @param stepReporter       the step reporter
     * @param exceptionHandler   the exception handler
     * @param hookContainer      the hook container
     * @param context            the context
     * @param previousStepsChain the previous steps chain
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code hookContainer} is null
     */
    public CtxSCOf(final StepReporter stepReporter,
                   final ExceptionHandler exceptionHandler,
                   final HookContainer hookContainer,
                   final C context,
                   final PS previousStepsChain) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (hookContainer == null) { throw new NullPointerException("hookContainer arg is null"); }
        if (previousStepsChain == null) { throw new NullPointerException("previousStepsChain arg is null"); }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.hookContainer = hookContainer;
        this.context = context;
        this.previousStepsChain = previousStepsChain;
    }

    @Override
    public final CtxSC<C, PS> callChainHooks() {
        try {
            this.hookContainer.callHooks();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final CtxSC<C, PS> chainHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(hook);
        return this;
    }

    @Override
    public final CtxSC<C, PS> chainHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(() -> hook.accept(this.context));
        return this;
    }

    @Override
    public final CtxSC<C, PS> threadHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingRunnable.unchecked(hook).run());
        return this;
    }

    @Override
    public final CtxSC<C, PS> threadHook(
        final ThrowingConsumer<? super C, ?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingConsumer.unchecked(hook).accept(this.context));
        return this;
    }

    @Override
    public final C ctx() {
        return this.context;
    }

    @Override
    public final PS previousStepsChain() {
        return this.previousStepsChain;
    }

    @Override
    public final MemNoCtxSC<CtxSC<C, PS>> withoutContext() {
        return new MemNoCtxSCOf<>(this.stepReporter, this.exceptionHandler, this.hookContainer, this);
    }

    @Override
    public final <U> Ctx2SC<U, C, CtxSC<C, PS>> withCtx(final U context) {
        return newMem1CtxStepsChain(context);
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> withCtx(
        final ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E {
        if (supplier == null) { this.throwNullArgException("supplier"); }
        return newMem1CtxStepsChain(this.execAction(supplier));
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> withCtx(
        final ThrowingFunction<? super C, ? extends U, ? extends E> function
    ) throws E {
        if (function == null) { this.throwNullArgException("function"); }
        return newMem1CtxStepsChain(this.execAction(() -> function.apply(this.context)));
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> action(
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
    public final <E extends Throwable> CtxSC<C, PS> action(
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
    public final CtxSC<C, PS> step(final String name) {
        return this.step(name, "");
    }

    @Override
    public final CtxSC<C, PS> step(
        final String name,
        final String desc
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        this.reportStep(name, desc, () -> null);
        return this;
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
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
    public final <E extends Throwable> CtxSC<C, PS> step(
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
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
    public final <E extends Throwable> CtxSC<C, PS> step(
        final FunctionStep<? super C, ?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.apply(this.context));
        return this;
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
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
    public final <E extends Throwable> CtxSC<C, PS> step(
        final String keyword,
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
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
    public final <E extends Throwable> CtxSC<C, PS> step(
        final String keyword,
        final FunctionStep<? super C, ?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(() -> step.withKeyword(keyword).apply(this.context));
        return this;
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
        final ThrowingRunnable<? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
        final String name,
        final ThrowingRunnable<? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
        final String name,
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
        final String name,
        final String desc,
        final ThrowingRunnable<? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
        this.reportStep(name, desc, () -> {
            action.run();
            return null;
        });
        return this;
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> step(
        final String name,
        final String desc,
        final ThrowingConsumer<? super C, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
        this.reportStep(name, desc, () -> {
            action.accept(this.context);
            return null;
        });
        return this;
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(() -> step.apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final String keyword,
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(step.withKeyword(keyword)));
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final String keyword,
        final FunctionStep<? super C, ? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return newMem1CtxStepsChain(this.execAction(() -> step.withKeyword(keyword).apply(this.context)));
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final ThrowingFunction<? super C, ? extends U, ? extends E> step
    ) throws E {
        return this.stepToCtx("", "", step);
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final String name,
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (action == null) { this.throwNullArgException("action"); }
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final String name,
        final ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
        return newMem1CtxStepsChain(this.reportStep(name, desc, action));
    }

    @Override
    public final <U, E extends Throwable> Ctx2SC<U, C, CtxSC<C, PS>> stepToCtx(
        final String name,
        final String desc,
        final ThrowingFunction<? super C, ? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
        return newMem1CtxStepsChain(this.reportStep(name, desc, () -> action.apply(this.context)));
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
        final String desc,
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
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
        if (action == null) { this.throwNullArgException("action"); }
        return this.reportStep(name, desc, () -> action.apply(this.context));
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> nestedSteps(
        final ThrowingConsumer<CtxSC<C, PS>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps("", "", stepsChain);
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> nestedSteps(
        final String name,
        final ThrowingConsumer<CtxSC<C, PS>, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(name, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> nestedSteps(
        final String name,
        final String desc,
        final ThrowingConsumer<CtxSC<C, PS>, ? extends E> stepsChain
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
        final ThrowingFunction<CtxSC<C, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo("", "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String name,
        final ThrowingFunction<CtxSC<C, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(name, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String name,
        final String desc,
        final ThrowingFunction<CtxSC<C, PS>, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(name, desc, () -> stepsChain.apply(this));
    }

    @Override
    public final <E extends Throwable> CtxSC<C, PS> branchSteps(
        final ThrowingConsumer<CtxSC<C, PS>, ? extends E> stepsChain
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
            new Object[]{this.context}, step);
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

    private <U> Ctx2SC<U, C, CtxSC<C, PS>> newMem1CtxStepsChain(final U newContext) {
        return new Ctx2SCOf<>(this.stepReporter, this.exceptionHandler, this.hookContainer, newContext,
            this.context, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.hookContainer.callHooks(baseEx);
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
