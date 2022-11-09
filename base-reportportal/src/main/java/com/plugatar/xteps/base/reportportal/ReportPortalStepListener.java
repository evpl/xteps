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
package com.plugatar.xteps.base.reportportal;

import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.step.StepRequestUtils;
import com.epam.reportportal.utils.templating.TemplateConfiguration;
import com.epam.reportportal.utils.templating.TemplateProcessing;
import com.plugatar.xteps.base.StepListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link StepListener} implementation reporting to Report Portal.
 */
public class ReportPortalStepListener implements StepListener {
    private final String emptyStepNameReplacement;
    private final TemplateConfiguration templateConfiguration;
    private final String contextParamName;

    /**
     * Zero-argument public ctor.
     */
    public ReportPortalStepListener() {
        this("step", "context", new TemplateConfiguration());
    }

    /**
     * Ctor.
     *
     * @param emptyStepNameReplacement the empty step name replacement
     * @param contextParamName         the context param name
     * @param templateConfiguration    the template configuration
     */
    public ReportPortalStepListener(final String emptyStepNameReplacement,
                                    final String contextParamName,
                                    final TemplateConfiguration templateConfiguration) {
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
        if (templateConfiguration == null) {
            throw new NullPointerException("templateConfiguration arg is null");
        }
        this.emptyStepNameReplacement = emptyStepNameReplacement;
        this.contextParamName = contextParamName;
        this.templateConfiguration = templateConfiguration;
    }

    @Override
    public final void stepStarted(final String stepUUID,
                                  final String stepName,
                                  final String stepDescription,
                                  final Object[] contexts) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
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
            launch.getStepReporter().startNestedStep(
                StepRequestUtils.buildStartStepRequest(processedStepName, processedStepDescription)
            );
        }
    }

    @Override
    public final void stepPassed(final String stepUUID) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().finishNestedStep();
        }
    }

    @Override
    public final void stepFailed(final String stepUUID,
                                 final Throwable exception) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().finishNestedStep(exception);
        }
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
        return TemplateProcessing.processTemplate(template, replacements, this.templateConfiguration);
    }
}
