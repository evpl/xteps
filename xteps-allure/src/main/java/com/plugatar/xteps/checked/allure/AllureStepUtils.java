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
package com.plugatar.xteps.checked.allure;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.util.ResultsUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class. Allure step utils.
 */
public final class AllureStepUtils {

    /**
     * Utility class ctor.
     */
    private AllureStepUtils() {
    }

    /**
     * Updates the current step name.
     *
     * @param name the step name
     * @return step name
     */
    public static String stepName(final String name) {
        Allure.getLifecycle().updateStep(stepResult -> stepResult.setName(name));
        return name;
    }

    /**
     * Updates the current step name.
     *
     * @param updateFunction the update function
     * @param <E>            the {@code updateFunction} exception type
     * @return step name
     * @throws XtepsException if {@code updateFunction} is null
     * @throws E              if {@code updateFunction} threw exception
     */
    public static <E extends Throwable> String stepName(
        final ThrowingFunction<String, String, ? extends E> updateFunction
    ) throws E {
        if (updateFunction == null) { throw new XtepsException("updateFunction arg is null"); }
        final AtomicReference<String> newNameRef = new AtomicReference<>();
        Allure.getLifecycle().updateStep(stepResult -> {
            final String newName = ThrowingFunction.unchecked(updateFunction).apply(stepResult.getName());
            newNameRef.set(newName);
            stepResult.setName(newName);
        });
        return newNameRef.get();
    }

    /**
     * Updates the current step description.
     *
     * @param description the step description
     * @return step description
     */
    public static String stepDescription(final String description) {
        Allure.getLifecycle().updateStep(stepResult -> stepResult.setDescription(description));
        return description;
    }

    /**
     * Updates the current step description.
     *
     * @param updateFunction the update function
     * @param <E>            the {@code updateFunction} exception type
     * @return step description
     * @throws XtepsException if {@code updateFunction} is null
     * @throws E              if {@code updateFunction} threw exception
     */
    public static <E extends Throwable> String stepDescription(
        final ThrowingFunction<String, String, ? extends E> updateFunction
    ) throws E {
        if (updateFunction == null) { throw new XtepsException("updateFunction arg is null"); }
        final AtomicReference<String> newDescriptionRef = new AtomicReference<>();
        Allure.getLifecycle().updateStep(stepResult -> {
            final String newDescription = ThrowingFunction.unchecked(updateFunction).apply(stepResult.getDescription());
            newDescriptionRef.set(newDescription);
            stepResult.setDescription(newDescription);
        });
        return newDescriptionRef.get();
    }

    /**
     * Adds parameter to the current step.
     *
     * @param name  the name
     * @param value the value
     * @param <T>   the value type
     * @return the value
     */
    public static <T> T stepParameter(final String name,
                                      final T value) {
        return stepParameter(name, value, null, null);
    }

    /**
     * Adds parameter to the current step.
     *
     * @param name     the name
     * @param value    the value
     * @param excluded true if parameter should be excluded from history key generation, false otherwise
     * @param <T>      the value type
     * @return the value
     */
    public static <T> T stepParameter(final String name,
                                      final T value,
                                      final Boolean excluded) {
        return stepParameter(name, value, excluded, null);
    }

    /**
     * Adds parameter to the current step.
     *
     * @param name  the name
     * @param value the value
     * @param mode  the mode
     * @param <T>   the value type
     * @return the value
     */
    public static <T> T stepParameter(final String name,
                                      final T value,
                                      final Parameter.Mode mode) {
        return stepParameter(name, value, null, mode);
    }

    /**
     * Adds parameter to the current step.
     *
     * @param name     the name
     * @param value    the value
     * @param excluded true if parameter should be excluded from history key generation, false otherwise
     * @param mode     the mode
     * @param <T>      the type of value
     * @return the value
     */
    public static <T> T stepParameter(final String name,
                                      final T value,
                                      final Boolean excluded,
                                      final Parameter.Mode mode) {
        final Parameter parameter = ResultsUtils.createParameter(name, value, excluded, mode);
        Allure.getLifecycle().updateStep(stepResult -> stepResult.getParameters().add(parameter));
        return value;
    }

    /**
     * Adds parameter to the current step.
     *
     * @param updateConsumer the new parameter update consumer
     * @param <E>            the {@code updateConsumer} exception type
     * @throws XtepsException if {@code updateConsumer} is null
     * @throws E              if {@code updateConsumer} threw exception
     */
    public static <E extends Throwable> void stepParameter(
        final ThrowingConsumer<Parameter, ? extends E> updateConsumer
    ) throws E {
        if (updateConsumer == null) { throw new XtepsException("updateConsumer arg is null"); }
        final Parameter parameter = new Parameter();
        updateConsumer.accept(parameter);
        Allure.getLifecycle().updateStep(stepResult -> stepResult.getParameters().add(parameter));
    }
}
