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

import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.UncheckedXteps;

import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithPrefix;

/**
 * Function step. This step will be executed and reported when calling the {@link #apply(Object)} method.
 *
 * @param <T> the type of the input argument
 * @param <R> the type of the result
 */
public class FunctionStep<T, R> implements ThrowingFunction<T, R, RuntimeException> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingFunction<? super T, ? extends R, ?> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    public FunctionStep(final ThrowingFunction<? super T, ? extends R, ?> step) {
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
                        final ThrowingFunction<? super T, ? extends R, ?> step) {
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
                        final ThrowingFunction<? super T, ? extends R, ?> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Performs and reports this step.
     *
     * @param t the input argument
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final R apply(final T t) {
        return UncheckedXteps.stepsChain().withContext(t)
            .stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new FunctionStep with given prefix in the step name.
     *
     * @param stepNamePrefix the step name prefix
     * @return FunctionStep with given prefix in the step name
     */
    public final FunctionStep<T, R> withNamePrefix(final String stepNamePrefix) {
        return new FunctionStep<>(
            stepNameWithPrefix(stepNamePrefix, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the input argument
     * @return RunnableStep
     */
    public final RunnableStep asRunnableStep(final T t) {
        return new RunnableStep(this.stepName, this.stepDescription, () -> this.step.apply(t));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t the input argument
     * @return SupplierStep
     */
    public final SupplierStep<R> asSupplierStep(final T t) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t));
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @return ConsumerStep
     */
    public final ConsumerStep<T> asConsumerStep() {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param <U> the type of the second input argument
     * @return BiConsumerStep
     */
    public final <U> BiConsumerStep<T, U> asBiConsumerStep() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <U, V> TriConsumerStep<T, U, V> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t));
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param <U> the type of the second input argument
     * @return BiFunctionStep
     */
    public final <U> BiFunctionStep<T, U, R> asBiFunctionStep() {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t));
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriFunctionStep
     */
    public final <U, V> TriFunctionStep<T, U, V, R> asTriFunctionStep() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t));
    }

    @Override
    public final String toString() {
        return "FunctionStep(" + this.stepName + ")";
    }
}
