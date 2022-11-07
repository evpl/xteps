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

import com.plugatar.xteps.base.ThrowingBiFunction;

/**
 * BiFunction step. This step will be executed and reported when calling the {@link #apply(Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
public class BiFunctionStep<T, U, R, E extends Throwable> implements ThrowingBiFunction<T, U, R, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingBiFunction<? super T, ? super U, ? extends R, ? extends E> step;

    /**
     * Ctor.
     *
     * @param stepName the step name
     * @param step     the step
     */
    public BiFunctionStep(final String stepName,
                          final ThrowingBiFunction<? super T, ? super U, ? extends R, ? extends E> step) {
        this(stepName, "", step);
    }

    /**
     * Ctor.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     */
    public BiFunctionStep(final String stepName,
                          final String stepDescription,
                          final ThrowingBiFunction<? super T, ? super U, ? extends R, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    @Override
    public final R apply(final T t, final U u) throws E {
        return Xteps.stepsChain().withContext(u).withContext(t)
            .stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked BiFunctionStep
     */
    @SuppressWarnings("unchecked")
    public final BiFunctionStep<T, U, R, RuntimeException> asUnchecked() {
        return (BiFunctionStep<T, U, R, RuntimeException>) this;
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return RunnableStep
     */
    public final RunnableStep<E> asRunnableStep(final T t, final U u) {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return SupplierStep
     */
    public final SupplierStep<R, E> asSupplierStep(final T t, final U u) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u));
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param u the second input argument
     * @return ConsumerStep
     */
    public final ConsumerStep<T, E> asConsumerStep(final U u) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @return BiConsumerStep
     */
    public final BiConsumerStep<T, U, E> asBiConsumerStep() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <V> TriConsumerStep<T, U, V, E> asTriConsumer() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t, u));
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param u the second input argument
     * @return FunctionStep
     */
    public final FunctionStep<T, R, E> asFunctionStep(final U u) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u));
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param <V> the type of the third input argument
     * @return TriFunctionStep
     */
    public final <V> TriFunctionStep<T, U, V, R, E> asTriFunctionStep() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t, u));
    }

    @Override
    public final String toString() {
        return "BiFunctionStep(" + this.stepName + ")";
    }
}
