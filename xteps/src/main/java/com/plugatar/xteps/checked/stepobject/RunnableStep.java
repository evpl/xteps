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
package com.plugatar.xteps.checked.stepobject;

import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.checked.Xteps.step;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Runnable step. This step will be executed and reported when calling the
 * {@link #run()} method.
 *
 * @param <E> the type of the throwing exception
 */
public class RunnableStep<E extends Throwable> implements ThrowingRunnable<E> {

    /**
     * The keyword of this step.
     */
    private final String keyword;

    /**
     * The name of this step.
     */
    private final String name;

    /**
     * The description of this step.
     */
    private final String desc;

    /**
     * The action of this step.
     */
    private final ThrowingRunnable<? extends E> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public RunnableStep(final ThrowingRunnable<? extends E> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(RunnableStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public RunnableStep(final String name,
                        final ThrowingRunnable<? extends E> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public RunnableStep(final String name,
                        final String desc,
                        final ThrowingRunnable<? extends E> action) {
        this("", name, desc, action);
    }

    /**
     * Ctor.
     *
     * @param keyword the step keyword
     * @param name    the step name
     * @param desc    the step description
     * @param action  the step action
     */
    public RunnableStep(final String keyword,
                        final String name,
                        final String desc,
                        final ThrowingRunnable<? extends E> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code RunnableStep}.
     *
     * @param name the step name
     * @return dummy {@code RunnableStep}
     */
    public static RunnableStep<RuntimeException> dummy(final String name) {
        return new RunnableStep<>(name, () -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final void run() throws E {
        step(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code RunnableStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code RunnableStep} with given keyword in the step name
     */
    public final RunnableStep<E> withKeyword(final String keyword) {
        return new RunnableStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked RunnableStep
     */
    @SuppressWarnings("unchecked")
    public final RunnableStep<RuntimeException> asUnchecked() {
        return (RunnableStep<RuntimeException>) this;
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code SupplierStep}
     */
    public final <R> SupplierStep<R, E> asSupplierStep(final R r) {
        return new SupplierStep<>(this.name, this.desc, () -> {
            this.action.run();
            return r;
        });
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param <T> the type of the input argument
     * @return {@code ConsumerStep}
     */
    public final <T> ConsumerStep<T, E> asConsumerStep() {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.run());
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return {@code BiConsumerStep}
     */
    public final <T, U> BiConsumerStep<T, U, E> asBiConsumerStep() {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.run());
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return {@code TriConsumerStep}
     */
    public final <T, U, V> TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.run());
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param r   the result
     * @param <T> the type of the input argument
     * @param <R> the type of the result
     * @return {@code FunctionStep}
     */
    public final <T, R> FunctionStep<T, R, E> asFunctionStep(final R r) {
        return new FunctionStep<>(this.name, this.desc, t -> {
            this.action.run();
            return r;
        });
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param r   the result
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <R> the type of the result
     * @return {@code BiFunctionStep}
     */
    public final <T, U, R> BiFunctionStep<T, U, R, E> asBiFunctionStep(final R r) {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> {
            this.action.run();
            return r;
        });
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param r   the result
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @param <R> the type of the result
     * @return {@code TriFunctionStep}
     */
    public final <T, U, V, R> TriFunctionStep<T, U, V, R, E> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> {
            this.action.run();
            return r;
        });
    }

    @Override
    public final String toString() {
        return "RunnableStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
