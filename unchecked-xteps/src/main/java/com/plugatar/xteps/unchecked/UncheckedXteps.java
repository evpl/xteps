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
import com.plugatar.xteps.base.hook.ThreadHooks;
import com.plugatar.xteps.base.hook.container.FakeHookContainer;
import com.plugatar.xteps.unchecked.chain.CtxSC;
import com.plugatar.xteps.unchecked.chain.Mem2CtxSC;
import com.plugatar.xteps.unchecked.chain.Mem3CtxSC;
import com.plugatar.xteps.unchecked.chain.NoCtxSC;
import com.plugatar.xteps.unchecked.chain.impl.NoCtxSCImpl;
import com.plugatar.xteps.unchecked.stepobject.RunnableStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;

import java.util.function.Supplier;

/**
 * Unchecked Xteps API.
 *
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class UncheckedXteps {

    /**
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param hook the hook
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code hook} is null
     */
    public static void threadHook(final ThrowingRunnable<?> hook) {
        ThreadHooks.add(hook);
    }

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
        UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName);
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
        UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName, stepDescription);
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
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final RunnableStep step
    ) {
        UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(step);
    }

    /**
     * Performs and reports given step with given keyword in the step name.
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
     * @param keyword the keyword
     * @param step    the step
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final String keyword,
        final RunnableStep step
    ) {
        UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(keyword, step);
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
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final String stepName,
        final ThrowingRunnable<?> step
    ) {
        UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName, step);
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
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final String stepName,
        final String stepDescription,
        final ThrowingRunnable<?> step
    ) {
        UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().step(stepName, stepDescription, step);
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
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final SupplierStep<? extends R> step
    ) {
        return UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(step);
    }

    /**
     * Reports given step with given keyword in the step name and returns the step result.
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
     * @param keyword the keyword
     * @param step    the step
     * @param <R>     the result type
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code keyword} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final String keyword,
        final SupplierStep<? extends R> step
    ) {
        return UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(keyword, step);
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
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ?> step
    ) {
        return UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(stepName, step);
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
     * @return {@code step} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code stepName} or {@code stepDescription} or {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final String stepName,
        final String stepDescription,
        final ThrowingSupplier<? extends R, ?> step
    ) {
        return UNCHECKED_XTEPS_BASE.get().simpleNoCtxSC().stepTo(stepName, stepDescription, step);
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
        return UNCHECKED_XTEPS_BASE.get().newNoCtxSC();
    }

    /**
     * Returns a contextual steps chain with given context.
     * <p>Alias for</p>
     * <pre>{@code stepsChain().withContext(context)}</pre>
     *
     * @param context the context
     * @param <C>     the context type
     * @return contextual steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     * @see #stepsChain()
     */
    public static <C> CtxSC<C> stepsChainOf(final C context) {
        return UNCHECKED_XTEPS_BASE.get().newNoCtxSC()
            .withContext(context);
    }

    /**
     * Returns a contextual steps chain with given contexts.
     * <p>Alias for</p>
     * <pre>{@code stepsChain().withContext(context2).withContext(context)}</pre>
     *
     * @param context  the context
     * @param context2 the second context
     * @param <C>      the context type
     * @param <C2>     the second context type
     * @return contextual steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     * @see #stepsChain()
     */
    public static <C, C2> Mem2CtxSC<C, C2, CtxSC<C2>> stepsChainOf(
        final C context,
        final C2 context2
    ) {
        return UNCHECKED_XTEPS_BASE.get().newNoCtxSC()
            .withContext(context2).withContext(context);
    }

    /**
     * Returns a contextual steps chain with given contexts.
     * <p>Alias for</p>
     * <pre>{@code stepsChain().withContext(context3).withContext(context2).withContext(context)}</pre>
     *
     * @param context  the context
     * @param context2 the second context
     * @param context3 the third context
     * @param <C>      the context type
     * @param <C2>     the second context type
     * @param <C3>     the third context type
     * @return contextual steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     * @see #stepsChain()
     */
    public static <C, C2, C3> Mem3CtxSC<C, C2, C3, Mem2CtxSC<C2, C3, CtxSC<C3>>> stepsChainOf(
        final C context,
        final C2 context2,
        final C3 context3
    ) {
        return UNCHECKED_XTEPS_BASE.get().newNoCtxSC()
            .withContext(context3).withContext(context2).withContext(context);
    }

    private static final Supplier<UncheckedXtepsBase> UNCHECKED_XTEPS_BASE = new Supplier<UncheckedXtepsBase>() {
        private volatile UncheckedXtepsBase instance = null;

        @Override
        public UncheckedXtepsBase get() {
            UncheckedXtepsBase result;
            if ((result = this.instance) == null) {
                synchronized (this) {
                    if ((result = this.instance) == null) {
                        final XtepsBase xtepsBase = XtepsBase.cached();
                        result = new UncheckedXtepsBase(xtepsBase);
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

    private static final class UncheckedXtepsBase {
        private final XtepsBase xtepsBase;
        private final NoCtxSC simpleNoCtxSC;

        private UncheckedXtepsBase(final XtepsBase xtepsBase) {
            this.xtepsBase = xtepsBase;
            this.simpleNoCtxSC = new NoCtxSCImpl(
                xtepsBase.stepReporter(), xtepsBase.exceptionHandler(), new FakeHookContainer()
            );
        }

        private NoCtxSC simpleNoCtxSC() {
            return this.simpleNoCtxSC;
        }

        private NoCtxSC newNoCtxSC() {
            return new NoCtxSCImpl(
                this.xtepsBase.stepReporter(),
                this.xtepsBase.exceptionHandler(),
                this.xtepsBase.hookContainerGenerator().get()
            );
        }
    }
}
