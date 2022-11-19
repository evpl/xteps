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
package com.plugatar.xteps.checked.qase;

import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import io.qase.api.StepStorage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link QaseStepUtils}.
 */
final class QaseStepUtilsTest {

    @Test
    void stepNameMethodWithString() {
        StepStorage.startStep();
        StepStorage.getCurrentStep().action("step name");

        final String methodResult = QaseStepUtils.stepName("new step name");
        assertThat(methodResult).isEqualTo("new step name");
        assertThat(StepStorage.getCurrentStep().getAction()).isEqualTo("new step name");
        StepStorage.stopStep();
    }

    @Test
    void stepNameMethodWithFunctionThrowsExceptionForNullArg() {
        StepStorage.startStep();
        StepStorage.getCurrentStep().action("step name");

        assertThatCode(() -> QaseStepUtils.stepName((ThrowingFunction<String, String, ?>) null))
            .isInstanceOf(XtepsException.class);
        StepStorage.stopStep();
    }

    @Test
    void stepNameMethodWithFunction() {
        StepStorage.startStep();
        StepStorage.getCurrentStep().action("step name");

        final String methodResult = QaseStepUtils.stepName(name -> "new " + name);
        assertThat(methodResult).isEqualTo("new step name");
        assertThat(StepStorage.getCurrentStep().getAction()).isEqualTo("new step name");
        StepStorage.stopStep();
    }

    @Test
    void stepDescriptionMethodWithString() {
        StepStorage.startStep();
        StepStorage.getCurrentStep().action("step description");

        final String methodResult = QaseStepUtils.stepDescription("new step description");
        assertThat(methodResult).isEqualTo("new step description");
        assertThat(StepStorage.getCurrentStep().getComment()).isEqualTo("new step description");
        StepStorage.stopStep();
    }

    @Test
    void stepDescriptionMethodWithFunctionThrowsExceptionForNullArg() {
        StepStorage.startStep();
        StepStorage.getCurrentStep().action("step description");

        assertThatCode(() -> QaseStepUtils.stepDescription((ThrowingFunction<String, String, ?>) null))
            .isInstanceOf(XtepsException.class);
        StepStorage.stopStep();
    }

    @Test
    void stepDescriptionMethodWithFunction() {
        StepStorage.startStep();
        StepStorage.getCurrentStep().comment("step description");

        final String methodResult = QaseStepUtils.stepDescription(description -> "new " + description);
        assertThat(methodResult).isEqualTo("new step description");
        assertThat(StepStorage.getCurrentStep().getComment()).isEqualTo("new step description");
        StepStorage.stopStep();
    }

    @Test
    void updateStepMethod() {
        final String stepName = "step name";
        final String stepDescription = "step description";
        StepStorage.startStep();

        QaseStepUtils.updateStep(stepResult -> stepResult.action(stepName).comment(stepDescription));
        assertThat(StepStorage.getCurrentStep().getAction()).isEqualTo(stepName);
        assertThat(StepStorage.getCurrentStep().getComment()).isEqualTo(stepDescription);
        StepStorage.stopStep();
    }
}
