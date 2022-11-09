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

import com.plugatar.xteps.base.StepListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.NamingUtils;
import io.qameta.allure.util.ResultsUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link StepListener} implementation reporting to Allure.
 */
public class AllureStepListener implements StepListener {
    private final String emptyStepNameReplacement;
    private final String stepDescriptionAttachmentName;
    private final String contextParamName;

    /**
     * Zero-argument public ctor.
     */
    public AllureStepListener() {
        this("step", "context", "Step description");
    }

    /**
     * Ctor.
     *
     * @param emptyStepNameReplacement      the empty step name replacement
     * @param contextParamName              the context param name
     * @param stepDescriptionAttachmentName the step description attachment name
     */
    public AllureStepListener(final String emptyStepNameReplacement,
                              final String contextParamName,
                              final String stepDescriptionAttachmentName) {
        if (emptyStepNameReplacement == null) {
            throw new NullPointerException("emptyStepNameReplacement arg is null");
        }
        if (emptyStepNameReplacement.isEmpty()) {
            throw new IllegalArgumentException("emptyStepNameReplacement arg is empty");
        }
        if (contextParamName == null) {
            throw new NullPointerException("contextParamName arg is null");
        }
        if (contextParamName.isEmpty()) {
            throw new IllegalArgumentException("contextParamName arg is empty");
        }
        if (stepDescriptionAttachmentName == null) {
            throw new NullPointerException("stepDescriptionAttachmentName arg is null");
        }
        if (stepDescriptionAttachmentName.isEmpty()) {
            throw new IllegalArgumentException("stepDescriptionAttachmentName arg is empty");
        }
        this.emptyStepNameReplacement = emptyStepNameReplacement;
        this.contextParamName = contextParamName;
        this.stepDescriptionAttachmentName = stepDescriptionAttachmentName;
    }

    @Override
    public final void stepStarted(final String stepUUID,
                                  final String stepName,
                                  final String stepDescription,
                                  final Object[] contexts) {
        Map<String, Object> replacements = null;
        /* Step name processing */
        final String processedStepName;
        if (stepName.isEmpty()) {
            processedStepName = this.emptyStepNameReplacement;
        } else {
            if (contexts.length == 0) {
                processedStepName = stepName;
            } else {
                replacements = this.contextsMap(contexts);
                processedStepName = this.processedTemplate(stepName, replacements);
            }
        }
        /* Step description processing */
        final String processedStepDescription;
        if (stepDescription.isEmpty()) {
            processedStepDescription = null;
        } else {
            if (contexts.length == 0) {
                processedStepDescription = stepDescription;
            } else {
                if (replacements == null) {
                    replacements = this.contextsMap(contexts);
                }
                processedStepDescription = this.processedTemplate(stepDescription, replacements);
            }
        }
        /* Reporting */
        Allure.getLifecycle().startStep(
            stepUUID,
            new StepResult().setName(processedStepName).setDescription(processedStepDescription)
        );
    }

    @Override
    public final void stepPassed(final String stepUUID) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        allureLifecycle.updateStep(stepUUID, stepResult -> {
            this.attachStepDescriptionIfPresent(stepResult);
            stepResult.setStatus(Status.PASSED);
        });
        allureLifecycle.stopStep(stepUUID);
    }

    @Override
    public final void stepFailed(final String stepUUID,
                                 final Throwable exception) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        allureLifecycle.updateStep(stepUUID, stepResult -> {
            this.attachStepDescriptionIfPresent(stepResult);
            stepResult.setStatus(ResultsUtils.getStatus(exception).orElse(Status.BROKEN))
                .setStatusDetails(ResultsUtils.getStatusDetails(exception).orElse(null));
        });
        allureLifecycle.stopStep(stepUUID);
    }

    private Map<String, Object> contextsMap(final Object[] contexts) {
        if (contexts.length != 0) {
            final Map<String, Object> map = new HashMap<>(contexts.length, 1.0f);
            map.put(this.contextParamName, contexts[0]);
            for (int idx = 1; idx < contexts.length; ++idx) {
                map.put(this.contextParamName + (idx + 1), contexts[idx]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    private String processedTemplate(final String template,
                                     final Map<String, Object> replacements) {
        return NamingUtils.processNameTemplate(template, replacements);
    }

    private void attachStepDescriptionIfPresent(final StepResult stepResult) {
        final String stepDescription = stepResult.getDescription();
        if (stepDescription != null && !stepDescription.isEmpty()) {
            Allure.attachment(this.stepDescriptionAttachmentName, stepDescription);
        }
    }
}
