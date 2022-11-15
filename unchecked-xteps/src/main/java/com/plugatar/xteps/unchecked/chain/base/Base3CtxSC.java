package com.plugatar.xteps.unchecked.chain.base;

import com.plugatar.xteps.base.ThrowingTriConsumer;
import com.plugatar.xteps.base.ThrowingTriFunction;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.stepobject.TriConsumerStep;
import com.plugatar.xteps.unchecked.stepobject.TriFunctionStep;

/**
 * Base triple context steps chain.
 *
 * @param <C>  the context type
 * @param <P1> the previous context type
 * @param <P2> the previous context type
 */
public interface Base3CtxSC<C, P1, P2> {

    /**
     * Adds given hook to this steps chain. This hook will be calls in case of any
     * exception in steps chain or in case of {@link BaseSC#callHooks()} method call.
     *
     * @param hook the hook
     * @return this steps chain
     * @throws XtepsException if {@code hook} is null
     */
    BaseCtxSC<C, ?> hook(ThrowingTriConsumer<C, P1, P2, ?> hook);

    /**
     * Returns a context steps chain of the new context.
     *
     * @param contextFunction the context function
     * @param <U>             the context type
     * @return contextual steps chain
     * @throws XtepsException if {@code contextFunction} is null
     */
    <U> BaseCtxSC<U, ?> withContext(
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> contextFunction
    );

    /**
     * Supply context to given consumer and returns this steps chain.
     *
     * @param consumer the consumer
     * @return this steps chain
     * @throws XtepsException if {@code consumer} is null
     */
    BaseCtxSC<C, ?> supplyContext(
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
     * Performs and reports given step and returns this steps chain.
     *
     * @param step the step
     * @return this steps chain
     * @throws XtepsException if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    BaseCtxSC<C, ?> step(
        TriConsumerStep<? super C, ? super P1, ? super P2> step
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
        TriConsumerStep<? super C, ? super P1, ? super P2> step
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
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
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
        ThrowingTriConsumer<? super C, ? super P1, ? super P2, ?> step
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
        TriFunctionStep<? super C, ? super P1, ? super P2, ? extends U> step
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
        TriFunctionStep<? super C, ? super P1, ? super P2, ? extends U> step
    );

    /**
     * Performs and reports given step with given name and returns a contextual
     * steps chain of the new context.
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
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
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
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends U, ?> step
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
        TriFunctionStep<? super C, ? super P1, ? super P2, ? extends R> step
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
        TriFunctionStep<? super C, ? super P1, ? super P2, ? extends R> step
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
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ?> step
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
        ThrowingTriFunction<? super C, ? super P1, ? super P2, ? extends R, ?> step
    );
}
