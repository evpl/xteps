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
package com.plugatar.xteps.unchecked.stepobject;

import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.unchecked.UncheckedXteps.stepsChainOf;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * BiConsumer step. This step will be executed and reported when calling the
 * {@link #accept(Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 */
public class BiConsumerStep<T, U> implements ThrowingBiConsumer<T, U, RuntimeException> {

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
    private final ThrowingBiConsumer<? super T, ? super U, ?> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public BiConsumerStep(final ThrowingBiConsumer<? super T, ? super U, ?> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(BiConsumerStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public BiConsumerStep(final String name,
                          final ThrowingBiConsumer<? super T, ? super U, ?> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public BiConsumerStep(final String name,
                          final String desc,
                          final ThrowingBiConsumer<? super T, ? super U, ?> action) {
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
    public BiConsumerStep(final String keyword,
                          final String name,
                          final String desc,
                          final ThrowingBiConsumer<? super T, ? super U, ?> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code BiConsumerStep}.
     *
     * @param name the step name
     * @param <T>  the type of the first input argument
     * @param <U>  the type of the second input argument
     * @return dummy {@code BiConsumerStep}
     */
    public static <T, U> BiConsumerStep<T, U> dummy(final String name) {
        return new BiConsumerStep<>(name, (t, u) -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final void accept(final T t, final U u) {
        stepsChainOf(t, u).step(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code BiConsumerStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code BiConsumerStep} with given keyword in the step name
     */
    public final BiConsumerStep<T, U> withKeyword(final String keyword) {
        return new BiConsumerStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as {@code BiConsumerStep<U, T>}.
     *
     * @return {@code BiConsumerStep<U, T>}
     */
    public final BiConsumerStep<U, T> asUT() {
        return new BiConsumerStep<>(this.name, this.desc, (u, t) -> this.action.accept(t, u));
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep asRunnableStep(final T t, final U u) {
        return new RunnableStep(this.name, this.desc, () -> this.action.accept(t, u));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t   the first input argument
     * @param u   the second input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code SupplierStep}
     */
    public final <R> SupplierStep<R> asSupplierStep(final T t, final U u, final R r) {
        return new SupplierStep<>(this.name, this.desc, () -> {
            this.action.accept(t, u);
            return r;
        });
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param u the second input argument
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T> asConsumerStep(final U u) {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.accept(t, u));
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @param <V> the type of the third input argument
     * @return {@code TriConsumerStep}
     */
    public final <V> TriConsumerStep<T, U, V> asTriConsumerStep() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.accept(t, u));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param u   the second input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code FunctionStep}
     */
    public final <R> FunctionStep<T, R> asFunctionStep(final U u, final R r) {
        return new FunctionStep<>(this.name, this.desc, t -> {
            this.action.accept(t, u);
            return r;
        });
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code BiFunctionStep}
     */
    public final <R> BiFunctionStep<T, U, R> asBiFunctionStep(final R r) {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> {
            this.action.accept(t, u);
            return r;
        });
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param r   the result
     * @param <V> the type of the third input argument
     * @param <R> the type of the result
     * @return {@code TriFunctionStep}
     */
    public final <R, V> TriFunctionStep<T, U, V, R> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> {
            this.action.accept(t, u);
            return r;
        });
    }

    @Override
    public final String toString() {
        return "BiConsumerStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
