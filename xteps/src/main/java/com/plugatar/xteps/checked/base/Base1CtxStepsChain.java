package com.plugatar.xteps.checked.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;

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
     * @param <E>             the exception type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     * @throws E              if {@code contextFunction} threw exception
     */
    <U, E extends Throwable> BaseCtxStepsChain<U, ?> withContext(
        ThrowingFunction<? super C, ? extends U, ? extends E> contextFunction
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
        ThrowingConsumer<? super C, ? extends E> consumer
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
        ThrowingFunction<? super C, ? extends R, ? extends E> function
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
     * @see #step(String, String, ThrowingConsumer)
     */
    <E extends Throwable> BaseCtxStepsChain<C, ?> step(
        String stepName,
        ThrowingConsumer<? super C, ? extends E> step
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
     * @see #step(String, ThrowingConsumer)
     */
    <E extends Throwable> BaseCtxStepsChain<C, ?> step(
        String stepName,
        String stepDescription,
        ThrowingConsumer<? super C, ? extends E> step
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
     * @see #stepToContext(String, String, ThrowingFunction)
     */
    <U, E extends Throwable> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
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
     * @see #stepToContext(String, ThrowingFunction)
     */
    <U, E extends Throwable> BaseCtxStepsChain<U, ?> stepToContext(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends U, ? extends E> step
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
     * @see #stepTo(String, String, ThrowingFunction)
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        ThrowingFunction<? super C, ? extends R, ? extends E> step
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
     * @see #stepTo(String, ThrowingFunction)
     */
    <R, E extends Throwable> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends R, ? extends E> step
    ) throws E;
}
