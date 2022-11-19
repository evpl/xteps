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
package com.plugatar.xteps.unchecked.qase;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import io.qase.api.StepStorage;
import io.qase.client.model.ResultCreateStepsInner;

/**
 * Utility class. Qase step utils.
 */
public final class QaseStepUtils {

    /**
     * Utility class ctor.
     */
    private QaseStepUtils() {
    }

    /**
     * Updates the current step name.
     *
     * @param name the step name
     * @return step name
     */
    public static String stepName(final String name) {
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        if (step != null) {
            StepStorage.getCurrentStep().action(name);
        }
        return name;
    }

    /**
     * Updates the current step name.
     *
     * @param updateFunction the update function
     * @return step name
     * @throws XtepsException if {@code updateFunction} is null
     */
    public static String stepName(
        final ThrowingFunction<String, String, ?> updateFunction
    ) {
        if (updateFunction == null) { throw new XtepsException("updateFunction arg is null"); }
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        final String newName;
        if (step != null) {
            newName = ThrowingFunction.unchecked(updateFunction).apply(step.getAction());
            step.action(newName);
        } else {
            newName = ThrowingFunction.unchecked(updateFunction).apply(null);
        }
        return newName;
    }

    /**
     * Updates the current step description.
     *
     * @param description the step description
     * @return step description
     */
    public static String stepDescription(final String description) {
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        if (step != null) {
            StepStorage.getCurrentStep().comment(description);
        }
        return description;
    }

    /**
     * Updates the current step description.
     *
     * @param updateFunction the update function
     * @return step description
     * @throws XtepsException if {@code updateFunction} is null
     */
    public static String stepDescription(
        final ThrowingFunction<String, String, ?> updateFunction
    ) {
        if (updateFunction == null) { throw new XtepsException("updateFunction arg is null"); }
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        final String newDescription;
        if (step != null) {
            newDescription = ThrowingFunction.unchecked(updateFunction).apply(step.getComment());
            step.comment(newDescription);
        } else {
            newDescription = ThrowingFunction.unchecked(updateFunction).apply(null);
        }
        return newDescription;
    }

    /**
     * Updates the current step.
     *
     * @param updateConsumer the update consumer
     * @throws XtepsException if {@code updateConsumer} is null
     */
    public static void updateStep(
        final ThrowingConsumer<ResultCreateStepsInner, ?> updateConsumer
    ) {
        if (updateConsumer == null) { throw new XtepsException("updateConsumer arg is null"); }
        ThrowingConsumer.unchecked(updateConsumer).accept(StepStorage.getCurrentStep());
    }
}
