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

import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.UncheckedXteps;

import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.humanReadableStepNameOfClass;
import static com.plugatar.xteps.unchecked.stepobject.StepObjectsUtils.stepNameWithKeyword;

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
     * @param step the step
     */
    protected TriConsumerStep(final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> step) {
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
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> step) {
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
                           final ThrowingTriConsumer<? super T, ? super U, ? super V, ?> step) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.step = step;
    }

    /**
     * Returns dummy {@code TriConsumerStep}.
     *
     * @param stepName the step name
     * @param <T>      the type of the first input argument
     * @param <U>      the type of the second input argument
     * @param <V>      the type of the third input argument
     * @return dummy {@code TriConsumerStep}
     */
    public static <T, U, V> TriConsumerStep<T, U, V> dummy(final String stepName) {
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
     */
    @Override
    public final void accept(final T t, final U u, final V v) {
        UncheckedXteps.stepsChain().withContext(v).withContext(u).withContext(t)
            .step(this.stepName, this.stepDescription, this.step);
    }

    /**
     * Returns a new {@code TriConsumerStep} with given keyword in the step name.
     *
     * @param keyword the keyword
     * @return {@code TriConsumerStep} with given keyword in the step name
     */
    public final TriConsumerStep<T, U, V> withKeyword(final String keyword) {
        return new TriConsumerStep<>(
            stepNameWithKeyword(keyword, this.stepName), this.stepDescription, this.step
        );
    }

    /**
     * Returns this step as {@code TriConsumerStep<T, V, U>}.
     *
     * @return {@code TriConsumerStep<T, V, U>}
     */
    public final TriConsumerStep<T, V, U> asTVU() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (t, v, u) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<U, T, V>}.
     *
     * @return {@code TriConsumerStep<U, T, V>}
     */
    public final TriConsumerStep<U, T, V> asUTV() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (u, t, v) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<U, V, T>}.
     *
     * @return {@code TriConsumerStep<U, V, T>}
     */
    public final TriConsumerStep<U, V, T> asUVT() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (u, v, t) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<V, T, U>}.
     *
     * @return {@code TriConsumerStep<V, T, U>}
     */
    public final TriConsumerStep<V, T, U> asVTU() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (v, t, u) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as {@code TriConsumerStep<V, U, T>}.
     *
     * @return {@code TriConsumerStep<V, U, T>}
     */
    public final TriConsumerStep<V, U, T> asVUT() {
        return new TriConsumerStep<>(this.stepName, this.stepDescription, (v, u, t) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code RunnableStep}.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code RunnableStep}
     */
    public final RunnableStep asRunnableStep(final T t, final U u, final V v) {
        return new RunnableStep(this.stepName, this.stepDescription, () -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code SupplierStep}.
     *
     * @param t   the first input argument
     * @param u   the second input argument
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code SupplierStep}
     */
    public final <R> SupplierStep<R> asSupplierStep(final T t, final U u, final V v, final R r) {
        return new SupplierStep<>(this.stepName, this.stepDescription, () -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a {@code ConsumerStep}.
     *
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code ConsumerStep}
     */
    public final ConsumerStep<T> asConsumerStep(final U u, final V v) {
        return new ConsumerStep<>(this.stepName, this.stepDescription, t -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code BiConsumerStep}.
     *
     * @param v the third input argument
     * @return {@code BiConsumerStep}
     */
    public final BiConsumerStep<T, U> asBiConsumerStep(final V v) {
        return new BiConsumerStep<>(this.stepName, this.stepDescription, (t, u) -> this.step.accept(t, u, v));
    }

    /**
     * Returns this step as a {@code FunctionStep}.
     *
     * @param u   the second input argument
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code FunctionStep}
     */
    public final <R> FunctionStep<T, R> asFunctionStep(final U u, final V v, final R r) {
        return new FunctionStep<>(this.stepName, this.stepDescription, t -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a {@code BiFunctionStep}.
     *
     * @param v   the third input argument
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code BiFunctionStep}
     */
    public final <R> BiFunctionStep<T, U, R> asBiFunctionStep(final V v, final R r) {
        return new BiFunctionStep<>(this.stepName, this.stepDescription, (t, u) -> {
            this.step.accept(t, u, v);
            return r;
        });
    }

    /**
     * Returns this step as a {@code TriFunctionStep}.
     *
     * @param r   the result
     * @param <R> the type of the result
     * @return {@code TriFunctionStep}
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
