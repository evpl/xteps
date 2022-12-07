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

import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.unchecked.UncheckedXteps.stepsChainOf;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * BiFunction step. This step will be executed and reported when calling the
 * {@link #apply(Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <R> the type of the result
 */
public class BiFunctionStep<T, U, R> implements ThrowingBiFunction<T, U, R, RuntimeException> {

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
    private final ThrowingBiFunction<? super T, ? super U, ? extends R, ?> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public BiFunctionStep(final ThrowingBiFunction<? super T, ? super U, ? extends R, ?> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(BiFunctionStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public BiFunctionStep(final String name,
                          final ThrowingBiFunction<? super T, ? super U, ? extends R, ?> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public BiFunctionStep(final String name,
                          final String desc,
                          final ThrowingBiFunction<? super T, ? super U, ? extends R, ?> action) {
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
    public BiFunctionStep(final String keyword,
                          final String name,
                          final String desc,
                          final ThrowingBiFunction<? super T, ? super U, ? extends R, ?> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code BiFunctionStep}.
     *
     * @param name the step name
     * @param <T>  the type of the first input argument
     * @param <U>  the type of the second input argument
     * @param <R>  the type of the result
     * @return dummy {@code BiFunctionStep}
     */
    public static <T, U, R> BiFunctionStep<T, U, R> dummy(final String name) {
        return new BiFunctionStep<>(name, (t, u) -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final R apply(final T t, final U u) {
        return stepsChainOf(t, u).stepTo(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code BiFunctionStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code BiFunctionStep} with given keyword in the step name
     */
    public final BiFunctionStep<T, U, R> withKeyword(final String keyword) {
        return new BiFunctionStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as {@code BiFunctionStep<U, T, R>}.
     *
     * @return {@code BiFunctionStep<U, T, R>}
     */
    public final BiFunctionStep<U, T, R> asUT() {
        return new BiFunctionStep<>(this.name, this.desc, (u, t) -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep asRunnableStep(final T t, final U u) {
        return new RunnableStep(this.name, this.desc, () -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code SupplierStep}
     */
    public final SupplierStep<R> asSupplierStep(final T t, final U u) {
        return new SupplierStep<>(this.name, this.desc, () -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param u the second input argument
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T> asConsumerStep(final U u) {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @return {@code BiConsumerStep}
     */
    public final BiConsumerStep<T, U> asBiConsumerStep() {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @param <V> the type of the third input argument
     * @return {@code TriConsumerStep}
     */
    public final <V> TriConsumerStep<T, U, V> asTriConsumer() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param u the second input argument
     * @return {@code FunctionStep}
     */
    public final FunctionStep<T, R> asFunctionStep(final U u) {
        return new FunctionStep<>(this.name, this.desc, t -> this.action.apply(t, u));
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param <V> the type of the third input argument
     * @return {@code TriFunctionStep}
     */
    public final <V> TriFunctionStep<T, U, V, R> asTriFunctionStep() {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> this.action.apply(t, u));
    }

    @Override
    public final String toString() {
        return "BiFunctionStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
