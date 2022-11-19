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

import com.plugatar.xteps.base.StepListener;
import io.qase.api.StepStorage;
import io.qase.api.utils.IntegrationUtils;
import io.qase.client.model.ResultCreateStepsInner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link StepListener} implementation reporting to Qase.
 */
public class QaseStepListener implements StepListener {
    private final String emptyStepNameReplacement;
    private final String contextParamName;
    private final char leftReplacementBorder;
    private final char rightReplacementBorder;

    /**
     * Zero-argument public ctor.
     */
    public QaseStepListener() {
        this("step", "context");
    }

    /**
     * Ctor.
     *
     * @param emptyStepNameReplacement the empty step name replacement
     * @param contextParamName         the context param name
     */
    public QaseStepListener(final String emptyStepNameReplacement,
                            final String contextParamName) {
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
        this.emptyStepNameReplacement = emptyStepNameReplacement;
        this.contextParamName = contextParamName;
        this.leftReplacementBorder = '{';
        this.rightReplacementBorder = '}';
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
        StepStorage.startStep();
        StepStorage.getCurrentStep()
            .action(processedStepName)
            .comment(processedStepDescription);
    }

    @Override
    public final void stepPassed(final String stepUUID) {
        StepStorage.getCurrentStep().status(ResultCreateStepsInner.StatusEnum.PASSED);
        StepStorage.stopStep();
    }

    @Override
    public final void stepFailed(final String stepUUID,
                                 final Throwable exception) {
        StepStorage.getCurrentStep()
            .status(ResultCreateStepsInner.StatusEnum.FAILED)
            .addAttachmentsItem(IntegrationUtils.getStacktrace(exception));
        StepStorage.stopStep();
    }

    private Map<String, Object> contextsMap(final Object[] contexts) {
        if (contexts.length != 0) {
            final Map<String, Object> map = new HashMap<>(contexts.length * 2, 1.0f);
            map.put(this.contextParamName, contexts[0]);
            map.put("0", contexts[0]);
            for (int idx = 1; idx < contexts.length; ++idx) {
                map.put(String.valueOf(idx), contexts[idx]);
                map.put(this.contextParamName + (idx + 1), contexts[idx]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    private String processedTemplate(final String template,
                                     final Map<String, Object> replacements) {
        String processedTemplate = template;
        for (final Map.Entry<String, Object> entry : replacements.entrySet()) {
            final String wrappedParamName = this.wrapParamName(entry.getKey());
            if (template.contains(wrappedParamName)) {
                processedTemplate = processedTemplate.replaceAll(
                    "\\" + wrappedParamName, objToString(entry.getValue())
                );
            }
        }
        return processedTemplate;
    }

    private String wrapParamName(final String paramName) {
        return this.leftReplacementBorder + paramName + this.rightReplacementBorder;
    }

    private static String objToString(Object args) {
        if (args.getClass().isArray()) {
            if (args instanceof int[]) {
                return Arrays.toString((int[]) args);
            } else if (args instanceof long[]) {
                return Arrays.toString((long[]) args);
            } else if (args instanceof double[]) {
                return Arrays.toString((double[]) args);
            } else if (args instanceof float[]) {
                return Arrays.toString((float[]) args);
            } else if (args instanceof boolean[]) {
                return Arrays.toString((boolean[]) args);
            } else if (args instanceof short[]) {
                return Arrays.toString((short[]) args);
            } else if (args instanceof char[]) {
                return Arrays.toString((char[]) args);
            } else if (args instanceof byte[]) {
                return Arrays.toString((byte[]) args);
            } else {
                return Arrays.stream(((Object[]) args)).map(String::valueOf).collect(Collectors.joining(", "));
            }
        }
        return String.valueOf(args);
    }
}
