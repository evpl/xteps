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

import com.plugatar.xteps.base.handler.DefaultExceptionHandler;
import com.plugatar.xteps.base.handler.FakeExceptionHandler;
import com.plugatar.xteps.base.hook.DefaultHooksContainer;
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
 * XtepsBase provider.
 */
final class XtepsBaseProvider {

    /**
     * Cached XtepsBase instance supplier.
     */
    static final Supplier<XtepsBase> CACHED_XTEPS_BASE = new Supplier<XtepsBase>() {
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

    private XtepsBaseProvider() {
    }

    static XtepsBase newXtepsBaseByProperties() {
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
            ? new DefaultExceptionHandler()
            : new FakeExceptionHandler();
        final HooksOrder defaultHooksOrder = hooksOrderProperty(properties, "xteps.defaultHooksOrder",
            HooksOrder.FROM_LAST);
        final long threadHookInterval = longPropertyInRange(properties, "xteps.threadHooksThreadInterval",
            0L, Long.MAX_VALUE, 100L);
        final int threadHookPriority = intPropertyInRange(properties, "xteps.threadHooksThreadPriority",
            Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, Thread.NORM_PRIORITY);
        final ThrowingSupplier<HooksContainer, RuntimeException> hooksContainerGenerator =
            () -> new DefaultHooksContainer(defaultHooksOrder);
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
            public ThrowingSupplier<HooksContainer, RuntimeException> hooksContainerGenerator() {
                return hooksContainerGenerator;
            }

            @Override
            public HooksOrder defaultHooksOrder() {
                return defaultHooksOrder;
            }

            @Override
            public long threadHooksThreadInterval() {
                return threadHookInterval;
            }

            @Override
            public int threadHooksThreadPriority() {
                return threadHookPriority;
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
        if (trimmedPropertyValue.equalsIgnoreCase("false")) {
            return false;
        }
        if (trimmedPropertyValue.equalsIgnoreCase("true")) {
            return true;
        }
        throw throwXtepsPropertyException(propertyName, propertyValue);
    }

    private static long longPropertyInRange(final Properties properties,
                                            final String propertyName,
                                            final long min,
                                            final long max,
                                            final long defaultValue) {
        final String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        final String trimmedPropertyValue = propertyValue.trim();
        if (trimmedPropertyValue.isEmpty()) {
            return defaultValue;
        }
        try {
            final long longValue = Long.parseLong(trimmedPropertyValue);
            if (longValue < min || longValue > max) {
                throw throwXtepsPropertyException(propertyName, propertyValue);
            }
            return longValue;
        } catch (final NumberFormatException ex) {
            throw throwXtepsPropertyException(propertyName, propertyValue);
        }
    }

    private static int intPropertyInRange(final Properties properties,
                                          final String propertyName,
                                          final int min,
                                          final int max,
                                          final int defaultValue) {
        final String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        final String trimmedPropertyValue = propertyValue.trim();
        if (trimmedPropertyValue.isEmpty()) {
            return defaultValue;
        }
        try {
            final int intValue = Integer.parseInt(trimmedPropertyValue);
            if (intValue < min || intValue > max) {
                throw throwXtepsPropertyException(propertyName, propertyValue);
            }
            return intValue;
        } catch (final NumberFormatException ex) {
            throw throwXtepsPropertyException(propertyName, propertyValue);
        }
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

    private static HooksOrder hooksOrderProperty(final Properties properties,
                                                 final String propertyName,
                                                 final HooksOrder defaultValue) {
        final String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        final String trimmedPropertyValue = propertyValue.trim();
        if (trimmedPropertyValue.isEmpty()) {
            return defaultValue;
        }
        for (HooksOrder currentOrder : HooksOrder.values()) {
            if (trimmedPropertyValue.equalsIgnoreCase(currentOrder.name())) {
                return currentOrder;
            }
        }
        throw throwXtepsPropertyException(propertyName, propertyValue);
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

    private static XtepsException throwXtepsPropertyException(final String propertyName,
                                                              final String propertyValue) {
        throw new XtepsException("Incorrect value " + propertyValue + " for " + propertyName + "property");
    }
}
