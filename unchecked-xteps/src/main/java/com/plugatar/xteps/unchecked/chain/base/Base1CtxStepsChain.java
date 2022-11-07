package com.plugatar.xteps.unchecked.chain.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.ConsumerStep;
import com.plugatar.xteps.unchecked.FunctionStep;

/**
 * Base single context steps chain.
 *
 * @param <C> the context type
 */
public interface Base1CtxStepsChain<C> {

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     */
    <U> BaseCtxStepsChain<U, ?> withContext(
        ThrowingFunction<? super C, ? extends U, ?> contextFunction
    );

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     */
    BaseCtxStepsChain<C, ?> supplyContext(
        ThrowingConsumer<? super C, ?> consumer
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
        ThrowingFunction<? super C, ? extends R, ?> function
    );

    /**
     * Performs and reports given step and returns this steps chain.
     *
     * @param step the step
     * @return this steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    BaseCtxStepsChain<C, ?> step(
        ConsumerStep<? super C> step
    );

    /**
     * Performs and reports given step with given name and returns this steps chain.
     *
     * @param stepName the step name
     * @param step     the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String, ThrowingConsumer)
     */
    BaseCtxStepsChain<C, ?> step(
        String stepName,
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * Performs and reports given step with given name and description and returns this steps chain.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @return this steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, ThrowingConsumer)
     */
    BaseCtxStepsChain<C, ?> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ?> step
    );

    /**
     * Performs and reports given step and returns a contextual steps chain of the new context.
     *
     * @param step the step
     * @param <U>  the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        FunctionStep<? super C, ? extends U> step
    );

    /**
     * Performs and reports given step with given name and returns a contextual steps chain
     * of the new context.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <U>      the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepToContext(String, String, ThrowingFunction)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * Performs and reports given step with given name and description and returns
     * a contextual steps chain of the new context.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepToContext(String, ThrowingFunction)
     */
    <U> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ?> step
    );

    /**
     * Performs and reports given step and returns the step result.
     *
     * @param step the step
     * @param <R>  the result type
     * @return {@code step} result
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        FunctionStep<? super C, ? extends R> step
    );

    /**
     * Performs and reports given step with given name and returns the step result.
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, String, ThrowingFunction)
     */
    <R> R stepTo(
        String stepName,
        ThrowingFunction<? super C, ? extends R, ?> step
    );

    /**
     * Performs and reports given step with given name and description and returns the step result.
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, ThrowingFunction)
     */
    <R> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends R, ?> step
    );
}
