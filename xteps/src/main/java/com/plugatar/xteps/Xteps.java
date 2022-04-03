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
package com.plugatar.xteps;

import com.plugatar.xteps.core.CtxSteps;
import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.XtepsBase;
import com.plugatar.xteps.core.base.supplier.XtepsBaseThrowingSupplier;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.ConfigException;
import com.plugatar.xteps.core.exception.StepNameFormatException;
import com.plugatar.xteps.core.exception.StepWriteException;
import com.plugatar.xteps.core.util.function.CachedThrowingSupplier;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;

/**
 * Utility class. Main Xteps API.
 *
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class Xteps {

    /**
     * Cached XtepsBase instance supplier.
     */
    private static final ThrowingSupplier<XtepsBase, RuntimeException> CACHED_BASE =
        new CachedThrowingSupplier<>(new XtepsBaseThrowingSupplier());

    /**
     * Utility class ctor.
     */
    private Xteps() {
    }

    /**
     * Returns XtepsBase instance. Non-reporting method.
     *
     * @return XtepsBase instance
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static XtepsBase xtepsBase() {
        return CACHED_BASE.get();
    }

    /**
     * Returns no context steps. Non-reporting method. Alias for {@link #steps()} method.<br>
     * Code example:
     * <pre>{@code
     * import com.plugatar.xteps.Xteps;
     *
     * Xteps.of()
     *     .step("Step 1", () -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", steps -> steps
     *         .step("Inner step 1", () -> {
     *             ...
     *         })
     *         .step("Inner step 2", () -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @return no context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static NoCtxSteps of() {
        return Xteps.steps();
    }

    /**
     * Returns no context steps. Non-reporting method.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.steps;
     *
     * steps()
     *     .step("Step 1", () -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", steps -> steps
     *         .step("Inner step 1", () -> {
     *             ...
     *         })
     *         .step("Inner step 2", () -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @return no context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static NoCtxSteps steps() {
        return CACHED_BASE.get().steps();
    }

    /**
     * Returns a context steps of given context. Non-reporting method.
     * Alias for {@link #stepsOf(Object)} method.<br>
     * Code example:
     * <pre>{@code
     * import com.plugatar.xteps.Xteps;
     *
     * stepsOf("context")
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", steps -> steps
     *         .step("Inner step 1", ctx -> {
     *             ...
     *         })
     *         .step("Inner step 2", ctx -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @param context the context
     * @param <T>     the context type
     * @return context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static <T> CtxSteps<T> of(final T context) {
        return Xteps.stepsOf(context);
    }

    /**
     * Returns a context steps of given context. Non-reporting method.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.stepsOf;
     *
     * stepsOf("context")
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", steps -> steps
     *         .step("Inner step 1", ctx -> {
     *             ...
     *         })
     *         .step("Inner step 2", ctx -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @param context the context
     * @param <T>     the context type
     * @return context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static <T> CtxSteps<T> stepsOf(final T context) {
        return CACHED_BASE.get().steps().toContext(context);
    }

    /**
     * Returns a context steps of given context. Non-reporting method.
     * Alias for {@link #stepsOf(ThrowingSupplier)} method.<br>
     * Code example:
     * <pre>{@code
     * import com.plugatar.xteps.Xteps;
     *
     * Xteps.of(() -> context)
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", steps -> steps
     *         .step("Inner step 1", ctx -> {
     *             ...
     *         })
     *         .step("Inner step 2", ctx -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @param contextSupplier the context supplier
     * @param <T>             the context type
     * @param <TH>            the {@code contextSupplier} exception type
     * @return context steps
     * @throws TH                if {@code contextSupplier} threw exception
     * @throws ArgumentException if {@code contextSupplier} is null
     * @throws ConfigException   if Xteps configuration is incorrect
     */
    public static <T, TH extends Throwable> CtxSteps<T> of(
        final ThrowingSupplier<? extends T, ? extends TH> contextSupplier
    ) throws TH {
        return Xteps.stepsOf(contextSupplier);
    }

    /**
     * Returns a context steps of given context. Non-reporting method.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.stepsOf;
     *
     * stepsOf(() -> context)
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", steps -> steps
     *         .step("Inner step 1", ctx -> {
     *             ...
     *         })
     *         .step("Inner step 2", ctx -> {
     *             ...
     *         })
     *     );
     * }</pre>
     *
     * @param contextSupplier the context supplier
     * @param <T>             the context type
     * @param <TH>            the {@code contextSupplier} exception type
     * @return context steps
     * @throws TH                if {@code contextSupplier} threw exception
     * @throws ArgumentException if {@code contextSupplier} is null
     * @throws ConfigException   if Xteps configuration is incorrect
     */
    public static <T, TH extends Throwable> CtxSteps<T> stepsOf(
        final ThrowingSupplier<? extends T, ? extends TH> contextSupplier
    ) throws TH {
        return CACHED_BASE.get().steps().toContext(contextSupplier);
    }

    /**
     * Performs empty step with given name and returns no context steps.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.emptyStep;
     *
     * emptyStep("Step 1");
     * emptyStep("Step 2");
     * }</pre>
     *
     * @param stepName the step name
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static void emptyStep(final String stepName) {
        CACHED_BASE.get().steps().emptyStep(stepName);
    }

    /**
     * Performs given step with given name and returns no context steps.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.step;
     *
     * step("Step 1", () -> {
     *     ...
     * });
     * step("Step 2", () -> {
     *     step("Inner step 1", () -> {
     *         ...
     *     });
     *     step("Inner step 2", () -> {
     *         ...
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <TH>     the {@code step} exception type
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static <TH extends Throwable> void step(
        final String stepName,
        final ThrowingRunnable<? extends TH> step
    ) throws TH {
        CACHED_BASE.get().steps().step(stepName, step);
    }

    /**
     * Performs given step with given name and returns the step result.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.step;
     * import static com.plugatar.xteps.Xteps.stepTo;
     *
     * String step1Result = stepTo("Step 1", () -> {
     *     ...
     *     return "result";
     * });
     * String step2Result = stepTo("Step 2", () -> {
     *     step("Inner step 1", () -> {
     *         ...
     *     });
     *     return stepTo("Inner step 2", () -> {
     *         ...
     *         return "result";
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <R>      the result type
     * @param <TH>     the {@code step} exception type
     * @return {@code step} result
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static <R, TH extends Throwable> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ? extends TH> step
    ) throws TH {
        return CACHED_BASE.get().steps().stepTo(stepName, step);
    }
}
