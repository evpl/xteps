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
package com.plugatar.xteps.reportportal;

import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.step.StepRequestUtils;
import com.plugatar.xteps.core.StepListener;

/**
 * {@link StepListener} implementation reporting to Report Portal.
 */
public class ReportPortalStepListener implements StepListener {

    /**
     * Zero-argument public ctor.
     *
     * @throws NoClassDefFoundError if no definition of the {@link Launch}
     *                              class could be found
     */
    public ReportPortalStepListener() {
        final Class<?> classDefCheck = Launch.class;
    }

    @Override
    public final void stepStarted(final String uuid,
                                  final String stepName) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().startNestedStep(
                StepRequestUtils.buildStartStepRequest(stepName, null)
            );
        }
    }

    @Override
    public final void stepPassed(final String uuid,
                                 final String stepName) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().finishNestedStep();
        }
    }

    @Override
    public final void stepFailed(final String uuid,
                                 final String stepName,
                                 final Throwable throwable) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().finishNestedStep(throwable);
        }
    }
}
