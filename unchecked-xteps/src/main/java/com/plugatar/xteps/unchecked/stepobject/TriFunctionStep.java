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

import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.unchecked.UncheckedXteps.stepsChainOf;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * TriFunction step. This step will be executed and reported when calling the
 * {@link #apply(Object, Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 * @param <R> the type of the result
 */
public class TriFunctionStep<T, U, V, R> implements ThrowingTriFunction<T, U, V, R, RuntimeException> {

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
    private final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public TriFunctionStep(
        final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> action
    ) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(TriFunctionStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public TriFunctionStep(
        final String name,
        final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> action
    ) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public TriFunctionStep(
        final String name,
        final String desc,
        final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> action
    ) {
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
    public TriFunctionStep(
        final String keyword,
        final String name,
        final String desc,
        final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> action
    ) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code TriFunctionStep}.
     *
     * @param name the step name
     * @param <T>  the type of the first input argument
     * @param <U>  the type of the second input argument
     * @param <V>  the type of the third input argument
     * @param <R>  the type of the result
     * @return dummy {@code TriFunctionStep}
     */
    public static <T, U, V, R> TriFunctionStep<T, U, V, R> dummy(final String name) {
        return new TriFunctionStep<>(name, (t, u, v) -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final R apply(final T t, final U u, final V v) {
        return stepsChainOf(t, u, v).stepTo(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code TriFunctionStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code TriFunctionStep} with given keyword in the step name
     */
    public final TriFunctionStep<T, U, V, R> withKeyword(final String keyword) {
        return new TriFunctionStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as {@code TriFunctionStep<T, V, U, R>}.
     *
     * @return {@code TriFunctionStep<T, V, U, R>}
     */
    public final TriFunctionStep<T, V, U, R> asTVU() {
        return new TriFunctionStep<>(this.name, this.desc, (t, v, u) -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<U, T, V, R>}.
     *
     * @return {@code TriFunctionStep<U, T, V, R>}
     */
    public final TriFunctionStep<U, T, V, R> asUTV() {
        return new TriFunctionStep<>(this.name, this.desc, (u, t, v) -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<U, V, T, R>}.
     *
     * @return {@code TriFunctionStep<U, V, T, R>}
     */
    public final TriFunctionStep<U, V, T, R> asUVT() {
        return new TriFunctionStep<>(this.name, this.desc, (u, v, t) -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<V, T, U, R>}.
     *
     * @return {@code TriFunctionStep<V, T, U, R>}
     */
    public final TriFunctionStep<V, T, U, R> asVTU() {
        return new TriFunctionStep<>(this.name, this.desc, (v, t, u) -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<V, U, T, R>}.
     *
     * @return {@code TriFunctionStep<V, U, T, R>}
     */
    public final TriFunctionStep<V, U, T, R> asVUT() {
        return new TriFunctionStep<>(this.name, this.desc, (v, u, t) -> this.action.apply(t, u, v));
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
        return new RunnableStep(this.name, this.desc, () -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code SupplierStep}
     */
    public final SupplierStep<R> asSupplierStep(final T t, final U u, final V v) {
        return new SupplierStep<>(this.name, this.desc, () -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param v the third input argument
     * @return {@code BiConsumerStep}
     */
    public final BiConsumerStep<T, U> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @return {@code TriConsumerStep}
     */
    public final TriConsumerStep<T, U, V> asTriConsumerStep() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code FunctionStep}
     */
    public final FunctionStep<T, R> asFunctionStep(final U u, final V v) {
        return new FunctionStep<>(this.name, this.desc, t -> this.action.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param v the third input argument
     * @return {@code BiFunctionStep}
     */
    public final BiFunctionStep<T, U, R> asBiFunctionStep(final V v) {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> this.action.apply(t, u, v));
    }

    @Override
    public final String toString() {
        return "TriFunctionStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
