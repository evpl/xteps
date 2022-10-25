package com.plugatar.xteps.unchecked.base;

import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.base.XtepsException;

/**
 * Base triple context steps chain.
 *
 * @param <C>  the context type
 * @param <P1> the previous context type
 * @param <P2> the previous context type
 */
public interface Base3CtxStepsChain<C, P1, P2> {

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     */
    <U> BaseCtxStepsChain<U, ?> withContext(
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> contextFunction
    );

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     */
    BaseCtxStepsChain<C, ?> supplyContext(
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> consumer
    );

    /**
     * Apply context to given function and returns result.
     *
     * @param function the function
     * @param <R>      the {@code function} result type
     * @return the {@code function} result
     * @throws XtepsException if {@code function} is null
     */
    <R> R applyContext(
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ?> function
    );

    /**
     * Performs given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String, ThrowingTriConsumer)
     */
    BaseCtxStepsChain<C, ?> step(
        String stepName,
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
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
     * @see #step(String, ThrowingTriConsumer)
     */
    BaseCtxStepsChain<C, ?> step(
        String stepName,
        String stepDescription,
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
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
     * @see #stepToContext(String, String, ThrowingTriFunction)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
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
     * @see #stepToContext(String, ThrowingTriFunction)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
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
     * @see #stepTo(String, String, ThrowingTriFunction)
     */
    <R> R stepTo(
        String stepName,
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ?> step
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
     * @see #stepTo(String, ThrowingTriFunction)
     */
    <R> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ?> step
    );
}
