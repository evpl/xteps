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
import com.plugatar.xteps.unchecked.UncheckedXteps;

import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Supplier step. This step will be executed and reported when calling the {@link #get()} method.
 *
 * @param <R> the type of the result
 */
public class SupplierStep<R> implements ThrowingSupplier<R, RuntimeException> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingSupplier<? extends R, ?> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    protected SupplierStep(final ThrowingSupplier<? extends R, ?> step) {
        this.stepName = humanReadableStepNameOfClass(this.getClass());
        this.stepDescription = "";
        this.step = step;
    }

    /**
     * Ctor.
     *
     * @param stepName the step name
     * @param step     the step
     */
    public SupplierStep(final String stepName,
                        final ThrowingSupplier<? extends R, ?> step) {
        this.stepName = stepName;
        this.stepDescription = "";
        this.step = step;
    }

    /**
     * Ctor.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     */
    public SupplierStep(final String stepName,
                        final String stepDescription,
                        final ThrowingSupplier<? extends R, ?> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Returns dummy {@code SupplierStep}.
     *
     * @param stepName the step name
     * @param <R>      the type of the result
     * @return dummy {@code SupplierStep}
     */
    public static <R> SupplierStep<R> dummy(final String stepName) {
        return new SupplierStep<>(
            stepName, () -> { throw new XtepsException("Step not implemented"); }
        );
    }

    /**
     * Performs and reports this step.
     *
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final R get() {
        return UncheckedXteps.stepsChain().stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new {@code SupplierStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code SupplierStep} with given keyword in the step name
     */
    public final SupplierStep<R> withKeyword(final String keyword) {
        return new SupplierStep<>(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @return {@code RunnableStep}
     */
    public final RunnableStep asRunnableStep() {
        return new RunnableStep(this.stepName, this.stepDescription, () -> this.step.get());
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param <T> the type of the input argument
     * @return {@code ConsumerStep}
     */
    public final <T> ConsumerStep<T> asConsumerStep() {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.get());
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return {@code BiConsumerStep}
     */
    public final <T, U> BiConsumerStep<T, U> asBiConsumerStep() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.get());
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
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.get());
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param <T> the type of the input argument
     * @return {@code FunctionStep}
     */
    public final <T> FunctionStep<T, R> asFunctionStep() {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> this.step.get());
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return {@code BiFunctionStep}
     */
    public final <T, U> BiFunctionStep<T, U, R> asBiFunctionStep() {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.get());
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
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.get());
    }

    @Override
    public final String toString() {
        return "SupplierStep(" + this.stepName + ")";
    }
}
