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
package com.plugatar.xteps.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ResultsUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AllureStepListener}.
 */
final class AllureStepListenerTest {

    @Test
    void classIsNotFinal() {
        assertThat(AllureStepListener.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = AllureStepListener.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void stepStartedMethod() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        listener.stepStarted(uuid, stepName);
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get()).isEqualTo(
            new StepResult().setName(stepName)
                .setStage(Stage.RUNNING)
        );

        Allure.getLifecycle().stopStep(uuid);
    }

    @Test
    void stepPassedMethod() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(stepName));

        listener.stepPassed(uuid, stepName);
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get()).isEqualTo(
            new StepResult().setName(stepName)
                .setStage(Stage.RUNNING)
                .setStatus(Status.PASSED)
        );
    }

    @Test
    void stepFailedForAssertionErrorMethod() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final AssertionError error = new AssertionError();
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(stepName));

        listener.stepFailed(uuid, stepName, error);
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get()).isEqualTo(
            new StepResult().setName(stepName)
                .setStage(Stage.RUNNING)
                .setStatus(Status.FAILED)
                .setStatusDetails(ResultsUtils.getStatusDetails(error).get())
        );
    }

    @Test
    void stepFailedForNotAssertionErrorMethod() {
        final AllureStepListener listener = new AllureStepListener();
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final Throwable throwable = new Throwable();
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(stepName));

        listener.stepFailed(uuid, stepName, throwable);
        final AtomicReference<StepResult> stepResult = new AtomicReference<>();
        Allure.getLifecycle().updateStep(uuid, stepResult::set);
        assertThat(stepResult.get()).isEqualTo(
            new StepResult().setName(stepName)
                .setStage(Stage.RUNNING)
                .setStatus(Status.BROKEN)
                .setStatusDetails(ResultsUtils.getStatusDetails(throwable).get())
        );
    }
}
