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
package com.plugatar.xteps.base;

import com.plugatar.xteps.base.container.DefaultHookContainer;
import com.plugatar.xteps.base.handler.FakeExceptionHandler;
import com.plugatar.xteps.base.handler.ThreadLocalMemorizingCleanStackTraceExceptionHandler;
import com.plugatar.xteps.base.reporter.DefaultStepReporter;
import com.plugatar.xteps.base.reporter.FakeStepReporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Xteps base.
 */
public interface XtepsBase {

    /**
     * Returns StepReporter.
     *
     * @return StepReporter
     */
    StepReporter stepReporter();

    /**
     * Returns ExceptionHandler.
     *
     * @return ExceptionHandler
     */
    ExceptionHandler exceptionHandler();

    /**
     * Returns HookContainer generator.
     *
     * @return HookContainer generator
     */
    ThrowingSupplier<HookContainer, RuntimeException> hookContainerGenerator();

    /**
     * Returns cached XtepsBase instance.
     *
     * @return cached XtepsBase instance
     */
    static XtepsBase cached() {
        return ByProperties.CACHED.get();
    }

    final class ByProperties {
        private static final Supplier<XtepsBase> CACHED = new Supplier<XtepsBase>() {
            private volatile XtepsBase instance = null;

            @Override
            public XtepsBase get() {
                XtepsBase result;
                if ((result = this.instance) == null) {
                    synchronized (this) {
                        if ((result = this.instance) == null) {
                            result = newXtepsBaseByProperties();
                            this.instance = result;
                        }
                        return result;
                    }
                }
                return result;
            }
        };

        private ByProperties() {
        }

        private static XtepsBase newXtepsBaseByProperties() {
            final Properties properties = systemPropertiesWithFile("xteps.properties");
            final StepReporter stepReporter;
            if (booleanProperty(properties, "xteps.enabled", true)) {
                final List<StepListener> listeners = new ArrayList<>();
                if (booleanProperty(properties, "xteps.spi", true)) {
                    listeners.addAll(listenersBySPI());
                }
                listeners.addAll(listenersByClassNames(stringListProperty(
                    properties, "xteps.listeners", ",", Collections.emptyList()
                )));
                stepReporter = listeners.isEmpty()
                    ? new FakeStepReporter()
                    : new DefaultStepReporter(uniqueByClass(listeners).toArray(new StepListener[0]));
            } else {
                stepReporter = new FakeStepReporter();
            }
            final ExceptionHandler exceptionHandler = booleanProperty(properties, "xteps.cleanStackTrace", true)
                ? new ThreadLocalMemorizingCleanStackTraceExceptionHandler()
                : new FakeExceptionHandler();
            return new XtepsBase() {
                @Override
                public StepReporter stepReporter() {
                    return stepReporter;
                }

                @Override
                public ExceptionHandler exceptionHandler() {
                    return exceptionHandler;
                }

                @Override
                public ThrowingSupplier<HookContainer, RuntimeException> hookContainerGenerator() {
                    return DefaultHookContainer::new;
                }
            };
        }

        private static Properties systemPropertiesWithFile(final String propertiesFilePath) {
            final Properties properties = new Properties();
            try (final InputStream stream =
                     ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFilePath)) {
                if (stream != null) {
                    properties.load(stream);
                }
            } catch (final Exception ignored) { }
            try (final InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFilePath)) {
                if (stream != null) {
                    properties.load(stream);
                }
            } catch (final Exception ignored) { }
            properties.putAll(System.getProperties());
            return properties;
        }

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

        private static <T> List<T> uniqueByClass(final List<T> listeners) {
            final Set<Class<?>> classes = Collections.newSetFromMap(new IdentityHashMap<>(8));
            return listeners.stream()
                .filter(listener -> classes.add(listener.getClass()))
                .collect(Collectors.toList());
        }
    }
}
