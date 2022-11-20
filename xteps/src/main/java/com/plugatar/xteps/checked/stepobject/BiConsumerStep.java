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

import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.Xteps;

import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * BiConsumer step. This step will be executed and reported when calling the {@link #accept(Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <E> the type of the throwing exception
 */
public class BiConsumerStep<T, U, E extends Throwable> implements ThrowingBiConsumer<T, U, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingBiConsumer<? super T, ? super U, ? extends E> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    protected BiConsumerStep(final ThrowingBiConsumer<? super T, ? super U, ? extends E> step) {
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
    public BiConsumerStep(final String stepName,
                          final ThrowingBiConsumer<? super T, ? super U, ? extends E> step) {
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
    public BiConsumerStep(final String stepName,
                          final String stepDescription,
                          final ThrowingBiConsumer<? super T, ? super U, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Returns dummy BiConsumerStep.
     *
     * @param stepName the step name
     * @param <T>      the type of the first input argument
     * @param <U>      the type of the second input argument
     * @return dummy BiConsumerStep
     */
    public static <T, U> BiConsumerStep<T, U, RuntimeException> dummy(final String stepName) {
        return new BiConsumerStep<>(
            stepName, (t, u) -> { throw new XtepsException("Step not implemented"); }
        );
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final void accept(final T t, final U u) throws E {
        Xteps.stepsChain().withContext(u).withContext(t)
            .step(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new BiConsumerStep with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return BiConsumerStep with given keyword in the step name
     */
    public final BiConsumerStep<T, U, E> withKeyword(final String keyword) {
        return new BiConsumerStep<>(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked BiConsumerStep
     */
    @SuppressWarnings("unchecked")
    public final BiConsumerStep<T, U, RuntimeException> asUnchecked() {
        return (BiConsumerStep<T, U, RuntimeException>) this;
    }

    /**
     * Returns this step as {@code BiConsumerStep<U, T, E>}.
     *
     * @return {@code BiConsumerStep<U, T, E>}
     */
    public final BiConsumerStep<U, T, E> asUT() {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (u, t) -> this.step.accept(t, u));
    }

    /**
     * Returns this step as a RunnableStep.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return RunnableStep
     */
    public final RunnableStep<E> asRunnableStep(final T t, final U u) {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.accept(t, u));
    }

    /**
     * Returns this step as a SupplierStep.
     *
     * @param t   the first input argument
     * @param u   the second input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return SupplierStep
     */
    public final <R> SupplierStep<R, E> asSupplierStep(final T t, final U u, final R r) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> {
            this.step.accept(t, u);
            return r;
        });
    }

    /**
     * Returns this step as a ConsumerStep.
     *
     * @param u the second input argument
     * @return ConsumerStep
     */
    public final ConsumerStep<T, E> asConsumerStep(final U u) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.accept(t, u));
    }

    /**
     * Returns this step as a TriConsumerStep.
     *
     * @param <V> the type of the third input argument
     * @return TriConsumerStep
     */
    public final <V> TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.accept(t, u));
    }

    /**
     * Returns this step as a FunctionStep.
     *
     * @param u   the second input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return FunctionStep
     */
    public final <R> FunctionStep<T, R, E> asFunctionStep(final U u, final R r) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> {
            this.step.accept(t, u);
            return r;
        });
    }

    /**
     * Returns this step as a BiFunctionStep.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return BiFunctionStep
     */
    public final <R> BiFunctionStep<T, U, R, E> asBiFunctionStep(final R r) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> {
            this.step.accept(t, u);
            return r;
        });
    }

    /**
     * Returns this step as a TriFunctionStep.
     *
     * @param r   the result
     * @param <V> the type of the third input argument
     * @param <R> the type of the result
     * @return TriFunctionStep
     */
    public final <R, V> TriFunctionStep<T, U, V, R, E> asTriFunctionStep(final R r) {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, u, v) -> {
            this.step.accept(t, u);
            return r;
        });
    }

    @Override
    public final String toString() {
        return "BiConsumerStep(" + this.stepName + ")";
    }
}
