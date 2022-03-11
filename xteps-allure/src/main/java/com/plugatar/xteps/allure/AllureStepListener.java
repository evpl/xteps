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
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ResultsUtils;

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
    public final void stepStarted(final String uuid,
                                  final String stepName) {
        Allure.getLifecycle().startStep(
            uuid,
            new StepResult().setName(stepName)
        );
    }

    @Override
    public final void stepPassed(final String uuid,
                                 final String stepName) {
        Allure.getLifecycle().updateStep(
            uuid,
            step -> step.setStatus(Status.PASSED)
        );
    }

    @Override
    public final void stepFailed(final String uuid,
                                 final String stepName,
                                 final Throwable throwable) {
        final Status status = ResultsUtils.getStatus(throwable).get();
        final StatusDetails statusDetails = ResultsUtils.getStatusDetails(throwable).get();
        Allure.getLifecycle().updateStep(
            uuid,
            step -> step.setStatus(status).setStatusDetails(statusDetails)
        );
    }
}
