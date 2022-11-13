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

import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.checked.Xteps;

import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithPrefix;

/**
 * TriFunction step. This step will be executed and reported when calling the
 * {@link #apply(Object, Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
public class TriFunctionStep<T, U, V, R, E extends Throwable> implements ThrowingTriFunction<T, U, V, R, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ? extends E> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    public TriFunctionStep(final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ? extends E> step) {
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
    public TriFunctionStep(final String stepName,
                           final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ? extends E> step) {
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
    public TriFunctionStep(final String stepName,
                           final String stepDescription,
                           final ThrowingTriFunction<? super T, ? super U, ? super V, ? extends R, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    @Override
    public final R apply(final T t, final U u, final V v) throws E {
        return Xteps.stepsChain().withContext(v).withContext(u).withContext(t)
            .stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new TriFunctionStep with given prefix in the step name.
     *
     * @param stepNamePrefix the step name prefix
     * @return TriFunctionStep with given prefix in the step name
     */
    public final TriFunctionStep<T, U, V, R, E> withNamePrefix(final String stepNamePrefix) {
        return new TriFunctionStep<>(
            stepNameWithPrefix(stepNamePrefix, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked TriFunctionStep
     */
    @SuppressWarnings("unchecked")
    public final TriFunctionStep<T, U, V, R, RuntimeException> asUnchecked() {
        return (TriFunctionStep<T, U, V, R, RuntimeException>) this;
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return RunnableStep
     */
    public final RunnableStep<E> asRunnableStep(final T t, final U u, final V v) {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return SupplierStep
     */
    public final SupplierStep<R, E> asSupplierStep(final T t, final U u, final V v) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return ConsumerStep
     */
    public final ConsumerStep<T, E> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param v the third input argument
     * @return BiConsumerStep
     */
    public final BiConsumerStep<T, U, E> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @return TriConsumerStep
     */
    public final TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return FunctionStep
     */
    public final FunctionStep<T, R, E> asFunctionStep(final U u, final V v) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param v the third input argument
     * @return BiFunctionStep
     */
    public final BiFunctionStep<T, U, R, E> asBiFunctionStep(final V v) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u, v));
    }

    @Override
    public final String toString() {
        return "TriFunctionStep(" + this.stepName + ")";
    }
}
