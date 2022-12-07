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
package com.plugatar.xteps.base.qase;

import io.qase.api.StepStorage;
import io.qase.client.model.ResultCreateStepsInner;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link QaseStepListener}.
 */
final class QaseStepListenerTest {

    @Test
    void stepStartedMethodEmptyName() {
        final QaseStepListener listener = new QaseStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepDescription = "step description";

        listener.stepStarted(uuid, "", stepDescription, new Object[]{});
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        assertThat(step.getAction()).isEqualTo("Step");
        assertThat(step.getComment()).isEqualTo(stepDescription);
        assertThat(step.getStatus()).isNull();
        StepStorage.stopStep();
    }

    @Test
    void stepStartedMethodEmptyDescription() {
        final QaseStepListener listener = new QaseStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        listener.stepStarted(uuid, stepName, "", new Object[]{});
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        assertThat(step.getAction()).isEqualTo(stepName);
        assertThat(step.getComment()).isNull();
        assertThat(step.getStatus()).isNull();
        StepStorage.stopStep();
    }

    @Test
    void stepStartedMethodWithoutReplacements() {
        final QaseStepListener listener = new QaseStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final String stepDescription = "step description";

        listener.stepStarted(uuid, stepName, stepDescription, new Object[]{});
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        assertThat(step.getAction()).isEqualTo(stepName);
        assertThat(step.getComment()).isEqualTo(stepDescription);
        assertThat(step.getStatus()).isNull();
        StepStorage.stopStep();
    }

    @Test
    void stepStartedMethodWithContextReplacements() {
        final QaseStepListener listener = new QaseStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name, first context = {0}, second context = {1}";
        final String stepDescription = "step description, first context = {0}, second context = {1}";

        listener.stepStarted(uuid, stepName, stepDescription, new Object[]{"context value 1", "context value 2"});
        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        assertThat(step.getAction()).isEqualTo(
            "step name, first context = context value 1, second context = context value 2"
        );
        assertThat(step.getComment()).isEqualTo(
            "step description, first context = context value 1, second context = context value 2"
        );
        assertThat(step.getStatus()).isNull();
        StepStorage.stopStep();
    }

    @Test
    void stepPassedMethod() {
        final QaseStepListener listener = new QaseStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        StepStorage.startStep();
        StepStorage.getCurrentStep().action(stepName);

        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        listener.stepPassed(uuid);
        assertThat(StepStorage.getCurrentStep()).isNull();
        assertThat(step.getAction()).isEqualTo(stepName);
        assertThat(step.getStatus()).isSameAs(ResultCreateStepsInner.StatusEnum.PASSED);
    }

    @Test
    void stepFailedMethod() {
        final QaseStepListener listener = new QaseStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final AssertionError error = new AssertionError();
        StepStorage.startStep();
        StepStorage.getCurrentStep().action(stepName);

        final ResultCreateStepsInner step = StepStorage.getCurrentStep();
        listener.stepFailed(uuid, error);
        assertThat(StepStorage.getCurrentStep()).isNull();
        assertThat(step.getAction()).isEqualTo(stepName);
        assertThat(step.getStatus()).isSameAs(ResultCreateStepsInner.StatusEnum.FAILED);
    }
}
