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

import com.plugatar.xteps.base.ThrowingTriConsumer;

/**
 * TriConsumer step. This step will be executed and reported when calling the
 * {@link #accept(Object, Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 */
public class TriConsumerStep<T, U, V> implements ThrowingTriConsumer<T, U, V, RuntimeException> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> step;

    /**
     * Ctor.
     *
     * @param stepName the step name
     * @param step     the step
     */
    public TriConsumerStep(final String stepName,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> step) {
        this(stepName, "", step);
    }

    /**
     * Ctor.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     */
    public TriConsumerStep(final String stepName,
                           final String stepDescription,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    @Override
    public final void accept(final T t, final U u, final V v) {
        UncheckedXteps.stepsChain().withContext(v).withContext(u).withContext(t)
            .step(this.stepName, this.stepDescription, this.step);
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
        return new RunnableStep(this.stepName, this.stepDescription, () -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t   the first input argument
     * @param u   the second input argument
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return SupplierStep
     */
    public final <R> SupplierStep<R> asSupplierStep(final T t, final U u, final V v, final R r) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return ConsumerStep
     */
    public final ConsumerStep<T> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param v the third input argument
     * @return BiConsumerStep
     */
    public final BiConsumerStep<T, U> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param u   the second input argument
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return FunctionStep
     */
    public final <R> FunctionStep<T, R> asFunctionStep(final U u, final V v, final R r) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return BiFunctionStep
     */
    public final <R> BiFunctionStep<T, U, R> asBiFunctionStep(final V v, final R r) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return TriFunctionStep
     */
    public final <R> TriFunctionStep<T, U, V, R> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    @Override
    public final String toString() {
        return "TriConsumerStep(" + this.stepName + ")";
    }
}
