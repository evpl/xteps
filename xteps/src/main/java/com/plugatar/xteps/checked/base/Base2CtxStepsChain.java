package com.plugatar.xteps.checked.base;

import com.plugatar.xteps.base.ThrowingBiConsumer;
import com.plugatar.xteps.base.ThrowingBiFunction;
import com.plugatar.xteps.base.XtepsException;

/**
 * Base double context steps chain.
 *
 * @param <C> the context type
 * @param <P> the previous context type
 */
public interface Base2CtxStepsChain<C, P> {

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @param <E>             the exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     * @throws E              if {@code contextFunction} threw exception
     */
    <U, E extends Throwable> BaseCtxStepsChain<U, ?> withContext(
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> contextFunction
    ) throws E;

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @param <E>      the {@code consumer} exception type
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     * @throws E              if {@code consumer} threw exception
     */
    <E extends Throwable> BaseCtxStepsChain<C, ?> supplyContext(
        ThrowingBiConsumer<? super C, ? super P, ? extends E> consumer
    ) throws E;

    /**
     * Apply context to given function and returns result.
     *
     * @param function the function
     * @param <E>      the {@code function} exception type
     * @param <R>      the {@code function} result type
     * @return the {@code function} result
     * @throws XtepsException if {@code function} is null
     * @throws E              if {@code function} threw exception
     */
    <R, E extends Throwable> R applyContext(
        ThrowingBiFunction<? super C, ? super P, ? extends R, ? extends E> function
    ) throws E;

    /**
     * Performs given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <E>      the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #step(String, String, ThrowingBiConsumer)
     */
    <E extends Throwable> BaseCtxStepsChain<C, ?> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <E>             the {@code step} exception type
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #step(String, ThrowingBiConsumer)
     */
    <E extends Throwable> BaseCtxStepsChain<C, ?> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super P, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and returns a contextual steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @param <E>      the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #stepToContext(String, String, ThrowingBiFunction)
     */
    <U, E extends Throwable> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and description and returns a contextual steps chain of the new context.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <U>             the context type
     * @param <E>             the {@code step} exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #stepToContext(String, ThrowingBiFunction)
     */
    <U, E extends Throwable> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @param <E>      the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #stepTo(String, String, ThrowingBiFunction)
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        ThrowingBiFunction<? super C, ? super P, ? extends R, ? extends E> step
    ) throws E;

    /**
     * Performs given step with given name and description and returns the step result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @param <E>             the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     * @see #stepTo(String, ThrowingBiFunction)
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P, ? extends R, ? extends E> step
    ) throws E;
}
