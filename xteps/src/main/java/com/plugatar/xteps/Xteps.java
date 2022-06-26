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

import com.plugatar.xteps.core.CtxStepsChain;
import com.plugatar.xteps.core.InitialStepsChain;
import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.XtepsException;
import com.plugatar.xteps.core.chain.InitialStepsChainImpl;
import com.plugatar.xteps.core.reporter.DefaultStepReporter;
import com.plugatar.xteps.util.function.ThrowingRunnable;
import com.plugatar.xteps.util.function.ThrowingSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class. Main Xteps API.
 *
 * @see Unchecked
 * @see <a href="https://github.com/evpl/xteps/blob/master/README.md">README</a>
 */
public final class Xteps {

    /**
     * Cached InitialStepsChain instance supplier.
     */
    private static final Supplier<InitialStepsChain> INITIAL_STEPS_CHAIN_SUPPLIER = new Supplier<InitialStepsChain>() {
        private volatile InitialStepsChain instance = null;

        @Override
        public InitialStepsChain get() {
            InitialStepsChain result;
            if ((result = this.instance) == null) {
                synchronized (this) {
                    if ((result = this.instance) == null) {
                        result = new InitialStepsChainSupplier().get();
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

    /**
     * Performs empty step with given name.<br>
     * Code example:
     * <pre>{@code
     * step("Step 1");
     * }</pre>
     *
     * @param stepName the step name
     * @throws XtepsException if {@code stepName} is null
     *                        or if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     */
    public static void step(final String stepName) {
        INITIAL_STEPS_CHAIN_SUPPLIER.get().step(stepName);
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
     *     step("Inner step 1", () -> {
     *         ...
     *     });
     * });
     * }</pre>
     *
     * @param stepName the step name
     * @param step     the step
     * @param <E>      the {@code step} exception type
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <E extends Throwable> void step(
        final String stepName,
        final ThrowingRunnable<? extends E> step
    ) throws E {
        INITIAL_STEPS_CHAIN_SUPPLIER.get().step(stepName, step);
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
     *     return stepTo("Inner step 1", () -> {
     *         ...
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
     * @throws XtepsException if {@code stepName} or {@code step} is null
     *                        or if Xteps configuration is incorrect
     *                        or if it's impossible to correctly report the step
     * @throws E              if {@code step} threw exception
     */
    public static <R, E extends Throwable> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        return INITIAL_STEPS_CHAIN_SUPPLIER.get().stepTo(stepName, step);
    }

    /**
     * Returns initial steps chain.<br>
     * Code example:
     * <pre>{@code
     * stepsChain()
     *     .step("Step 1", () -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", stepsChain -> stepsChain
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
     * @throws XtepsException if Xteps configuration is incorrect
     */
    public static InitialStepsChain stepsChain() {
        return INITIAL_STEPS_CHAIN_SUPPLIER.get();
    }

    /**
     * Returns a contextual steps chain of given context.<br>
     * Code example:
     * <pre>{@code
     * stepsChainOf("context")
     *     .step("Step 1", ctx -> {
     *         ...
     *     })
     *     .nestedSteps("Step 2", stepsChain -> stepsChain
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
     * @param <C>     the context type
     * @return context steps
     * @throws XtepsException if Xteps configuration is incorrect
     */
    public static <C> CtxStepsChain<C, InitialStepsChain> stepsChainOf(final C context) {
        return INITIAL_STEPS_CHAIN_SUPPLIER.get().withContext(context);
    }

    /**
     * {@link Supplier} implementation for {@link InitialStepsChain}.
     */
    static final class InitialStepsChainSupplier implements Supplier<InitialStepsChain> {
        private final Properties properties;

        /**
         * Ctor.
         */
        InitialStepsChainSupplier() {
            this.properties = systemPropertiesWithFile("xteps.properties");
        }

        /**
         * Returns NoCtxSteps instance.
         *
         * @return NoCtxSteps instance
         * @throws XtepsException if it's impossible to correctly instantiate NoCtxSteps
         */
        @Override
        public InitialStepsChain get() {
            final List<StepListener> listeners = new ArrayList<>();
            if (booleanProperty(this.properties, "xteps.enabled", true)) {
                if (booleanProperty(this.properties, "xteps.spi", true)) {
                    listeners.addAll(listenersBySPI());
                }
                listeners.addAll(
                    listenersByClassNames(
                        stringListProperty(this.properties, "xteps.listeners", ",", Collections.emptyList())
                    )
                );
            }
            return new InitialStepsChainImpl(
                new DefaultStepReporter(listeners.toArray(new StepListener[0]))
            );
        }

        /**
         * Returns properties in priority:
         * <br>1. System properties
         * <br>2. Current thread ClassLoader properties file
         * <br>3. System ClassLoader properties file
         *
         * @return the properties
         */
        private static Properties systemPropertiesWithFile(final String propertiesFilePath) {
            final Properties properties = new Properties();
            try (final InputStream stream =
                     ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFilePath)) {
                if (stream != null) {
                    properties.load(stream);
                }
            } catch (final IOException ignored) { }
            try (final InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFilePath)) {
                if (stream != null) {
                    properties.load(stream);
                }
            } catch (final IOException ignored) { }
            properties.putAll(System.getProperties());
            return properties;
        }

        /**
         * Returns boolean property value, or {@code defaultValue} if property missing.
         *
         * @param properties   the properties
         * @param propertyName the property name
         * @param defaultValue the default value
         * @return property value
         */
        private static boolean booleanProperty(final Properties properties,
                                               final String propertyName,
                                               final boolean defaultValue) {
            final String propertyValue = properties.getProperty(propertyName);
            if (propertyValue == null) {
                return defaultValue;
            }
            final String trimmedPropertyValue = propertyValue.trim();
            if (trimmedPropertyValue.isEmpty()) {
                return defaultValue;
            }
            return trimmedPropertyValue.equalsIgnoreCase("true");
        }

        /**
         * Returns list of strings property value or {@code defaultValue} if property missing.
         *
         * @param properties   the properties
         * @param propertyName the property name
         * @param delimiter    the delimiter
         * @param defaultValue the default value
         * @return property value
         */
        private static List<String> stringListProperty(final Properties properties,
                                                       final String propertyName,
                                                       final String delimiter,
                                                       final List<String> defaultValue) {
            final String propertyValue = properties.getProperty(propertyName);
            if (propertyValue == null) {
                return defaultValue;
            }
            final List<String> stringList = Arrays.stream(propertyValue.split(delimiter))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
            if (stringList.isEmpty()) {
                return defaultValue;
            }
            return stringList;
        }

        /**
         * Returns StepListener instances list provided by SPI mechanism.
         *
         * @return StepListener instances list
         * @throws XtepsException if it's impossible to instantiate any listener
         */
        private static List<StepListener> listenersBySPI() {
            final List<StepListener> listeners = new ArrayList<>();
            try {
                for (final StepListener listener : ServiceLoader.load(StepListener.class)) {
                    listeners.add(listener);
                }
            } catch (final Exception ex) {
                throw new XtepsException("Cannot instantiate StepListener by SPI cause " + ex, ex);
            }
            return listeners;
        }

        /**
         * Returns StepListener instances list provided by classes names.
         *
         * @param classNames the class names
         * @return StepListener instances list
         * @throws XtepsException if it's impossible to instantiate any listener
         */
        private static List<StepListener> listenersByClassNames(final List<String> classNames) {
            final List<StepListener> listeners = new ArrayList<>();
            for (final String className : classNames) {
                final StepListener listener;
                try {
                    listener = (StepListener) Class.forName(className).getConstructor().newInstance();
                } catch (final Exception ex) {
                    throw new XtepsException("Cannot instantiate StepListener " + className + " cause " + ex, ex);
                }
                listeners.add(listener);
            }
            return listeners;
        }
    }
}
