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
import com.plugatar.xteps.checked.chain.CtxSC;
import com.plugatar.xteps.checked.chain.NoCtxSC;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

/**
 * No context steps chain implementation.
 */
public class NoCtxSCOf implements NoCtxSC {
    private final StepReporter stepReporter;
    private final ExceptionHandler exceptionHandler;
    private final HookContainer hookContainer;

    /**
     * Ctor.
     *
     * @param stepReporter     the step reporter
     * @param exceptionHandler the exception handler
     * @param hookContainer    the hook container
     * @throws NullPointerException if {@code stepReporter} or {@code exceptionHandler}
     *                              or {@code hookContainer} is null
     */
    public NoCtxSCOf(final StepReporter stepReporter,
                     final ExceptionHandler exceptionHandler,
                     final HookContainer hookContainer) {
        if (stepReporter == null) { throw new NullPointerException("stepReporter arg is null"); }
        if (exceptionHandler == null) { throw new NullPointerException("exceptionHandler arg is null"); }
        if (hookContainer == null) { throw new NullPointerException("hookContainer arg is null"); }
        this.stepReporter = stepReporter;
        this.exceptionHandler = exceptionHandler;
        this.hookContainer = hookContainer;
    }

    @Override
    public final NoCtxSC callChainHooks() {
        try {
            this.hookContainer.callHooks();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
        return this;
    }

    @Override
    public final NoCtxSC chainHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        this.hookContainer.add(hook);
        return this;
    }

    @Override
    public final NoCtxSC threadHook(
        final ThrowingRunnable<?> hook
    ) {
        if (hook == null) { this.throwNullArgException("hook"); }
        ThreadHooks.add(() -> ThrowingRunnable.unchecked(hook).run());
        return this;
    }

    @Override
    public final <U> CtxSC<U, NoCtxSC> withCtx(final U context) {
        return this.newCtxStepsChain(context);
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U, NoCtxSC> withCtx(
        final ThrowingSupplier<? extends U, ? extends E> supplier
    ) throws E {
        if (supplier == null) { this.throwNullArgException("supplier"); }
        return this.newCtxStepsChain(this.execAction(supplier));
    }

    @Override
    public final <E extends Throwable> NoCtxSC action(
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
    public final <R, E extends Throwable> R actionTo(
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (action == null) { this.throwNullArgException("action"); }
        return this.execAction(action);
    }

    @Override
    public final NoCtxSC step(final String name) {
        return this.step(name, "");
    }

    @Override
    public final NoCtxSC step(
        final String name,
        final String desc
    ) {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        this.reportStep(name, desc, () -> null);
        return this;
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
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
    public final <E extends Throwable> NoCtxSC step(
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step);
        return this;
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
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
    public final <E extends Throwable> NoCtxSC step(
        final String keyword,
        final SupplierStep<?, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        this.execAction(step.withKeyword(keyword));
        return this;
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
        final ThrowingRunnable<? extends E> action
    ) throws E {
        return this.step("", "", action);
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
        final String name,
        final ThrowingRunnable<? extends E> action
    ) throws E {
        return this.step(name, "", action);
    }

    @Override
    public final <E extends Throwable> NoCtxSC step(
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
    public final <U, E extends Throwable> CtxSC<U, NoCtxSC> stepToCtx(
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (step == null) { this.throwNullArgException("step"); }
        return this.newCtxStepsChain(this.execAction(step));
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U, NoCtxSC> stepToCtx(
        final String keyword,
        final SupplierStep<? extends U, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.newCtxStepsChain(this.execAction(step.withKeyword(keyword)));
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U, NoCtxSC> stepToCtx(
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx("", "", action);
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U, NoCtxSC> stepToCtx(
        final String name,
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        return this.stepToCtx(name, "", action);
    }

    @Override
    public final <U, E extends Throwable> CtxSC<U, NoCtxSC> stepToCtx(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends U, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
        return this.newCtxStepsChain(this.reportStep(name, desc, action));
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
        final String keyword,
        final SupplierStep<? extends R, ? extends E> step
    ) throws E {
        if (keyword == null) { this.throwNullArgException("keyword"); }
        if (step == null) { this.throwNullArgException("step"); }
        return this.execAction(step.withKeyword(keyword));
    }

    @Override
    public final <R, E extends Throwable> R stepTo(
        final ThrowingSupplier<? extends R, ? extends E> action
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
        final String desc,
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (action == null) { this.throwNullArgException("action"); }
        return this.reportStep(name, desc, action);
    }

    @Override
    public final <E extends Throwable> NoCtxSC nestedSteps(
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps("", "", stepsChain);
    }

    @Override
    public final <E extends Throwable> NoCtxSC nestedSteps(
        final String name,
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
    ) throws E {
        return this.nestedSteps(name, "", stepsChain);
    }

    @Override
    public final <E extends Throwable> NoCtxSC nestedSteps(
        final String name,
        final String desc,
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
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
        final ThrowingFunction<NoCtxSC, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo("", "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String name,
        final ThrowingFunction<NoCtxSC, ? extends R, ? extends E> stepsChain
    ) throws E {
        return this.nestedStepsTo(name, "", stepsChain);
    }

    @Override
    public final <R, E extends Throwable> R nestedStepsTo(
        final String name,
        final String desc,
        final ThrowingFunction<NoCtxSC, ? extends R, ? extends E> stepsChain
    ) throws E {
        if (name == null) { this.throwNullArgException("name"); }
        if (desc == null) { this.throwNullArgException("desc"); }
        if (stepsChain == null) { this.throwNullArgException("stepsChain"); }
        return this.reportStep(name, desc, () -> stepsChain.apply(this));
    }

    @Override
    public final <E extends Throwable> NoCtxSC branchSteps(
        final ThrowingConsumer<NoCtxSC, ? extends E> stepsChain
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
            new Object[]{}, step);
    }

    private <R, E extends Throwable> R execAction(
        final ThrowingSupplier<R, E> action
    ) throws E {
        try {
            return action.get();
        } catch (final Throwable ex) {
            this.exceptionHandler.handle(ex);
            throw ex;
        }
    }

    private <U> CtxSC<U, NoCtxSC> newCtxStepsChain(final U newContext) {
        return new CtxSCOf<>(this.stepReporter, this.exceptionHandler, this.hookContainer, newContext, this);
    }

    private void throwNullArgException(final String argName) {
        final XtepsException baseEx = new XtepsException(argName + " arg is null");
        this.exceptionHandler.handle(baseEx);
        throw baseEx;
    }
}
