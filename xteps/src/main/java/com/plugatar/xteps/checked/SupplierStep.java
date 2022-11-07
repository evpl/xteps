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
package com.plugatar.xteps.checked;

import com.plugatar.xteps.base.ThrowingSupplier;

/**
 * Supplier step. This step will be executed and reported when calling the {@link #get()} method.
 *
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
public class SupplierStep<R, E extends Throwable> implements ThrowingSupplier<R, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingSupplier<? extends R, ? extends E> step;

    /**
     * Ctor.
     *
     * @param stepName the step name
     * @param step     the step
     */
    public SupplierStep(final String stepName,
                        final ThrowingSupplier<? extends R, ? extends E> step) {
        this(stepName, "", step);
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
                        final ThrowingSupplier<? extends R, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    @Override
    public final R get() throws E {
        return Xteps.stepsChain().stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked SupplierStep
     */
    @SuppressWarnings("unchecked")
    public final SupplierStep<R, RuntimeException> asUnchecked() {
        return (SupplierStep<R, RuntimeException>) this;
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @return RunnableStep
     */
    public final RunnableStep<E> asRunnableStep() {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.get());
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param <T> the type of the input argument
     * @return ConsumerStep
     */
    public final <T> ConsumerStep<T, E> asConsumerStep() {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.get());
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return BiConsumerStep
     */
    public final <T, U> BiConsumerStep<T, U, E> asBiConsumerStep() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.get());
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <T, U, V> TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.get());
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param <T> the type of the input argument
     * @return FunctionStep
     */
    public final <T> FunctionStep<T, R, E> asFunctionStep() {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> this.step.get());
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return BiFunctionStep
     */
    public final <T, U> BiFunctionStep<T, U, R, E> asBiFunctionStep() {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.get());
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriFunctionStep
     */
    public final <T, U, V> TriFunctionStep<T, U, V, R, E> asTriFunctionStep() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.get());
    }

    @Override
    public final String toString() {
        return "SupplierStep(" + this.stepName + ")";
    }
}