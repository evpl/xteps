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

import com.plugatar.xteps.core.StepListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ResultsUtils;

import java.nio.charset.StandardCharsets;

/**
 * {@link StepListener} implementation reporting to Allure.
 */
public class AllureStepListener implements StepListener {

    /**
     * Zero-argument public ctor.
     *
     * @throws NoClassDefFoundError if no definition of the {@link Allure}
     *                              class could be found
     */
    public AllureStepListener() {
        final Class<?> classDefCheck = Allure.class;
    }

    @Override
    public void stepStarted(final String uuid,
                            final String stepName,
                            final String stepDescription) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final StepResult stepResult = new StepResult();
        allureLifecycle.startStep(
            uuid,
            stepResult.setName(stepName.isEmpty() ? "Step" : stepName)
        );
        if (!stepDescription.isEmpty()) {
            /* Step description can only be added via text attachments at the moment
                https://github.com/allure-framework/allure2/issues/1699 */
            allureLifecycle.addAttachment(
                "Step description", "text/plain", ".txt", stepDescription.getBytes(StandardCharsets.UTF_8)
            );
            /* In case Allure starts adding step descriptions */
            stepResult.setDescription(stepDescription);
        }
    }

    @Override
    public final void stepPassed(final String uuid) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        allureLifecycle.updateStep(
            uuid,
            step -> step.setStatus(Status.PASSED)
        );
        allureLifecycle.stopStep(uuid);
    }

    @Override
    public final void stepFailed(final String uuid,
                                 final Throwable exception) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        allureLifecycle.updateStep(
            uuid,
            step -> step.setStatus(ResultsUtils.getStatus(exception).orElse(Status.BROKEN))
                .setStatusDetails(ResultsUtils.getStatusDetails(exception).orElse(null))
        );
        allureLifecycle.stopStep(uuid);
    }
}
