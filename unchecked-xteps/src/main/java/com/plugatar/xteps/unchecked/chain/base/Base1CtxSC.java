package com.plugatar.xteps.unchecked.chain.base;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.stepobject.ConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.FunctionStep;

/**
 * Base single context steps chain.
 *
 * @param <C> the context type
 */
public interface Base1CtxSC<C> {

    /**
     * Adds given hook to this steps chain. This hook will be calls in case of any
     * exception in steps chain or in case of {@link BaseSC#callHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    BaseCtxSC<C, ?> hook(ThrowingConsumer<C, ?> hook);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     */
    <U> BaseCtxSC<U, ?> withContext(
        ThrowingFunction<? super C, ? extends U, ?> contextFunction
    );

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     */
    BaseCtxSC<C, ?> supplyContext(
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
    BaseCtxSC<C, ?> step(
        ConsumerStep<? super C> step
    );

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * this steps chain.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @return this steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    BaseCtxSC<C, ?> step(
        String stepNamePrefix,
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
     */
    BaseCtxSC<C, ?> step(
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
     */
    BaseCtxSC<C, ?> step(
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
    <U> BaseCtxSC<U, ?> stepToContext(
        FunctionStep<? super C, ? extends U> step
    );

    /**
     * Performs and reports given step with given prefix in the step name and returns
     * a contextual steps chain of the new context.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <U>            the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <U> BaseCtxSC<U, ?> stepToContext(
        String stepNamePrefix,
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
     */
    <U> BaseCtxSC<U, ?> stepToContext(
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
     */
    <U> BaseCtxSC<U, ?> stepToContext(
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
     * Performs and reports given step with given prefix in the step name and returns
     * the step result.
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <R>            the result type
     * @return {@code step} result
     * @throws XtepsException if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    <R> R stepTo(
        String stepNamePrefix,
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
     */
    <R> R stepTo(
        String stepName,
        String stepDescription,
        ThrowingFunction<? super C, ? extends R, ?> step
    );
}
