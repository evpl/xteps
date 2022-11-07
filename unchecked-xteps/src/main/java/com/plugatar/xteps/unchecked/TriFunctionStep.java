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
package com.plugatar.xteps.unchecked;

import com.plugatar.xteps.base.ThrowingTriFunction;

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
    private final String stepName;
    private final String stepDescription;
    private final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> step;

    /**
     * Ctor.
     *
     * @param stepName the step name
     * @param step     the step
     */
    public TriFunctionStep(final String stepName,
                           final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> step) {
        this(stepName, "", step);
    }

    /**
     * Ctor.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     */
    public TriFunctionStep(final String stepName,
                           final String stepDescription,
                           final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ?> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    @Override
    public final R apply(final T t, final U u, final V v) {
        return UncheckedXteps.stepsChain().withContext(v).withContext(u).withContext(t)
            .stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return RunnableStep
     */
    public final RunnableStep asRunnableStep(final T t, final U u, final V v) {
        return new RunnableStep(this.stepName, this.stepDescription, () -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return SupplierStep
     */
    public final SupplierStep<R> asSupplierStep(final T t, final U u, final V v) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return ConsumerStep
     */
    public final ConsumerStep<T> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param v the third input argument
     * @return BiConsumerStep
     */
    public final BiConsumerStep<T, U> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @return TriConsumerStep
     */
    public final TriConsumerStep<T, U, V> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return FunctionStep
     */
    public final FunctionStep<T, R> asFunctionStep(final U u, final V v) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param v the third input argument
     * @return BiFunctionStep
     */
    public final BiFunctionStep<T, U, R> asBiFunctionStep(final V v) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u, v));
    }

    @Override
    public final String toString() {
        return "TriFunctionStep(" + this.stepName + ")";
    }
}
