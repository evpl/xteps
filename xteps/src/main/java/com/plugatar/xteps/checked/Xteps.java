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
package com.plugatar.xteps.checked;

import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsBase;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.base.container.FakeHookContainer;
import com.plugatar.xteps.checked.chain.NoCtxSC;
import com.plugatar.xteps.checked.chain.impl.NoCtxSCImpl;
import com.plugatar.xteps.checked.stepobject.RunnableStep;
import com.plugatar.xteps.checked.stepobject.SupplierStep;

import java.util.function.Supplier;

/**
 * Xteps API.
 *
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class Xteps {

    /**
     * Performs and reports empty step with given name.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * step("Step 1");
     * }</pre>
     *
     * @param stepName the step name
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(final String stepName) {
        CHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName);
    }

    /**
     * Performs and reports empty step with given name and description.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * step("Step 1", "Description");
     * }</pre>
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(final String stepName,
                            final String stepDescription) {
        CHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName, stepDescription);
    }

    /**
     * Performs and reports given step.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * public class CustomStep extends RunnableStep<RuntimeException> {
     *
     *     public CustomStep() {
     *         super("Custom step", () -> {
     *             //...
     *         });
     *     }
     * }
     *
     * step(new CustomStep());
     * }</pre>
     *
     * @param step the step
     * @param <E>  the {@code step} exception type
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <E extends Throwable> void step(
        final RunnableStep<? extends E> step
    ) throws E {
        CHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(step);
    }

    /**
     * Performs and reports given step with given prefix in the step name.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * public class CustomStep extends RunnableStep<RuntimeException> {
     *
     *     public CustomStep() {
     *         super("Custom step", () -> {
     *             //...
     *         });
     *     }
     * }
     *
     * step("GIVEN", new CustomStep());
     * }</pre>
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <E>            the {@code step} exception type
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <E extends Throwable> void step(
        final String stepNamePrefix,
        final RunnableStep<? extends E> step
    ) throws E {
        CHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepNamePrefix, step);
    }

    /**
     * Performs and reports given step with given name.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * step("Step 1", () -> {
     *     //...
     * });
     * step("Step 2", () -> {
     *     //...
     *     step("Nested step 1", () -> {
     *         //...
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <E>      the {@code step} exception type
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <E extends Throwable> void step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        CHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName, step);
    }

    /**
     * Performs and reports given step with given name and description.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * step("Step 1", "Description", () -> {
     *     //...
     * });
     * step("Step 2", "Description", () -> {
     *     //...
     *     step("Nested step 1", "Description", () -> {
     *         //...
     *     });
     * });
     * }</pre>
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <E>             the {@code step} exception type
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <E extends Throwable> void step(
        final String stepName,
        final String stepDescription,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        CHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName, stepDescription, step);
    }

    /**
     * Performs and reports given step and returns the step result.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * public class CustomStep extends SupplierStep<String, RuntimeException> {
     *
     *     public CustomStep() {
     *         super("Custom step", () -> {
     *             //...
     *             return "result";
     *         });
     *     }
     * }
     *
     * String result = stepTo(new CustomStep());
     * }</pre>
     *
     * @param step the step
     * @param <R>  the result type
     * @param <E>  the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <R, E extends Throwable> R stepTo(
        final SupplierStep<? extends R, ? extends E> step
    ) throws E {
        return CHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(step);
    }

    /**
     * reports given step with given prefix in the step name and returns the step result.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * public class CustomStep extends SupplierStep<String, RuntimeException> {
     *
     *     public CustomStep() {
     *         super("Custom step", () -> {
     *             //...
     *             return "result";
     *         });
     *     }
     * }
     *
     * String result = stepTo("GIVEN", new CustomStep());
     * }</pre>
     *
     * @param stepNamePrefix the step name prefix
     * @param step           the step
     * @param <R>            the result type
     * @param <E>            the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepNamePrefix} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <R, E extends Throwable> R stepTo(
        final String stepNamePrefix,
        final SupplierStep<? extends R, ? extends E> step
    ) throws E {
        return CHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(stepNamePrefix, step);
    }

    /**
     * Performs and reports given step with given name and returns the step result.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * String step1Result = stepTo("Step 1", () -> {
     *     //...
     *     return "result1";
     * });
     * String step2Result = stepTo("Step 2", () -> {
     *     //...
     *     return stepTo("Nested step 1", () -> {
     *         //...
     *         return "result2";
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @param <E>      the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        return CHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(stepName, step);
    }

    /**
     * Performs and reports given step with given name and description and returns the step result.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * String step1Result = stepTo("Step 1", "Description", () -> {
     *     //...
     *     return "result1";
     * });
     * String step2Result = stepTo("Step 2", "Description", () -> {
     *     //...
     *     return stepTo("Nested step 1", "Description", () -> {
     *         //...
     *         return "result2";
     *     });
     * });
     * }</pre>
     *
     * @param stepName        the step name
     * @param stepDescription the step description
     * @param step            the step
     * @param <R>             the result type
     * @param <E>             the {@code step} exception type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <R, E extends Throwable> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        return CHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(stepName, stepDescription, step);
    }

    /**
     * Returns no context steps chain.
     *
     * <p>Code example:</p>
     *
     * <pre>{@code
     * stepsChain()
     *     .step("Step 1", () -> {
     *         //...
     *     })
     *     .nestedSteps("Step 2", stepsChain -> stepsChain
     *         .step("Nested step 1", () -> {
     *             //...
     *         })
     *         .step("Nested step 2", "Description", () -> {
     *             //...
     *         })
     *     );
     * stepsChain().withContext("context")
     *     .step("Step 3", ctx -> {
     *         //...
     *     })
     *     .nestedSteps("Step 4", stepsChain -> stepsChain
     *         .step("Nested step 1", ctx -> {
     *             //...
     *         })
     *         .step("Nested step 2", "Description", ctx -> {
     *             //...
     *         })
     *     );
     * }</pre>
     *
     * @return no context steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     */
    public static NoCtxSC stepsChain() {
        return CHECKED_XTEPS_BASE.get().newNoCtxCS();
    }

    private static final Supplier<CheckedXtepsBase> CHECKED_XTEPS_BASE = new Supplier<CheckedXtepsBase>() {
        private volatile CheckedXtepsBase instance = null;

        @Override
        public CheckedXtepsBase get() {
            CheckedXtepsBase result;
            if ((result = this.instance) == null) {
                synchronized (this) {
                    if ((result = this.instance) == null) {
                        final XtepsBase xtepsBase = XtepsBase.cached();
                        result = new CheckedXtepsBase(xtepsBase);
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
    private Xteps() {
    }

    private static final class CheckedXtepsBase {
        private final XtepsBase xtepsBase;
        private final NoCtxSC simpleNoCtxSC;

        private CheckedXtepsBase(final XtepsBase xtepsBase) {
            this.xtepsBase = xtepsBase;
            this.simpleNoCtxSC = new NoCtxSCImpl(
                xtepsBase.stepReporter(), xtepsBase.exceptionHandler(), new FakeHookContainer()
            );
        }

        private NoCtxSC simpleNoCtxSC() {
            return this.simpleNoCtxSC;
        }

        private NoCtxSC newNoCtxCS() {
            return new NoCtxSCImpl(
                this.xtepsBase.stepReporter(),
                this.xtepsBase.exceptionHandler(),
                this.xtepsBase.hookContainerGenerator().get()
            );
        }
    }
}
