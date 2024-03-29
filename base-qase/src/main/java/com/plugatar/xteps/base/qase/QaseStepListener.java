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

import java.util.Map;

import static com.plugatar.xteps.base.util.StepListenerUtils.paramArrayAsMap;
import static com.plugatar.xteps.base.util.StepListenerUtils.processedTemplate;

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
                replacements = paramArrayAsMap(this.leftReplacementBorder, this.rightReplacementBorder, params);
                processedName = processedTemplate(name, replacements);
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
                    replacements = paramArrayAsMap(this.leftReplacementBorder, this.rightReplacementBorder, params);
                }
                processedDescription = processedTemplate(description, replacements);
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
}
