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

import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.formatter.DefaultStepNameFormatter;
import com.plugatar.xteps.core.listener.ComplexStepListener;
import com.plugatar.xteps.core.steps.NoCtxStepsOf;
import com.plugatar.xteps.core.writer.DefaultStepWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class with single available method. {@link #stepsByConfig()} method
 * generate NoCtxSteps instance from the Xteps framework configuration.
 */
final class StepsProvider {

    /**
     * Utility class ctor.
     */
    private StepsProvider() {
    }

    /**
     * Returns generated NoCtxSteps instance from the framework configuration.
     *
     * @return generated NoCtxSteps
     * @throws Xteps.ConfigException if it's impossible to correctly instantiate NoCtxSteps
     */
    static NoCtxSteps stepsByConfig() {
        final Properties properties = StepsProvider.allProperties();
        final boolean enabled =
            StepsProvider.booleanProperty(properties, "xteps.enabled", true);
        final Pattern replacementPattern =
            StepsProvider.patternProperty(properties, "xteps.replacementPattern", "\\{([^}]*)}");
        final boolean fieldForceAccess =
            StepsProvider.booleanProperty(properties, "xteps.fieldForceAccess", false);
        final boolean methodForceAccess =
            StepsProvider.booleanProperty(properties, "xteps.methodForceAccess", false);
        final boolean useSPIListeners =
            StepsProvider.booleanProperty(properties, "xteps.useSPIListeners", true);
        final List<String> listenersNames =
            StepsProvider.stringListProperty(properties, "xteps.listeners", ",", Collections.emptyList());
        final StepListener listener;
        if (enabled) {
            final List<StepListener> allListeners = new ArrayList<>();
            if (useSPIListeners) {
                allListeners.addAll(StepsProvider.listenersBySPI());
            }
            if (!listenersNames.isEmpty()) {
                allListeners.addAll(StepsProvider.listenersByClassesNames(listenersNames));
            }
            if (allListeners.isEmpty()) {
                throw new Xteps.ConfigException("No one step listener found");
            }
            listener = StepsProvider.combinedListener(allListeners);
        } else {
            listener = new FakeStepListener();
        }
        try {
            return new NoCtxStepsOf(
                new DefaultStepNameFormatter(replacementPattern, fieldForceAccess, methodForceAccess),
                new DefaultStepWriter(listener)
            );
        } catch (final Exception ex) {
            throw new Xteps.ConfigException("Cannot start Xteps cause " + ex, ex);
        }
    }

    /**
     * Returns a single listener that combines given listeners.
     *
     * @param listeners the listeners
     * @return combined listener
     */
    private static StepListener combinedListener(final List<StepListener> listeners) {
        final Set<Class<?>> classes = new HashSet<>();
        final StepListener[] distinctListeners = listeners.stream()
            .filter(element -> classes.add(element.getClass()))
            .toArray(StepListener[]::new);
        return distinctListeners.length == 1
            ? distinctListeners[0]
            : new ComplexStepListener(distinctListeners);
    }

    /**
     * Returns properties in priority:
     * <br>1. System properties
     * <br>2. Current thread ClassLoader properties file
     * <br>3. System ClassLoader properties file
     *
     * @return the properties
     */
    private static Properties allProperties() {
        final String propertiesFilePath = "xteps.properties";
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
     * @throws Xteps.ConfigException if property has incorrect value (not true or false)
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
        if ("true".equalsIgnoreCase(trimmedPropertyValue)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedPropertyValue)) {
            return false;
        }
        throw new Xteps.ConfigException(propertyName + " boolean property has incorrect value = " +
            trimmedPropertyValue);
    }

    /**
     * Returns {@link Pattern} property value, or pattern of {@code defaultStringValue} if property missing.
     *
     * @param properties         the properties
     * @param propertyName       the property name
     * @param defaultStringValue the default string value
     * @return property value
     * @throws Xteps.ConfigException if property has incorrect value
     */
    private static Pattern patternProperty(final Properties properties,
                                           final String propertyName,
                                           final String defaultStringValue) {
        String propertyValue = properties.getProperty(propertyName);
        final String stringPattern = propertyValue == null || propertyValue.isEmpty() ? defaultStringValue
            : (propertyValue = propertyValue.trim()).isEmpty() ? defaultStringValue
            : propertyValue;
        try {
            return Pattern.compile(stringPattern);
        } catch (final Exception ex) {
            throw new Xteps.ConfigException(propertyName + " property has incorrect value = " + propertyValue +
                " cause " + ex, ex);
        }
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
        final String trimmedPropertyValue = propertyValue.trim();
        if (trimmedPropertyValue.isEmpty()) {
            return defaultValue;
        }
        final List<String> stringList = Arrays.stream(trimmedPropertyValue.split(delimiter))
            .map(String::trim)
            .filter(str -> !str.isEmpty())
            .collect(Collectors.toList());
        if (stringList.isEmpty()) {
            return defaultValue;
        }
        return stringList;
    }

    /**
     * Returns StepListener instances list provided by classes names.
     *
     * @param classesNames the classes names
     * @return StepListener instances list
     * @throws Xteps.ConfigException if it's impossible to instantiate any listener
     */
    private static List<StepListener> listenersByClassesNames(final List<String> classesNames) {
        final List<StepListener> listeners = new ArrayList<>(classesNames.size());
        for (final String className : classesNames) {
            final StepListener listener;
            try {
                listener = (StepListener) Class.forName(className).getConstructor().newInstance();
            } catch (final Exception ex) {
                throw new Xteps.ConfigException("Cannot instantiate StepListener " + className +
                    " cause " + ex, ex);
            }
            listeners.add(listener);
        }
        return listeners;
    }

    /**
     * Returns StepListener instances list provided by SPI mechanism.
     *
     * @return StepListener instances list
     * @throws Xteps.ConfigException if it's impossible to instantiate any listener
     */
    private static List<StepListener> listenersBySPI() {
        final List<StepListener> listeners = new ArrayList<>();
        try {
            for (final StepListener listener : ServiceLoader.load(StepListener.class)) {
                listeners.add(listener);
            }
        } catch (final Exception ex) {
            throw new Xteps.ConfigException("Cannot instantiate StepListener by SPI cause " + ex, ex);
        }
        return listeners;
    }

    /**
     * The fake step listener. It doesn't do anything.
     */
    private static class FakeStepListener implements StepListener {

        /**
         * Ctor.
         */
        private FakeStepListener() {
        }

        @Override
        public void stepStarted(final String uuid,
                                final String stepName) {
        }

        @Override
        public void stepPassed(final String uuid,
                               final String stepName) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final String stepName,
                               final Throwable throwable) {
        }
    }
}
