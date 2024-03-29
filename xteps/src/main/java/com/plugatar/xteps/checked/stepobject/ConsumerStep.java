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

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.checked.Xteps.stepsChainOf;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Consumer step. This step will be executed and reported when calling the
 * {@link #accept(Object)} method.
 *
 * @param <T> the type of the input argument
 * @param <E> the type of the throwing exception
 */
public class ConsumerStep<T, E extends Throwable> implements ThrowingConsumer<T, E> {

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
    private final ThrowingConsumer<? super T, ? extends E> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public ConsumerStep(final ThrowingConsumer<? super T, ? extends E> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(ConsumerStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public ConsumerStep(final String name,
                        final ThrowingConsumer<? super T, ? extends E> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public ConsumerStep(final String name,
                        final String desc,
                        final ThrowingConsumer<? super T, ? extends E> action) {
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
    public ConsumerStep(final String keyword,
                        final String name,
                        final String desc,
                        final ThrowingConsumer<? super T, ? extends E> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code ConsumerStep}.
     *
     * @param name the step name
     * @param <T>  the type of the input argument
     * @return dummy {@code ConsumerStep}
     */
    public static <T> ConsumerStep<T, RuntimeException> dummy(final String name) {
        return new ConsumerStep<>(name, t -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @param t the input argument
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final void accept(final T t) throws E {
        stepsChainOf(t).step(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code ConsumerStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code ConsumerStep} with given keyword in the step name
     */
    public final ConsumerStep<T, E> withKeyword(final String keyword) {
        return new ConsumerStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked {@code ConsumerStep}
     */
    @SuppressWarnings("unchecked")
    public final ConsumerStep<T, RuntimeException> asUnchecked() {
        return (ConsumerStep<T, RuntimeException>) this;
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep<E> asRunnableStep(final T t) {
        return new RunnableStep<>(this.name, this.desc, () -> this.action.accept(t));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t   the input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code SupplierStep}
     */
    public final <R> SupplierStep<R, E> asSupplierStep(final T t, final R r) {
        return new SupplierStep<>(this.name, this.desc, () -> {
            this.action.accept(t);
            return r;
        });
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param <U> the type of the second input argument
     * @return {@code BiConsumerStep}
     */
    public final <U> BiConsumerStep<T, U, E> asBiConsumer() {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.accept(t));
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return {@code TriConsumerStep}
     */
    public final <U, V> TriConsumerStep<T, U, V, E> asTriConsumer() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.accept(t));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code FunctionStep}
     */
    public final <R> FunctionStep<T, R, E> asFunctionStep(final R r) {
        return new FunctionStep<>(this.name, this.desc, t -> {
            this.action.accept(t);
            return r;
        });
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param r   the result
     * @param <U> the type of the second input argument
     * @param <R> the type of the result
     * @return {@code BiFunctionStep}
     */
    public final <U, R> BiFunctionStep<T, U, R, E> asBiFunctionStep(final R r) {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> {
            this.action.accept(t);
            return r;
        });
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param r   the result
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @param <R> the type of the result
     * @return {@code TriFunctionStep}
     */
    public final <U, V, R> TriFunctionStep<T, U, V, R, E> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> {
            this.action.accept(t);
            return r;
        });
    }

    @Override
    public final String toString() {
        return "ConsumerStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
