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
package com.plugatar.xteps.core.base;

import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.StepNameFormatter;
import com.plugatar.xteps.core.StepWriter;
import com.plugatar.xteps.core.XtepsBase;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.steps.NoCtxStepsOf;

import java.util.Collections;
import java.util.Map;

/**
 * {@link XtepsBase} implementation.
 */
public class XtepsBaseOf implements XtepsBase {
    private final Map<String, String> config;
    private final StepNameFormatter sf;
    private final StepWriter sw;
    private final NoCtxSteps noCtxSteps;

    /**
     * Ctor.
     *
     * @param config the config
     * @param sf     the step name formatter
     * @param sw     the step writer
     */
    public XtepsBaseOf(final Map<String, String> config,
                       final StepNameFormatter sf,
                       final StepWriter sw) {
        if (config == null) { throw new ArgumentException("config arg is null"); }
        if (sf == null) { throw new ArgumentException("sf arg is null"); }
        if (sw == null) { throw new ArgumentException("sw arg is null"); }
        this.config = Collections.unmodifiableMap(config);
        this.sf = sf;
        this.sw = sw;
        this.noCtxSteps = new NoCtxStepsOf(sf, sw);
    }

    @Override
    public final Map<String, String> config() {
        return this.config;
    }

    @Override
    public final StepNameFormatter stepNameFormatter() {
        return this.sf;
    }

    @Override
    public final StepWriter stepWriter() {
        return this.sw;
    }

    @Override
    public final NoCtxSteps steps() {
        return this.noCtxSteps;
    }
}
