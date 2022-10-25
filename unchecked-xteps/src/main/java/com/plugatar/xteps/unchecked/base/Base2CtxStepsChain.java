package com.plugatar.xteps.unchecked.base;

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
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     * @throws Throwable      if {@code contextFunction} threw exception
     */
    <U> BaseCtxStepsChain<U, ?> withContext(
        ThrowingBiFunction<? super C, ? super P, ? extends U, ?> contextFunction
    );

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     * @throws Throwable      if {@code consumer} threw exception
     */
    BaseCtxStepsChain<C, ?> supplyContext(
        ThrowingBiConsumer<? super C, ? super P, ?> consumer
    );

    /**
     * Apply context to given function and returns result.
     *
     * @param function the function
     * @param <R>      the {@code function} result type
     * @return the {@code function} result
     * @throws XtepsException if {@code function} is null
     * @throws Throwable      if {@code function} threw exception
     */
    <R> R applyContext(
        ThrowingBiFunction<? super C, ? super P, ? extends R, ?> function
    );

    /**
     * Performs given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws Throwable      if {@code step} threw exception
     * @see #step(String, String, ThrowingBiConsumer)
     */
    BaseCtxStepsChain<C, ?> step(
        String stepName,
        ThrowingBiConsumer<? super C, ? super P, ?> step
    );

    /**
     * Performs given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws Throwable      if {@code step} threw exception
     * @see #step(String, ThrowingBiConsumer)
     */
    BaseCtxStepsChain<C, ?> step(
        String stepName,
        String stepDescription,
        ThrowingBiConsumer<? super C, ? super P, ?> step
    );

    /**
     * Performs given step with given name and returns a contextual steps chain of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws Throwable      if {@code step} threw exception
     * @see #stepToContext(String, String, ThrowingBiFunction)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    );

    /**
     * Performs given step with given name and description and returns a contextual steps chain of the new context.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws Throwable      if {@code step} threw exception
     * @see #stepToContext(String, ThrowingBiFunction)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P, ? extends U, ?> step
    );

    /**
     * Performs given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws Throwable      if {@code step} threw exception
     * @see #stepTo(String, String, ThrowingBiFunction)
     */
    <R> R stepTo(
        String stepName,
        ThrowingBiFunction<? super C, ? super P, ? extends R, ?> step
    );

    /**
     * Performs given step with given name and description and returns the step result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws Throwable      if {@code step} threw exception
     * @see #stepTo(String, ThrowingBiFunction)
     */
    <R> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingBiFunction<? super C, ? super P, ? extends R, ?> step
    );
}
