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
package com.plugatar.xteps.unchecked;

import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsBase;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.unchecked.impl.NoCtxStepsChainImpl;

import java.util.function.Supplier;

/**
 * Main Unchecked Xteps API. Utility class.
 *
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class UncheckedXteps {

    /**
     * Performs empty step with given name.<br>
     * Code example:
     * <pre>{@code
     * step("Step 1");
     * }</pre>
     *
     * @param stepName the step name
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String)
     */
    public static void step(final String stepName) {
        CACHED_NO_CTX_STEPS_CHAIN.get().step(stepName);
    }

    /**
     * Performs empty step with given name and description.<br>
     * Code example:
     * <pre>{@code
     * step("Step 1", "Description");
     * }</pre>
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String)
     */
    public static void step(final String stepName,
                            final String stepDescription) {
        CACHED_NO_CTX_STEPS_CHAIN.get().step(stepName, stepDescription);
    }

    /**
     * Performs given step with given name.<br>
     * Code example:
     * <pre>{@code
     * step("Step 1", () -> {
     *     ...
     * });
     * step("Step 2", () -> {
     *     ...
     *     step("Nested step 1", () -> {
     *         ...
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, String, ThrowingRunnable)
     */
    public static void step(final String stepName,
                            final ThrowingRunnable<?> step) {
        CACHED_NO_CTX_STEPS_CHAIN.get().step(stepName, step);
    }

    /**
     * Performs given step with given name and description.<br>
     * Code example:
     * <pre>{@code
     * step("Step 1", "Description", () -> {
     *     ...
     * });
     * step("Step 2", "Description", () -> {
     *     ...
     *     step("Nested step 1", "Description", () -> {
     *         ...
     *     });
     * });
     * }</pre>
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #step(String, ThrowingRunnable)
     */
    public static void step(final String stepName,
                            final String stepDescription,
                            final ThrowingRunnable<?> step) {
        CACHED_NO_CTX_STEPS_CHAIN.get().step(stepName, stepDescription, step);
    }

    /**
     * Performs given step with given name and returns the step result.<br>
     * Code example:
     * <pre>{@code
     * String step1Result = stepTo("Step 1", () -> {
     *     ...
     *     return "result1";
     * });
     * String step2Result = stepTo("Step 2", () -> {
     *     ...
     *     return stepTo("Nested step 1", () -> {
     *         ...
     *         return "result2";
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, String, ThrowingSupplier)
     */
    public static <R> R stepTo(final String stepName,
                               final ThrowingSupplier<? extends R, ?> step) {
        return CACHED_NO_CTX_STEPS_CHAIN.get().stepTo(stepName, step);
    }

    /**
     * Performs given step with given name and description and returns the step result.<br>
     * Code example:
     * <pre>{@code
     * String step1Result = stepTo("Step 1", "Description", () -> {
     *     ...
     *     return "result1";
     * });
     * String step2Result = stepTo("Step 2", "Description", () -> {
     *     ...
     *     return stepTo("Nested step 1", "Description", () -> {
     *         ...
     *         return "result2";
     *     });
     * });
     * }</pre>
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @see #stepTo(String, ThrowingSupplier)
     */
    public static <R> R stepTo(final String stepName,
                               final String stepDescription,
                               final ThrowingSupplier<? extends R, ?> step) {
        return CACHED_NO_CTX_STEPS_CHAIN.get().stepTo(stepName, stepDescription, step);
    }

    /**
     * Returns no context steps chain.<br>
     * Code example:
     * <pre>{@code
     * stepsChain()
     *     .step("Step 1", () -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", stepsChain -> stepsChain
     *         .step("Nested step 1", () -> {
     *             ...
     *         })
     *         .step("Nested step 2", "Description", () -> {
     *             ...
     *         })
     *     );
     * stepsChain().withContext("context")
     *     .step("Step 3", ctx -> {
     *         ...
     *     })
     *     .nestedSteps("Step 4", stepsChain -> stepsChain
     *         .step("Nested step 1", ctx -> {
     *             ...
     *         })
     *         .step("Nested step 2", "Description", ctx -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @return no context steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     */
    public static NoCtxStepsChain stepsChain() {
        return CACHED_NO_CTX_STEPS_CHAIN.get();
    }

    private static final Supplier<NoCtxStepsChain> CACHED_NO_CTX_STEPS_CHAIN = new Supplier<NoCtxStepsChain>() {
        private volatile NoCtxStepsChain instance = null;

        @Override
        public NoCtxStepsChain get() {
            NoCtxStepsChain result;
            if ((result = this.instance) == null) {
                synchronized (this) {
                    if ((result = this.instance) == null) {
                        final XtepsBase config = XtepsBase.cached();
                        result = new NoCtxStepsChainImpl(
                            config.stepReporter(), config.exceptionHandler(), config.safeACContainerGenerator()
                        );
                        this.instance = result;
                    }
                    return result;
                }
            }
            return result;
        }
    };

    /**
     * Utility class ctor.
     */
    private UncheckedXteps() {
    }
}