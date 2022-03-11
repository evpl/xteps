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
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepNameFormatException;
import com.plugatar.xteps.core.exception.StepWriteException;
import com.plugatar.xteps.core.exception.XtepsException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;

import java.util.function.Supplier;

/**
 * Utility class. Xteps API.
 *
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class Xteps {

    /**
     * Cached NoCtxSteps instance supplier.
     */
    private static final Supplier<NoCtxSteps> NO_CTX_STEPS = new Supplier<NoCtxSteps>() {
        private NoCtxSteps instance = null;

        @Override
        public NoCtxSteps get() {
            NoCtxSteps result;
            if ((result = this.instance) == null) {
                synchronized (this) {
                    if ((result = this.instance) == null) {
                        result = StepsProvider.stepsByConfig();
                        this.instance = result;
                    }
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

    /**
     * Returns no context steps. Alias for {@link #steps()} method.<br>
     * Code example:
     * <pre>{@code
     * Xteps.of()
     *     .step("Step 1", () -> {
     *         ...
     *     })
     *     .step("Step 2", () -> {
     *         ...
     *     });
     * }</pre>
     *
     * @return no context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static NoCtxSteps of() {
        return NO_CTX_STEPS.get();
    }

    /**
     * Returns no context steps.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.steps;
     *
     * steps()
     *     .step("Step 1", () -> {
     *         ...
     *     })
     *     .step("Step 2", () -> {
     *         ...
     *     });
     * }</pre>
     *
     * @return no context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static NoCtxSteps steps() {
        return NO_CTX_STEPS.get();
    }

    /**
     * Returns a context steps of given context. Alias for {@link #stepsOf(Object)} method.<br>
     * Code example:
     * <pre>{@code
     * Xteps.of(context)
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .step("Step 2", ctx -> {
     *         ...
     *     });
     * }</pre>
     *
     * @param context the context
     * @param <T>     the context type
     * @return context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static <T> CtxSteps<T> of(final T context) {
        return NO_CTX_STEPS.get().toContext(context);
    }

    /**
     * Returns a context steps of given context.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.stepsOf;
     *
     * stepsOf(context)
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .step("Step 2", ctx -> {
     *         ...
     *     });
     * }</pre>
     *
     * @param context the context
     * @param <T>     the context type
     * @return context steps
     * @throws ConfigException if Xteps configuration is incorrect
     */
    public static <T> CtxSteps<T> stepsOf(final T context) {
        return NO_CTX_STEPS.get().toContext(context);
    }

    /**
     * Returns a context steps of given context. Alias for {@link #stepsOf(ThrowingSupplier)} method.<br>
     * Code example:
     * <pre>{@code
     * Xteps.of(() -> context)
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .step("Step 2", ctx -> {
     *         ...
     *     });
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
        return NO_CTX_STEPS.get().toContext(contextSupplier);
    }

    /**
     * Returns a context steps of given context.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.stepsOf;
     *
     * stepsOf(() -> context)
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .step("Step 2", ctx -> {
     *         ...
     *     });
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
        return NO_CTX_STEPS.get().toContext(contextSupplier);
    }

    /**
     * Performs empty step with given name and returns no context steps.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.emptyStep;
     *
     * emptyStep("Step 1").emptyStep("Step 2");
     * }</pre>
     *
     * @param stepName the step name
     * @return no context steps
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static NoCtxSteps emptyStep(final String stepName) {
        return NO_CTX_STEPS.get().emptyStep(stepName);
    }

    /**
     * Performs given step with given name and returns no context steps.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.step;
     *
     * step("Step 1", () -> {
     *     ...
     * }).step("Step 2", () -> {
     *     ...
     * }).step("Step 3", () -> {
     *     ...
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <TH>     the {@code step} exception type
     * @return no context steps
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static <TH extends Throwable> NoCtxSteps step(
        final String stepName,
        final ThrowingRunnable<? extends TH> step
    ) throws TH {
        return NO_CTX_STEPS.get().step(stepName, step);
    }

    /**
     * Performs given step with given name and returns a context steps.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.stepToContext;
     *
     * stepToContext("Step 1", () -> context)
     *     .step("Step 2", ctx -> {
     *         ...
     *     })
     *     .step("Step 3", ctx -> {
     *         ...
     *     });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <T>      the context type
     * @param <TH>     the {@code step} exception type
     * @return context steps
     * @throws TH                      if {@code step} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code step} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the step
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static <T, TH extends Throwable> CtxSteps<T> stepToContext(
        String stepName,
        ThrowingSupplier<? extends T, ? extends TH> step
    ) throws TH {
        return NO_CTX_STEPS.get().stepToContext(stepName, step);
    }

    /**
     * Performs given step with given name and returns the step result.<br>
     * Code example:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.stepTo;
     *
     * String stepResult = stepTo("Step 1", () -> "result");
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
        return NO_CTX_STEPS.get().stepTo(stepName, step);
    }

    /**
     * Performs the step with given name and nested steps and returns no context steps.<br>
     * Code example 1:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.nestedSteps;
     *
     * nestedSteps("Step 1", steps -> steps
     *     .emptyStep("Inner step 1")
     *     .step("Inner step 2", () -> {
     *         ...
     *     })
     * ).step("Step 2", () -> {
     *     ...
     * });
     * }</pre>
     * Code example 2:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.nestedSteps;
     * import static com.plugatar.xteps.Xteps.emptyStep;
     * import static com.plugatar.xteps.Xteps.step;
     *
     * nestedSteps("Step 1", ignored -> {
     *     emptyStep("Inner step 1");
     *     step("Inner step 2", () -> {
     *         ...
     *     });
     * }).step("Step 2", () -> {
     *     ...
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param steps    the nested steps
     * @param <TH>     the {@code steps} exception type
     * @return no context steps
     * @throws TH                      if {@code steps} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code steps} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the steps
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static <TH extends Throwable> NoCtxSteps nestedSteps(
        final String stepName,
        final ThrowingConsumer<NoCtxSteps, ? extends TH> steps
    ) throws TH {
        return NO_CTX_STEPS.get().nestedSteps(stepName, steps);
    }

    /**
     * Performs step with given name and nested steps and returns the nested steps result.<br>
     * Code example 1:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.nestedStepsTo;
     *
     * String stepResult = nestedStepsTo("Step 1", steps -> {
     *     steps
     *         .emptyStep("Inner step 1")
     *         .step("Inner step 2", () -> {
     *             ...
     *         });
     *     return "result";
     * });
     * }</pre>
     * Code example 2:
     * <pre>{@code
     * import static com.plugatar.xteps.Xteps.nestedStepsTo;
     *
     * nestedStepsTo("Step 1", steps -> steps
     *     .emptyStep("Inner step 1")
     *     .stepToContext("Inner step 2", () -> context)
     * ).step("Step 2", ctx -> {
     *     ...
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param steps    the nested steps
     * @param <R>      the result type
     * @param <TH>     the {@code steps} exception type
     * @return {@code steps} result
     * @throws TH                      if {@code steps} threw exception
     * @throws ArgumentException       if {@code stepName} is null or empty or if {@code steps} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     * @throws StepWriteException      if it's impossible to correctly report the steps
     * @throws ConfigException         if Xteps configuration is incorrect
     */
    public static <R, TH extends Throwable> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<NoCtxSteps, ? extends R, ? extends TH> steps
    ) throws TH {
        return NO_CTX_STEPS.get().nestedStepsTo(stepName, steps);
    }

    /**
     * Thrown to indicate incorrect Xteps configuration.
     */
    public static final class ConfigException extends XtepsException {

        /**
         * Ctor.
         */
        ConfigException() {
            super();
        }

        /**
         * Ctor.
         *
         * @param message the message
         */
        ConfigException(final String message) {
            super(message);
        }

        /**
         * Ctor.
         *
         * @param cause the cause
         */
        ConfigException(final Throwable cause) {
            super(cause);
        }

        /**
         * Ctor.
         *
         * @param message the message
         * @param cause   the cause
         */
        ConfigException(final String message,
                        final Throwable cause) {
            super(message, cause);
        }
    }
}
