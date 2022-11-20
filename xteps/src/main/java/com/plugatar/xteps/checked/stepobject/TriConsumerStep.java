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

import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.checked.Xteps;

import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.checked.stepobject.StepObjectsUtils.stepNameWithKeyword;

/**
 * TriConsumer step. This step will be executed and reported when calling the
 * {@link #accept(Object, Object, Object)} method.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 * @param <E> the type of the throwing exception
 */
public class TriConsumerStep<T, U, V, E extends Throwable> implements ThrowingTriConsumer<T, U, V, E> {
    private final String stepName;
    private final String stepDescription;
    private final ThrowingTriConsumer<? super T, ? super U, ? super V, ? extends E> step;

    /**
     * Ctor.
     *
     * @param step the step
     */
    protected TriConsumerStep(final ThrowingTriConsumer<? super T, ? super U, ? super V, ? extends E> step) {
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
    public TriConsumerStep(final String stepName,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ? extends E> step) {
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
    public TriConsumerStep(final String stepName,
                           final String stepDescription,
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ? extends E> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Returns dummy TriConsumerStep.
     *
     * @param stepName the step name
     * @param <T>      the type of the first input argument
     * @param <U>      the type of the second input argument
     * @param <V>      the type of the third input argument
     * @return dummy TriConsumerStep
     */
    public static <T, U, V> TriConsumerStep<T, U, V, RuntimeException> dummy(final String stepName) {
        return new TriConsumerStep<>(
            stepName, (t, u, v) -> { throw new XtepsException("Step not implemented"); }
        );
    }

    /**
     * Performs and reports this step.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     * @throws E              if this step threw exception
     */
    @Override
    public final void accept(final T t, final U u, final V v) throws E {
        Xteps.stepsChain().withContext(v).withContext(u).withContext(t)
            .step(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new TriConsumerStep with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return TriConsumerStep with given keyword in the step name
     */
    public final TriConsumerStep<T, U, V, E> withKeyword(final String keyword) {
        return new TriConsumerStep<>(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as unchecked.
     *
     * @return unchecked TriConsumerStep
     */
    @SuppressWarnings("unchecked")
    public final TriConsumerStep<T, U, V, RuntimeException> asUnchecked() {
        return (TriConsumerStep<T, U, V, RuntimeException>) this;
    }

    /**
     * Returns this step as {@code TriConsumerStep<T, V, U, E>}.
     *
     * @return {@code TriConsumerStep<T, V, U, E>}
     */
    public final TriConsumerStep<T, V, U, E> asTVU() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, v, u) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<U, T, V, E>}.
     *
     * @return {@code TriConsumerStep<U, T, V, E>}
     */
    public final TriConsumerStep<U, T, V, E> asUTV() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (u, t, v) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<U, V, T, E>}.
     *
     * @return {@code TriConsumerStep<U, V, T, E>}
     */
    public final TriConsumerStep<U, V, T, E> asUVT() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (u, v, t) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<V, T, U, E>}.
     *
     * @return {@code TriConsumerStep<V, T, U, E>}
     */
    public final TriConsumerStep<V, T, U, E> asVTU() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (v, t, u) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<V, U, T, E>}.
     *
     * @return {@code TriConsumerStep<V, U, T, E>}
     */
    public final TriConsumerStep<V, U, T, E> asVUT() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (v, u, t) -> this.step.accept(t, u, v));
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
        return new RunnableStep<>(this.stepName, this.stepDescription, () -> this.step.accept(t, u, v));
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
    public final <R> SupplierStep<R, E> asSupplierStep(final T t, final U u, final V v, final R r) {
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
    public final ConsumerStep<T, E> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a BiConsumerStep.
     *
     * @param v the third input argument
     * @return BiConsumerStep
     */
    public final BiConsumerStep<T, U, E> asBiConsumerStep(final V v) {
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
    public final <R> FunctionStep<T, R, E> asFunctionStep(final U u, final V v, final R r) {
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
    public final <R> BiFunctionStep<T, U, R, E> asBiFunctionStep(final V v, final R r) {
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
    public final <R> TriFunctionStep<T, U, V, R, E> asTriFunctionStep(final R r) {
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
