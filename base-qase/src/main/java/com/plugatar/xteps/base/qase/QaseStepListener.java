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

/**
 * {@link StepListener} implementation for Qase.
 */
public class QaseStepListener implements StepListener {
    private final String emptyNameReplacement;
    private final char leftReplacementBorder;
    private final char rightReplacementBorder;

    /**
     * Zero-argument public ctor.
     */
    public QaseStepListener() {
        this("Step", '{', '}');
    }

    /**
     * @param emptyNameReplacement   the empty step name replacement
     * @param leftReplacementBorder  the left replacement border
     * @param rightReplacementBorder the right replacement border
     */
    public QaseStepListener(final String emptyNameReplacement,
                            final char leftReplacementBorder,
                            final char rightReplacementBorder) {
        final Class<StepStorage> dependencyCheck = StepStorage.class;
        if (emptyNameReplacement == null) {
            throw new NullPointerException("emptyNameReplacement arg is null");
        }
        if (emptyNameReplacement.isEmpty()) {
            throw new IllegalArgumentException("emptyNameReplacement arg is empty");
        }
        this.emptyNameReplacement = emptyNameReplacement;
        this.leftReplacementBorder = leftReplacementBorder;
        this.rightReplacementBorder = rightReplacementBorder;
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
        StepStorage.startStep();
        StepStorage.getCurrentStep()
            .action(processedName)
            .comment(processedDescription);
    }

    @Override
    public final void stepPassed(final String uuid) {
        StepStorage.getCurrentStep().status(ResultCreateStepsInner.StatusEnum.PASSED);
        StepStorage.stopStep();
    }

    @Override
    public final void stepFailed(final String uuid,
                                 final Throwable exception) {
        StepStorage.getCurrentStep()
            .status(ResultCreateStepsInner.StatusEnum.FAILED)
            .addAttachmentsItem(IntegrationUtils.getStacktrace(exception));
        StepStorage.stopStep();
    }

    private Map<String, Object> paramsMap(final Object[] params) {
        if (params.length != 0) {
            final Map<String, Object> map = new HashMap<>(params.length, 1.0f);
            for (int idx = 0; idx < params.length; ++idx) {
                map.put(this.leftReplacementBorder + String.valueOf(idx) + this.rightReplacementBorder, params[idx]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    private String processedTemplate(final String template,
                                     final Map<String, Object> replacements) {
        if (replacements.isEmpty()) {
            return template;
        }
        String processedTemplate = template;
        for (final Map.Entry<String, Object> entry : replacements.entrySet()) {
            final String key = entry.getKey();
            if (processedTemplate.contains(key)) {
                processedTemplate = processedTemplate.replace(key, objToString(entry.getValue()));
            }
        }
        return processedTemplate;
    }

    private static String objToString(final Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof int[]) {
                return Arrays.toString((int[]) obj);
            } else if (obj instanceof long[]) {
                return Arrays.toString((long[]) obj);
            } else if (obj instanceof double[]) {
                return Arrays.toString((double[]) obj);
            } else if (obj instanceof float[]) {
                return Arrays.toString((float[]) obj);
            } else if (obj instanceof boolean[]) {
                return Arrays.toString((boolean[]) obj);
            } else if (obj instanceof short[]) {
                return Arrays.toString((short[]) obj);
            } else if (obj instanceof char[]) {
                return Arrays.toString((char[]) obj);
            } else if (obj instanceof byte[]) {
                return Arrays.toString((byte[]) obj);
            } else {
                return Arrays.toString((Object[]) obj);
            }
        }
        return String.valueOf(obj);
    }
}
