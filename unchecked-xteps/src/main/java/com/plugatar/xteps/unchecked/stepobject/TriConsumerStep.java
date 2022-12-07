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

import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.unchecked.UncheckedXteps.stepsChainOf;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * TriConsumer step. This step will be executed and reported when calling the
 * {@link #accept(Object, Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 */
public class TriConsumerStep<T, U, V> implements ThrowingTriConsumer<T, U, V, RuntimeException> {

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
    private final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public TriConsumerStep(final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(TriConsumerStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public TriConsumerStep(final String name,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public TriConsumerStep(final String name,
                           final String desc,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> action) {
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
    public TriConsumerStep(final String keyword,
                           final String name,
                           final String desc,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code TriConsumerStep}.
     *
     * @param name the step name
     * @param <T>  the type of the first input argument
     * @param <U>  the type of the second input argument
     * @param <V>  the type of the third input argument
     * @return dummy {@code TriConsumerStep}
     */
    public static <T, U, V> TriConsumerStep<T, U, V> dummy(final String name) {
        return new TriConsumerStep<>(name, (t, u, v) -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final void accept(final T t, final U u, final V v) {
        stepsChainOf(t, u, v).step(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code TriConsumerStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code TriConsumerStep} with given keyword in the step name
     */
    public final TriConsumerStep<T, U, V> withKeyword(final String keyword) {
        return new TriConsumerStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as {@code TriConsumerStep<T, V, U>}.
     *
     * @return {@code TriConsumerStep<T, V, U>}
     */
    public final TriConsumerStep<T, V, U> asTVU() {
        return new TriConsumerStep<>(this.name, this.desc, (t, v, u) -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<U, T, V>}.
     *
     * @return {@code TriConsumerStep<U, T, V>}
     */
    public final TriConsumerStep<U, T, V> asUTV() {
        return new TriConsumerStep<>(this.name, this.desc, (u, t, v) -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<U, V, T>}.
     *
     * @return {@code TriConsumerStep<U, V, T>}
     */
    public final TriConsumerStep<U, V, T> asUVT() {
        return new TriConsumerStep<>(this.name, this.desc, (u, v, t) -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<V, T, U>}.
     *
     * @return {@code TriConsumerStep<V, T, U>}
     */
    public final TriConsumerStep<V, T, U> asVTU() {
        return new TriConsumerStep<>(this.name, this.desc, (v, t, u) -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<V, U, T>}.
     *
     * @return {@code TriConsumerStep<V, U, T>}
     */
    public final TriConsumerStep<V, U, T> asVUT() {
        return new TriConsumerStep<>(this.name, this.desc, (v, u, t) -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep asRunnableStep(final T t, final U u, final V v) {
        return new RunnableStep(this.name, this.desc, () -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t   the first input argument
     * @param u   the second input argument
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code SupplierStep}
     */
    public final <R> SupplierStep<R> asSupplierStep(final T t, final U u, final V v, final R r) {
        return new SupplierStep<>(this.name, this.desc, () -> {
            this.action.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param v the third input argument
     * @return {@code BiConsumerStep}
     */
    public final BiConsumerStep<T, U> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param u   the second input argument
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code FunctionStep}
     */
    public final <R> FunctionStep<T, R> asFunctionStep(final U u, final V v, final R r) {
        return new FunctionStep<>(this.name, this.desc, t -> {
            this.action.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code BiFunctionStep}
     */
    public final <R> BiFunctionStep<T, U, R> asBiFunctionStep(final V v, final R r) {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> {
            this.action.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code TriFunctionStep}
     */
    public final <R> TriFunctionStep<T, U, V, R> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> {
            this.action.accept(t, u, v);
            return r;
        });
    }

    @Override
    public final String toString() {
        return "TriConsumerStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
