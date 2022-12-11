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

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.HookPriority;
import com.plugatar.xteps.base.HooksContainer;
import com.plugatar.xteps.base.HooksOrder;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsBase;
import com.plugatar.xteps.base.XtepsException;
import com.plugatar.xteps.base.hook.FakeHooksContainer;
import com.plugatar.xteps.base.hook.ThreadHooks;
import com.plugatar.xteps.unchecked.chain.Ctx2SC;
import com.plugatar.xteps.unchecked.chain.Ctx3SC;
import com.plugatar.xteps.unchecked.chain.CtxSC;
import com.plugatar.xteps.unchecked.chain.NoCtxSC;
import com.plugatar.xteps.unchecked.chain.impl.Ctx2SCOf;
import com.plugatar.xteps.unchecked.chain.impl.Ctx3SCOf;
import com.plugatar.xteps.unchecked.chain.impl.CtxSCOf;
import com.plugatar.xteps.unchecked.chain.impl.NoCtxSCOf;
import com.plugatar.xteps.unchecked.stepobject.RunnableStep;
import com.plugatar.xteps.unchecked.stepobject.SupplierStep;

import java.util.function.Supplier;

/**
 * Unchecked Xteps API.
 * <p>
 * Step methods:
 * <ul>
 * <li>{@link #step(String)}</li>
 * <li>{@link #step(String, String)}</li>
 * <li>{@link #step(RunnableStep)}</li>
 * <li>{@link #step(String, RunnableStep)}</li>
 * <li>{@link #step(SupplierStep)}</li>
 * <li>{@link #step(String, SupplierStep)}</li>
 * <li>{@link #step(ThrowingRunnable)}</li>
 * <li>{@link #step(String, ThrowingRunnable)}</li>
 * <li>{@link #step(String, String, ThrowingRunnable)}</li>
 * <li>{@link #stepTo(SupplierStep)}</li>
 * <li>{@link #stepTo(String, SupplierStep)}</li>
 * <li>{@link #stepTo(ThrowingSupplier)}</li>
 * <li>{@link #stepTo(String, ThrowingSupplier)}</li>
 * <li>{@link #stepTo(String, String, ThrowingSupplier)}</li>
 * </ul>
 * <p>
 * Steps chain methods:
 * <ul>
 * <li>{@link #stepsChain()}</li>
 * <li>{@link #stepsChainOf()}</li>
 * <li>{@link #stepsChainOf(Object)}</li>
 * <li>{@link #stepsChainOf(Object, Object)}</li>
 * <li>{@link #stepsChainOf(Object, Object, Object)}</li>
 * </ul>
 * <p>
 * Thread hooks methods:
 * <ul>
 * <li>{@link #threadHook(ThrowingRunnable)}</li>
 * <li>{@link #threadHook(int, ThrowingRunnable)}</li>
 * <li>{@link #threadHookOrder(HooksOrder)}</li>
 * </ul>
 *
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class UncheckedXteps {

    /**
     * Utility class ctor.
     */
    private UncheckedXteps() {
    }

    /**
     * Performs and reports empty step with given name.
     * <p>
     * Code example:
     * <pre>{@code
     * step("Step 1");
     * }</pre>
     *
     * @param name the step name
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code name} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(final String name) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(name);
    }

    /**
     * Performs and reports empty step with given name and description.
     * <p>
     * Code example:
     * <pre>{@code
     * step("Step 1", "Description");
     * }</pre>
     *
     * @param name the step name
     * @param desc the step description
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code name} or {@code desc} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(final String name,
                            final String desc) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(name, desc);
    }

    /**
     * Performs and reports given step.
     * <p>
     * Code example:
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
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(step);
    }

    /**
     * Performs and reports given step with given keyword in the step name.
     * <p>
     * Code example:
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
     * step("When", new CustomStep());
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
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(keyword, step);
    }

    /**
     * Performs and reports given step.
     * <p>
     * Code example:
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
     * step(new CustomStep());
     * }</pre>
     *
     * @param step the step
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code step} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final SupplierStep<?> step
    ) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(step);
    }

    /**
     * Reports given step with given keyword in the step name.
     * <p>
     * Code example:
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
     * step("When", new CustomStep());
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
        final SupplierStep<?> step
    ) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(keyword, step);
    }

    /**
     * Performs and reports given step with empty name.
     * <p>
     * Code example:
     * <pre>{@code
     * step(() -> {
     *     //...
     * });
     * step(() -> {
     *     //...
     *     step(() -> {
     *         //...
     *     });
     * });
     * }</pre>
     *
     * @param action the step action
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final ThrowingRunnable<?> action
    ) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(action);
    }

    /**
     * Performs and reports given step with given name.
     * <p>
     * Code example:
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
     * @param name   the step name
     * @param action the step action
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final String name,
        final ThrowingRunnable<?> action
    ) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(name, action);
    }

    /**
     * Performs and reports given step with given name and description.
     * <p>
     * Code example:
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
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    public static void step(
        final String name,
        final String desc,
        final ThrowingRunnable<?> action
    ) {
        CACHED_FAKE_HOOKS_NO_CTX_SC.get().step(name, desc, action);
    }

    /**
     * Performs and reports given step and returns the step result.
     * <p>
     * Code example:
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
        return CACHED_FAKE_HOOKS_NO_CTX_SC.get().stepTo(step);
    }

    /**
     * Reports given step with given keyword in the step name and returns the step result.
     * <p>
     * Code example:
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
     * String result = stepTo("When", new CustomStep());
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
        return CACHED_FAKE_HOOKS_NO_CTX_SC.get().stepTo(keyword, step);
    }

    /**
     * Performs and reports given step with empty name and returns the step result.
     * <p>
     * Code example:
     * <pre>{@code
     * String step1Result = stepTo(() -> {
     *     //...
     *     return "result1";
     * });
     * String step2Result = stepTo(() -> {
     *     //...
     *     return stepTo(() -> {
     *         //...
     *         return "result2";
     *     });
     * });
     * }</pre>
     *
     * @param action the step action
     * @param <R>    the result type
     * @return {@code action} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final ThrowingSupplier<? extends R, ?> action
    ) {
        return CACHED_FAKE_HOOKS_NO_CTX_SC.get().stepTo(action);
    }

    /**
     * Performs and reports given step with given name and returns the step result.
     * <p>
     * Code example:
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
     * @param name   the step name
     * @param action the step action
     * @param <R>    the result type
     * @return {@code action} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code name} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final String name,
        final ThrowingSupplier<? extends R, ?> action
    ) {
        return CACHED_FAKE_HOOKS_NO_CTX_SC.get().stepTo(name, action);
    }

    /**
     * Performs and reports given step with given name and description and returns the step result.
     * <p>
     * Code example:
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
     * @param name   the step name
     * @param desc   the step description
     * @param action the step action
     * @param <R>    the result type
     * @return {@code action} result
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code name} or {@code desc} or {@code action} is null
     *                        or if it's impossible to correctly report the step
     */
    public static <R> R stepTo(
        final String name,
        final String desc,
        final ThrowingSupplier<? extends R, ?> action
    ) {
        return CACHED_FAKE_HOOKS_NO_CTX_SC.get().stepTo(name, desc, action);
    }

    /**
     * Returns no context steps chain.
     * <p>
     * Code example:
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
     * stepsChain().withCtx("context")
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
     *     )
     *     .stepToCtx("Step 5", ctx -> "context 2")
     *     .step("Step 6", (ctx1, ctx2) -> {
     *         //...
     *     });
     * }</pre>
     *
     * @return no context steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     */
    public static NoCtxSC stepsChain() {
        final XtepsBase xb = XtepsBase.cached();
        return new NoCtxSCOf(xb.stepReporter(), xb.exceptionHandler(), xb.hooksContainerGenerator().get());
    }

    /**
     * Returns no context steps chain. Alias for {@link #stepsChain()} method.
     *
     * @return no context steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     * @see #stepsChain()
     */
    public static NoCtxSC stepsChainOf() {
        return stepsChain();
    }

    /**
     * Returns a contextual steps chain with given context.
     * <p>
     * Code example:
     * <pre>{@code
     * stepsChainOf("context")
     *     .step("Step", ctx -> {
     *         //...
     *     });
     * }</pre>
     *
     * @param context the context
     * @param <C>     the context type
     * @return contextual steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     * @see #stepsChain()
     */
    public static <C> CtxSC<C, NoCtxSC> stepsChainOf(final C context) {
        final XtepsBase xb = XtepsBase.cached();
        final StepReporter stepReporter = xb.stepReporter();
        final ExceptionHandler exceptionHandler = xb.exceptionHandler();
        final HooksContainer hooksContainer = xb.hooksContainerGenerator().get();
        return new CtxSCOf<>(
            stepReporter, exceptionHandler, hooksContainer,
            context,
            new NoCtxSCOf(stepReporter, exceptionHandler, hooksContainer)
        );
    }

    /**
     * Returns a contextual steps chain with given contexts.
     * <p>
     * Code example:
     * <pre>{@code
     * stepsChainOf("context1", "context2")
     *     .step("Step", (ctx1, ctx2) -> {
     *         //...
     *     });
     * }</pre>
     *
     * @param context  the context
     * @param context2 the second context
     * @param <C>      the context type
     * @param <C2>     the second context type
     * @return contextual steps chain
     * @throws XtepsException if Xteps configuration is incorrect
     * @see #stepsChain()
     */
    public static <C, C2> Ctx2SC<C, C2, NoCtxSC> stepsChainOf(
        final C context,
        final C2 context2
    ) {
        final XtepsBase xb = XtepsBase.cached();
        final StepReporter stepReporter = xb.stepReporter();
        final ExceptionHandler exceptionHandler = xb.exceptionHandler();
        final HooksContainer hooksContainer = xb.hooksContainerGenerator().get();
        return new Ctx2SCOf<>(
            stepReporter, exceptionHandler, hooksContainer,
            context, context2,
            new NoCtxSCOf(stepReporter, exceptionHandler, hooksContainer)
        );
    }

    /**
     * Returns a contextual steps chain with given contexts.
     * <p>
     * Code example:
     * <pre>{@code
     * stepsChainOf("context1", "context2", "context3")
     *     .step("Step", (ctx1, ctx2, ctx3) -> {
     *         //...
     *     });
     * }</pre>
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
    public static <C, C2, C3> Ctx3SC<C, C2, C3, NoCtxSC> stepsChainOf(
        final C context,
        final C2 context2,
        final C3 context3
    ) {
        final XtepsBase xb = XtepsBase.cached();
        final StepReporter stepReporter = xb.stepReporter();
        final ExceptionHandler exceptionHandler = xb.exceptionHandler();
        final HooksContainer hooksContainer = xb.hooksContainerGenerator().get();
        return new Ctx3SCOf<>(
            stepReporter, exceptionHandler, hooksContainer,
            context, context2, context3,
            new NoCtxSCOf(stepReporter, exceptionHandler, hooksContainer)
        );
    }

    /**
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param hook the hook
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code hook} is null
     */
    public static void threadHook(final ThrowingRunnable<?> hook) {
        ThreadHooks.addHook(HookPriority.NORM_HOOK_PRIORITY, hook);
    }

    /**
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param priority the priority
     * @param hook     the hook
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code hook} is null
     *                        or if {@code priority} is not in the range {@link HookPriority#MIN_HOOK_PRIORITY} to
     *                        {@link HookPriority#MAX_HOOK_PRIORITY}
     */
    public static void threadHook(final int priority,
                                  final ThrowingRunnable<?> hook) {
        ThreadHooks.addHook(priority, hook);
    }

    /**
     * Sets given hooks order for the current thread.
     *
     * @param order the hooks order
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code hook} is null
     */
    public static void threadHookOrder(final HooksOrder order) {
        ThreadHooks.setOrder(order);
    }

    private static final Supplier<NoCtxSC> CACHED_FAKE_HOOKS_NO_CTX_SC = new Supplier<NoCtxSC>() {
        private volatile NoCtxSC instance = null;

        @Override
        public NoCtxSC get() {
            NoCtxSC result;
            if ((result = this.instance) == null) {
                synchronized (this) {
                    if ((result = this.instance) == null) {
                        final XtepsBase xb = XtepsBase.cached();
                        result = new NoCtxSCOf(xb.stepReporter(), xb.exceptionHandler(), new FakeHooksContainer());
                        this.instance = result;
                    }
                    return result;
                }
            }
            return result;
        }
    };
}
