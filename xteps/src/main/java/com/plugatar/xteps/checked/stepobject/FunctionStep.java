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
import com.plugatar.xteps.checked.Xteps;

import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithPrefix;

/**
 * Function step. This step will be executed and reported when calling the {@link #apply(Object)} method.
 *
 * @param <T> the type of the input argument
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
public class FunctionStep<T, R, E extends Throwable> implements ThrowingFunction<T, R, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingFunction<? super T, ? extends R, ? extends E> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    public FunctionStep(final ThrowingFunction<? super T, ? extends R, ? extends E> step) {
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
    public FunctionStep(final String stepName,
                        final ThrowingFunction<? super T, ? extends R, ? extends E> step) {
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
    public FunctionStep(final String stepName,
                        final String stepDescription,
                        final ThrowingFunction<? super T, ? extends R, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    @Override
    public final R apply(final T t) throws E {
        return Xteps.stepsChain().withContext(t)
            .stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new FunctionStep with given prefix in the step name.
     *
     * @param stepNamePrefix the step name prefix
     * @return FunctionStep with given prefix in the step name
     */
    public final FunctionStep<T, R, E> withNamePrefix(final String stepNamePrefix) {
        return new FunctionStep<>(
            stepNameWithPrefix(stepNamePrefix, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked FunctionStep
     */
    @SuppressWarnings("unchecked")
    public final FunctionStep<T, R, RuntimeException> asUnchecked() {
        return (FunctionStep<T, R, RuntimeException>) this;
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the input argument
     * @return RunnableStep
     */
    public final RunnableStep<E> asRunnableStep(final T t) {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t the input argument
     * @return SupplierStep
     */
    public final SupplierStep<R, E> asSupplierStep(final T t) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t));
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @return ConsumerStep
     */
    public final ConsumerStep<T, E> asConsumerStep() {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param <U> the type of the second input argument
     * @return BiConsumerStep
     */
    public final <U> BiConsumerStep<T, U, E> asBiConsumerStep() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <U, V> TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t));
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param <U> the type of the second input argument
     * @return BiFunctionStep
     */
    public final <U> BiFunctionStep<T, U, R, E> asBiFunctionStep() {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t));
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriFunctionStep
     */
    public final <U, V> TriFunctionStep<T, U, V, R, E> asTriFunctionStep() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t));
    }

    @Override
    public final String toString() {
        return "FunctionStep(" + this.stepName + ")";
    }
}
