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
package com.plugatar.xteps.base.allure;

import com.plugatar.xteps.base.OptionalValue;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.StepResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AllureStepListener}.
 */
final class AllureStepListenerTest {

    @Test
    void stepStartedMethodEmptyName() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();

        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepDescription = "step description";

        listener.stepStarted(uuid, "", stepDescription, OptionalValue.of("context value"));
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        allureLifecycle.updateStep(uuid, stepResult::set);
        assertThat(stepResult.get().getName()).isEqualTo("step");
        assertThat(stepResult.get().getStage()).isEqualTo(Stage.RUNNING);

        allureLifecycle.stopStep(uuid);
    }

    @Test
    void stepStartedMethodEmptyDescription() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        listener.stepStarted(uuid, stepName, "", OptionalValue.of("context value"));
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get().getName()).isEqualTo(stepName);
        assertThat(stepResult.get().getStage()).isEqualTo(Stage.RUNNING);

        Allure.getLifecycle().stopStep(uuid);
    }

    @Test
    void stepStartedMethodWithoutReplacements() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final String stepDescription = "step description";

        listener.stepStarted(uuid, stepName, stepDescription, OptionalValue.of("context value"));
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get().getName()).isEqualTo(stepName);
        assertThat(stepResult.get().getDescription()).isEqualTo(stepDescription);
        assertThat(stepResult.get().getStage()).isEqualTo(Stage.RUNNING);

        Allure.getLifecycle().stopStep(uuid);
    }

    @Test
    void stepStartedMethodWithReplacements() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step {context} name";
        final String stepDescription = "step {context} description";

        listener.stepStarted(uuid, stepName, stepDescription, OptionalValue.of("context value"));
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get().getName()).isEqualTo("step context value name");
        assertThat(stepResult.get().getDescription()).isEqualTo("step context value description");
        assertThat(stepResult.get().getStage()).isEqualTo(Stage.RUNNING);

        Allure.getLifecycle().stopStep(uuid);
    }

    @Test
    void stepPassedMethod() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(stepName));

        listener.stepPassed(uuid);
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get()).isNull();
    }

    @Test
    void stepFailedMethod() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final AssertionError error = new AssertionError();
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(stepName));

        listener.stepFailed(uuid, error);
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get()).isNull();
    }
}
