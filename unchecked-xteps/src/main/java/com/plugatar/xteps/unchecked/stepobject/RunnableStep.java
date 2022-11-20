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

import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.UncheckedXteps;

import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Runnable step. This step will be executed and reported when calling the {@link #run()} method.
 */
public class RunnableStep implements ThrowingRunnable<RuntimeException> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingRunnable<?> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    protected RunnableStep(final ThrowingRunnable<?> step) {
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
    public RunnableStep(final String stepName,
                        final ThrowingRunnable<?> step) {
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
    public RunnableStep(final String stepName,
                        final String stepDescription,
                        final ThrowingRunnable<?> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Returns dummy RunnableStep.
     *
     * @param stepName the step name
     * @return dummy RunnableStep
     */
    public static RunnableStep dummy(final String stepName) {
        return new RunnableStep(
            stepName, () -> { throw new XtepsException("Step not implemented"); }
        );
    }

    /**
     * Performs and reports this step.
     *
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     */
    @Override
    public final void run() {
        UncheckedXteps.stepsChain().step(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new RunnableStep with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return RunnableStep with given keyword in the step name
     */
    public final RunnableStep withKeyword(final String keyword) {
        return new RunnableStep(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return SupplierStep
     */
    public final <R> SupplierStep<R> asSupplierStep(final R r) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> {
            this.step.run();
            return r;
        });
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param <T> the type of the input argument
     * @return ConsumerStep
     */
    public final <T> ConsumerStep<T> asConsumerStep() {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.run());
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @return BiConsumerStep
     */
    public final <T, U> BiConsumerStep<T, U> asBiConsumerStep() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.run());
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <T, U, V> TriConsumerStep<T, U, V> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.run());
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param r   the result
     * @param <T> the type of the input argument
     * @param <R> the type of the result
     * @return FunctionStep
     */
    public final <T, R> FunctionStep<T, R> asFunctionStep(final R r) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> {
            this.step.run();
            return r;
        });
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param r   the result
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <R> the type of the result
     * @return BiFunctionStep
     */
    public final <T, U, R> BiFunctionStep<T, U, R> asBiFunctionStep(final R r) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> {
            this.step.run();
            return r;
        });
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param r   the result
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @param <R> the type of the result
     * @return TriFunctionStep
     */
    public final <T, U, V, R> TriFunctionStep<T, U, V, R> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> {
            this.step.run();
            return r;
        });
    }

    @Override
    public final String toString() {
        return "RunnableStep(" + this.stepName + ")";
    }
}
