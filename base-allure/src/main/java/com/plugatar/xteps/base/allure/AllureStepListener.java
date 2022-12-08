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

import java.util.Map;

import static com.plugatar.xteps.base.util.StepListenerUtils.paramArrayAsMap;

/**
 * {@link StepListener} implementation for Allure.
 */
public class AllureStepListener implements StepListener {
    private final String emptyNameReplacement;
    private final String descriptionAttachmentName;

    /**
     * Zero-argument public ctor.
     */
    public AllureStepListener() {
        this("Step", "Step description");
    }

    /**
     * Ctor.
     *
     * @param emptyNameReplacement      the empty step name replacement
     * @param descriptionAttachmentName the step description attachment name
     */
    public AllureStepListener(final String emptyNameReplacement,
                              final String descriptionAttachmentName) {
        final Class<Allure> dependencyCheck = Allure.class;
        if (emptyNameReplacement == null) {
            throw new NullPointerException("emptyNameReplacement arg is null");
        }
        if (emptyNameReplacement.isEmpty()) {
            throw new IllegalArgumentException("emptyNameReplacement arg is empty");
        }
        if (descriptionAttachmentName == null) {
            throw new NullPointerException("descriptionAttachmentName arg is null");
        }
        if (descriptionAttachmentName.isEmpty()) {
            throw new IllegalArgumentException("descriptionAttachmentName arg is empty");
        }
        this.emptyNameReplacement = emptyNameReplacement;
        this.descriptionAttachmentName = descriptionAttachmentName;
    }

    @Override
    public final void stepStarted(final String uuid,
                                  final String name,
                                  final String description,
                                  final Object[] params) {
        Map<String, Object> replacements = null;
        /* Step name processing */
        final String processedName;
        if (name.isEmpty()) {
            processedName = this.emptyNameReplacement;
        } else {
            if (params.length == 0) {
                processedName = name;
            } else {
                replacements = paramArrayAsMap(params);
                processedName = this.processedTemplate(name, replacements);
            }
        }
        /* Step description processing */
        final String processedDescription;
        if (description.isEmpty()) {
            processedDescription = null;
        } else {
            if (params.length == 0) {
                processedDescription = description;
            } else {
                if (replacements == null) {
                    replacements = paramArrayAsMap(params);
                }
                processedDescription = this.processedTemplate(description, replacements);
            }
        }
        /* Reporting */
        Allure.getLifecycle().startStep(
            uuid,
            new StepResult().setName(processedName).setDescription(processedDescription)
        );
    }

    @Override
    public final void stepPassed(final String uuid) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        allureLifecycle.updateStep(uuid, stepResult -> {
            this.attachStepDescriptionIfPresent(stepResult);
            stepResult.setStatus(Status.PASSED);
        });
        allureLifecycle.stopStep(uuid);
    }

    @Override
    public final void stepFailed(final String uuid,
                                 final Throwable exception) {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        allureLifecycle.updateStep(uuid, stepResult -> {
            this.attachStepDescriptionIfPresent(stepResult);
            stepResult.setStatus(ResultsUtils.getStatus(exception).orElse(Status.BROKEN))
                .setStatusDetails(ResultsUtils.getStatusDetails(exception).orElse(null));
        });
        allureLifecycle.stopStep(uuid);
    }

    private String processedTemplate(final String template,
                                     final Map<String, Object> replacements) {
        return replacements.isEmpty()
            ? template
            : NamingUtils.processNameTemplate(template, replacements);
    }

    private void attachStepDescriptionIfPresent(final StepResult stepResult) {
        final String stepDescription = stepResult.getDescription();
        if (stepDescription != null && !stepDescription.isEmpty()) {
            Allure.attachment(this.descriptionAttachmentName, stepDescription);
        }
    }
}
