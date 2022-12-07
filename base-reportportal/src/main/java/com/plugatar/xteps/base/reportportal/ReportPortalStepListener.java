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
 * {@link StepListener} implementation for ReportPortal.
 */
public class ReportPortalStepListener implements StepListener {
    private final String emptyNameReplacement;
    private final TemplateConfiguration templateConfiguration;

    /**
     * Zero-argument public ctor.
     */
    public ReportPortalStepListener() {
        this("Step", new TemplateConfiguration());
    }

    /**
     * Ctor.
     *
     * @param emptyNameReplacement  the empty step name replacement
     * @param templateConfiguration the template configuration
     */
    public ReportPortalStepListener(final String emptyNameReplacement,
                                    final TemplateConfiguration templateConfiguration) {
        final Class<Launch> dependencyCheck = Launch.class;
        if (emptyNameReplacement == null) {
            throw new NullPointerException("emptyNameReplacement arg is null");
        }
        if (emptyNameReplacement.isEmpty()) {
            throw new IllegalArgumentException("emptyNameReplacement arg is empty");
        }
        if (templateConfiguration == null) {
            throw new NullPointerException("templateConfiguration arg is null");
        }
        this.emptyNameReplacement = emptyNameReplacement;
        this.templateConfiguration = templateConfiguration;
    }

    @Override
    public final void stepStarted(final String uuid,
                                  final String name,
                                  final String description,
                                  final Object[] params) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            Map<String, Object> replacements = null;
            /* Step name processing */
            final String processedName;
            if (name.isEmpty()) {
                processedName = this.emptyNameReplacement;
            } else {
                if (params.length == 0) {
                    processedName = name;
                } else {
                    replacements = this.paramsMap(params);
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
                        replacements = this.paramsMap(params);
                    }
                    processedDescription = this.processedTemplate(description, replacements);
                }
            }
            /* Reporting */
            launch.getStepReporter().startNestedStep(
                StepRequestUtils.buildStartStepRequest(processedName, processedDescription)
            );
        }
    }

    @Override
    public final void stepPassed(final String uuid) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().finishNestedStep();
        }
    }

    @Override
    public final void stepFailed(final String uuid,
                                 final Throwable exception) {
        final Launch launch = Launch.currentLaunch();
        if (launch != null) {
            launch.getStepReporter().finishNestedStep(exception);
        }
    }

    private Map<String, Object> paramsMap(final Object[] params) {
        if (params.length != 0) {
            final Map<String, Object> map = new HashMap<>(params.length, 1.0f);
            for (int idx = 0; idx < params.length; ++idx) {
                map.put(String.valueOf(idx), params[idx]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    private String processedTemplate(final String template,
                                     final Map<String, Object> replacements) {
        return replacements.isEmpty()
            ? template
            : TemplateProcessing.processTemplate(template, replacements, this.templateConfiguration);
    }
}
