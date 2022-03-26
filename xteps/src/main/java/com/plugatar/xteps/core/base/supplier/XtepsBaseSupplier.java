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
package com.plugatar.xteps.core.base.supplier;

import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.XtepsBase;
import com.plugatar.xteps.core.base.XtepsBaseOf;
import com.plugatar.xteps.core.exception.ConfigException;
import com.plugatar.xteps.core.formatter.DefaultStepNameFormatter;
import com.plugatar.xteps.core.listener.ComplexStepListener;
import com.plugatar.xteps.core.listener.FakeStepListener;
import com.plugatar.xteps.core.writer.DefaultStepWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {@link Supplier} implementation for {@link XtepsBase}.
 */
public class XtepsBaseSupplier implements Supplier<XtepsBase> {
    private final Map<String, String> properties;

    /**
     * Ctor.
     */
    public XtepsBaseSupplier() {
        this.properties = systemPropertiesWithFile("xteps", "xteps.properties");
    }

    /**
     * Returns XtepsBase.
     *
     * @return XtepsBase
     * @throws ConfigException if it's impossible to correctly instantiate XtepsBase
     */
    @Override
    public final XtepsBase get() {
        final boolean enabled =
            booleanProperty(this.properties, "xteps.enabled", true);
        final Pattern replacementPattern =
            patternProperty(this.properties, "xteps.replacementPattern", "\\{([^}]*)}");
        final boolean fieldForceAccess =
            booleanProperty(this.properties, "xteps.fieldForceAccess", false);
        final boolean methodForceAccess =
            booleanProperty(this.properties, "xteps.methodForceAccess", false);
        final boolean cleanStackTrace =
            booleanProperty(this.properties, "xteps.cleanStackTrace", true);
        final boolean useSPIListeners =
            booleanProperty(this.properties, "xteps.useSPIListeners", true);
        final List<String> listenersNames =
            stringListProperty(this.properties, "xteps.listeners", ",", Collections.emptyList());
        final StepListener listener;
        if (enabled) {
            final List<StepListener> allListeners = new ArrayList<>();
            if (useSPIListeners) {
                allListeners.addAll(listenersBySPI());
            }
            if (!listenersNames.isEmpty()) {
                allListeners.addAll(listenersByClassesNames(listenersNames));
            }
            if (allListeners.isEmpty()) {
                throw new ConfigException("No one step listener found");
            }
            listener = combinedListener(allListeners);
        } else {
            listener = new FakeStepListener();
        }
        try {
            return new XtepsBaseOf(
                this.properties,
                new DefaultStepNameFormatter(replacementPattern, fieldForceAccess, methodForceAccess),
                new DefaultStepWriter(listener, cleanStackTrace)
            );
        } catch (final Exception ex) {
            throw new ConfigException("Cannot instantiate XtepsBase cause " + ex, ex);
        }
    }

    /**
     * Returns properties in priority:
     * <br>1. System properties
     * <br>2. Current thread ClassLoader properties file
     * <br>3. System ClassLoader properties file
     *
     * @return the properties
     */
    private static Map<String, String> systemPropertiesWithFile(final String propertyPrefix,
                                                                final String propertiesFilePath) {
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
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) (Map<?, ?>) properties.entrySet()
            .stream()
            .filter(entry -> {
                final Object key = entry.getKey();
                return key instanceof String
                    && entry.getValue() instanceof String
                    && ((String) key).startsWith(propertyPrefix);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new HashMap<>(map);
    }

    /**
     * Returns boolean property value, or {@code defaultValue} if property missing.
     *
     * @param properties   the properties
     * @param propertyName the property name
     * @param defaultValue the default value
     * @return property value
     * @throws ConfigException if property has incorrect value (not true or false)
     */
    private static boolean booleanProperty(final Map<String, String> properties,
                                           final String propertyName,
                                           final boolean defaultValue) {
        final String propertyValue = properties.get(propertyName);
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
        throw new ConfigException(propertyName + " boolean property has incorrect value = " + trimmedPropertyValue);
    }

    /**
     * Returns {@link Pattern} property value, or pattern of {@code defaultStringValue} if property missing.
     *
     * @param properties         the properties
     * @param propertyName       the property name
     * @param defaultStringValue the default string value
     * @return property value
     * @throws ConfigException if property has incorrect value
     */
    private static Pattern patternProperty(final Map<String, String> properties,
                                           final String propertyName,
                                           final String defaultStringValue) {
        String propertyValue = properties.get(propertyName);
        final String stringPattern = propertyValue == null || propertyValue.isEmpty() ? defaultStringValue
            : (propertyValue = propertyValue.trim()).isEmpty() ? defaultStringValue
            : propertyValue;
        try {
            return Pattern.compile(stringPattern);
        } catch (final Exception ex) {
            throw new ConfigException(propertyName + " property has incorrect value = " + propertyValue +
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
    private static List<String> stringListProperty(final Map<String, String> properties,
                                                   final String propertyName,
                                                   final String delimiter,
                                                   final List<String> defaultValue) {
        final String propertyValue = properties.get(propertyName);
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
        return Collections.unmodifiableList(new ArrayList<>(stringList));
    }

    /**
     * Returns StepListener instances list provided by classes names.
     *
     * @param classesNames the classes names
     * @return StepListener instances list
     * @throws ConfigException if it's impossible to instantiate any listener
     */
    private static List<StepListener> listenersByClassesNames(final List<String> classesNames) {
        final List<StepListener> listeners = new ArrayList<>(classesNames.size());
        for (final String className : classesNames) {
            final StepListener listener;
            try {
                listener = (StepListener) Class.forName(className).getConstructor().newInstance();
            } catch (final Exception ex) {
                throw new ConfigException("Cannot instantiate StepListener " + className +
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
     * @throws ConfigException if it's impossible to instantiate any listener
     */
    private static List<StepListener> listenersBySPI() {
        final List<StepListener> listeners = new ArrayList<>();
        try {
            for (final StepListener listener : ServiceLoader.load(StepListener.class)) {
                listeners.add(listener);
            }
        } catch (final Exception ex) {
            throw new ConfigException("Cannot instantiate StepListener by SPI cause " + ex, ex);
        }
        return listeners;
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
}
