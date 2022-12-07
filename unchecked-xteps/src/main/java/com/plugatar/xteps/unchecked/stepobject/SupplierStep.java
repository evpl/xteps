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

import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.unchecked.UncheckedXteps.stepTo;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Supplier step. This step will be executed and reported when calling the
 * {@link #get()} method.
 *
 * @param <R> the type of the result
 */
public class SupplierStep<R> implements ThrowingSupplier<R, RuntimeException> {

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
    private final ThrowingSupplier<? extends R, ?> action;

    /**
     * Ctor.
     *
     * @param action the step action
     */
    public SupplierStep(final ThrowingSupplier<? extends R, ?> action) {
        this.keyword = "";
        this.name = humanReadableOrEmptyStepName(SupplierStep.class, this.getClass());
        this.desc = "";
        this.action = action;
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param action the step action
     */
    public SupplierStep(final String name,
                        final ThrowingSupplier<? extends R, ?> action) {
        this("", name, "", action);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     */
    public SupplierStep(final String name,
                        final String desc,
                        final ThrowingSupplier<? extends R, ?> action) {
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
    public SupplierStep(final String keyword,
                        final String name,
                        final String desc,
                        final ThrowingSupplier<? extends R, ?> action) {
        this.keyword = keyword;
        this.name = name;
        this.desc = desc;
        this.action = action;
    }

    /**
     * Returns dummy {@code SupplierStep}.
     *
     * @param name the step name
     * @param <R>  the type of the result
     * @return dummy {@code SupplierStep}
     */
    public static <R> SupplierStep<R> dummy(final String name) {
        return new SupplierStep<>(name, () -> { throw new XtepsException("Step not implemented"); });
    }

    /**
     * Performs and reports this step.
     *
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #keyword} or {@link #name} or {@link #desc} or {@link #action} is null
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final R get() {
        return stepTo(stepNameWithKeyword(this.keyword, this.name), this.desc, this.action);
    }

    /**
     * Returns a new {@code SupplierStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code SupplierStep} with given keyword in the step name
     */
    public final SupplierStep<R> withKeyword(final String keyword) {
        return new SupplierStep<>(keyword, this.name, this.desc, this.action);
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @return {@code RunnableStep}
     */
    public final RunnableStep asRunnableStep() {
        return new RunnableStep(this.name, this.desc, () -> this.action.get());
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param <T> the type of the input argument
     * @return {@code ConsumerStep}
     */
    public final <T> ConsumerStep<T> asConsumerStep() {
        return new ConsumerStep<>(this.name, this.desc, t -> this.action.get());
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return {@code BiConsumerStep}
     */
    public final <T, U> BiConsumerStep<T, U> asBiConsumerStep() {
        return new BiConsumerStep<>(this.name, this.desc, (t, u) -> this.action.get());
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return {@code TriConsumerStep}
     */
    public final <T, U, V> TriConsumerStep<T, U, V> asTriConsumerStep() {
        return new TriConsumerStep<>(this.name, this.desc, (t, u, v) -> this.action.get());
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param <T> the type of the input argument
     * @return {@code FunctionStep}
     */
    public final <T> FunctionStep<T, R> asFunctionStep() {
        return new FunctionStep<>(this.name, this.desc, t -> this.action.get());
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return {@code BiFunctionStep}
     */
    public final <T, U> BiFunctionStep<T, U, R> asBiFunctionStep() {
        return new BiFunctionStep<>(this.name, this.desc, (t, u) -> this.action.get());
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return {@code TriFunctionStep}
     */
    public final <T, U, V> TriFunctionStep<T, U, V, R> asTriFunctionStep() {
        return new TriFunctionStep<>(this.name, this.desc, (t, u, v) -> this.action.get());
    }

    @Override
    public final String toString() {
        return "SupplierStep(" + stepNameWithKeyword(this.keyword, this.name) + ")";
    }
}
