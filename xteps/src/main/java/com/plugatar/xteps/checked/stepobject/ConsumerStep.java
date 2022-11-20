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

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.Xteps;

import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * Consumer step. This step will be executed and reported when calling the {@link #accept(Object)} method.
 *
 * @param <T> the type of the input argument
 * @param <E> the type of the throwing exception
 */
public class ConsumerStep<T, E extends Throwable> implements ThrowingConsumer<T, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingConsumer<? super T, ? extends E> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    protected ConsumerStep(final ThrowingConsumer<? super T, ? extends E> step) {
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
    public ConsumerStep(final String stepName,
                        final ThrowingConsumer<? super T, ? extends E> step) {
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
    public ConsumerStep(final String stepName,
                        final String stepDescription,
                        final ThrowingConsumer<? super T, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Returns dummy ConsumerStep.
     *
     * @param stepName the step name
     * @param <T>      the type of the input argument
     * @return dummy ConsumerStep
     */
    public static <T> ConsumerStep<T, RuntimeException> dummy(final String stepName) {
        return new ConsumerStep<>(
            stepName, t -> { throw new XtepsException("Step not implemented"); }
        );
    }

    /**
     * Performs and reports this step.
     *
     * @param t the input argument
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final void accept(final T t) throws E {
        Xteps.stepsChain().withContext(t)
            .step(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new ConsumerStep with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return ConsumerStep with given keyword in the step name
     */
    public final ConsumerStep<T, E> withKeyword(final String keyword) {
        return new ConsumerStep<>(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked ConsumerStep
     */
    @SuppressWarnings("unchecked")
    public final ConsumerStep<T, RuntimeException> asUnchecked() {
        return (ConsumerStep<T, RuntimeException>) this;
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the input argument
     * @return RunnableStep
     */
    public final RunnableStep<E> asRunnableStep(final T t) {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.accept(t));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t   the input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return SupplierStep
     */
    public final <R> SupplierStep<R, E> asSupplierStep(final T t, final R r) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> {
            this.step.accept(t);
            return r;
        });
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param <U> the type of the second input argument
     * @return BiConsumerStep
     */
    public final <U> BiConsumerStep<T, U, E> asBiConsumer() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.accept(t));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <U, V> TriConsumerStep<T, U, V, E> asTriConsumer() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.accept(t));
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return FunctionStep
     */
    public final <R> FunctionStep<T, R, E> asFunctionStep(final R r) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> {
            this.step.accept(t);
            return r;
        });
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param r   the result
     * @param <U> the type of the second input argument
     * @param <R> the type of the result
     * @return BiFunctionStep
     */
    public final <U, R> BiFunctionStep<T, U, R, E> asBiFunctionStep(final R r) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> {
            this.step.accept(t);
            return r;
        });
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param r   the result
     * @param <U> the type of the second input argument
     * @param <V> the type of the third input argument
     * @param <R> the type of the result
     * @return TriFunctionStep
     */
    public final <U, V, R> TriFunctionStep<T, U, V, R, E> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> {
            this.step.accept(t);
            return r;
        });
    }

    @Override
    public final String toString() {
        return "ConsumerStep(" + this.stepName + ")";
    }
}
