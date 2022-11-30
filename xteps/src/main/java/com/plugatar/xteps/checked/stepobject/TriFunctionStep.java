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
import com.plugatar.xteps.base.XtepsException;

import static com.plugatar.xteps.checked.Xteps.stepsChainOf;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableOrEmptyStepName;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

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
        this.stepName = humanReadableOrEmptyStepName(TriFunctionStep.class, this.getClass());
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

    /**
     * Returns dummy {@code TriFunctionStep}.
     *
     * @param stepName the step name
     * @param <T>      the type of the first input argument
     * @param <U>      the type of the second input argument
     * @param <V>      the type of the third input argument
     * @param <R>      the type of the result
     * @return dummy {@code TriFunctionStep}
     */
    public static <T, U, V, R> TriFunctionStep<T, U, V, R, RuntimeException> dummy(final String stepName) {
        return new TriFunctionStep<>(
            stepName, (t, u, v) -> { throw new XtepsException("Step not implemented"); }
        );
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return the result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@link #stepName} or {@link #stepDescription} or {@link #step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final R apply(final T t, final U u, final V v) throws E {
        return stepsChainOf(t, u, v).stepTo(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new {@code TriFunctionStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code TriFunctionStep} with given keyword in the step name
     */
    public final TriFunctionStep<T, U, V, R, E> withKeyword(final String keyword) {
        return new TriFunctionStep<>(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked {@code TriFunctionStep}
     */
    @SuppressWarnings("unchecked")
    public final TriFunctionStep<T, U, V, R, RuntimeException> asUnchecked() {
        return (TriFunctionStep<T, U, V, R, RuntimeException>) this;
    }

    /**
     * Returns this step as {@code TriFunctionStep<T, V, U, R, E>}.
     *
     * @return {@code TriFunctionStep<T, V, U, R, E>}
     */
    public final TriFunctionStep<T, V, U, R, E> asTVU() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (t, v, u) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<U, T, V, R, E>}.
     *
     * @return {@code TriFunctionStep<U, T, V, R, E>}
     */
    public final TriFunctionStep<U, T, V, R, E> asUTV() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (u, t, v) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<U, V, T, R, E>}.
     *
     * @return {@code TriFunctionStep<U, V, T, R, E>}
     */
    public final TriFunctionStep<U, V, T, R, E> asUVT() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (u, v, t) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<V, T, U, R, E>}.
     *
     * @return {@code TriFunctionStep<V, T, U, R, E>}
     */
    public final TriFunctionStep<V, T, U, R, E> asVTU() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (v, t, u) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as {@code TriFunctionStep<V, U, T, R, E>}.
     *
     * @return {@code TriFunctionStep<V, U, T, R, E>}
     */
    public final TriFunctionStep<V, U, T, R, E> asVUT() {
        return new TriFunctionStep<>(this.stepName, this.stepDescription, (v, u, t) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep<E> asRunnableStep(final T t, final U u, final V v) {
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code SupplierStep}
     */
    public final SupplierStep<R, E> asSupplierStep(final T t, final U u, final V v) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T, E> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param v the third input argument
     * @return {@code BiConsumerStep}
     */
    public final BiConsumerStep<T, U, E> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code TriConsumerStep}.
     *
     * @return {@code TriConsumerStep}
     */
    public final TriConsumerStep<T, U, V, E> asTriConsumerStep() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, u, v) -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code FunctionStep}
     */
    public final FunctionStep<T, R, E> asFunctionStep(final U u, final V v) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> this.step.apply(t, u, v));
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param v the third input argument
     * @return {@code BiFunctionStep}
     */
    public final BiFunctionStep<T, U, R, E> asBiFunctionStep(final V v) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.apply(t, u, v));
    }

    @Override
    public final String toString() {
        return "TriFunctionStep(" + this.stepName + ")";
    }
}
