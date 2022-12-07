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

import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.checked.Xteps.stepsChainOf;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Function step. This step will be executed and reported when calling the
 * {@link #apply(Object)} method.
 *
 * @param <T> the type of the input argument
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
public class FunctionStep<T, R, E extends Throwable> implements ThrowingFunction<T, R, E> {

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
    private final ThrowingFunction<? super T, ? extends R, ? extends E> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public FunctionStep(final ThrowingFunction<? super T, ? extends R, ? extends E> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(FunctionStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public FunctionStep(final String name,
                        final ThrowingFunction<? super T, ? extends R, ? extends E> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public FunctionStep(final String name,
                        final String desc,
                        final ThrowingFunction<? super T, ? extends R, ? extends E> action) {
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
    public FunctionStep(final String keyword,
                        final String name,
                        final String desc,
                        final ThrowingFunction<? super T, ? extends R, ? extends E> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code FunctionStep}.
     *
     * @param name the step name
     * @param <T>  the type of the input argument
     * @param <R>  the type of the result
     * @return dummy {@code FunctionStep}
     */
    public static <T, R> FunctionStep<T, R, RuntimeException> dummy(final String name) {
        return new FunctionStep<>(name, t -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @param t the input argument
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final R apply(final T t) throws E {
        return stepsChainOf(t).stepTo(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code FunctionStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code FunctionStep} with given keyword in the step name
     */
    public final FunctionStep<T, R, E> withKeyword(final String keyword) {
        return new FunctionStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked {@code FunctionStep}
     */
    @SuppressWarnings("unchecked")
    public final FunctionStep<T, R, RuntimeException> asUnchecked() {
        return (FunctionStep<T, R, RuntimeException>) this;
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep<E> asRunnableStep(final T t) {
        return new RunnableStep<>(this.name, this.desc, () -> this.action.apply(t));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t the input argument
     * @return {@code SupplierStep}
     */
    public final SupplierStep<R, E> asSupplierStep(final T t) {
        return new SupplierStep<>(this.name, this.desc, () -> this.action.apply(t));
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T, E> asConsumerStep() {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.apply(t));
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param <U> the type of the second input argument
     * @return {@code BiConsumerStep}
     */
    public final <U> BiConsumerStep<T, U, E> asBiConsumerStep() {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.apply(t));
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return {@code TriConsumerStep}
     */
    public final <U, V> TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.apply(t));
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param <U> the type of the second input argument
     * @return {@code BiFunctionStep}
     */
    public final <U> BiFunctionStep<T, U, R, E> asBiFunctionStep() {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> this.action.apply(t));
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return {@code TriFunctionStep}
     */
    public final <U, V> TriFunctionStep<T, U, V, R, E> asTriFunctionStep() {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> this.action.apply(t));
    }

    @Override
    public final String toString() {
        return "FunctionStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
