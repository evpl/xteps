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
package com.plugatar.xteps.core.steps;

import com.plugatar.xteps.core.CtxSteps;
import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.StepNameFormatter;
import com.plugatar.xteps.core.StepWriter;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;

import java.util.Collections;
import java.util.Map;

/**
 * {@link NoCtxSteps} implementation.
 */
public class NoCtxStepsOf implements NoCtxSteps {
    private final StepNameFormatter sf;
    private final StepWriter sw;

    /**
     * Ctor.
     *
     * @param sf the step name formatter
     * @param sw the step writer
     * @throws ArgumentException if {@code sf} or {@code sw} is null
     */
    public NoCtxStepsOf(final StepNameFormatter sf,
                        final StepWriter sw) {
        if (sf == null) { throw new ArgumentException("sf arg is null"); }
        if (sw == null) { throw new ArgumentException("sw arg is null"); }
        this.sf = sf;
        this.sw = sw;
    }

    @Override
    public final <U> CtxSteps<U> toContext(final U context) {
        return new CtxStepsOf<>(
            this.sf, this.sw,
            context
        );
    }

    @Override
    public final <U, TH extends Throwable> CtxSteps<U> toContext(
        final ThrowingSupplier<? extends U, ? extends TH> contextSupplier
    ) throws TH {
        if (contextSupplier == null) { throw new ArgumentException("contextSupplier arg is null"); }
        return new CtxStepsOf<>(
            this.sf, this.sw,
            contextSupplier.get()
        );
    }

    @Override
    public final NoCtxSteps emptyStep(final String stepName) {
        this.sw.writeEmptyStep(
            this.sf.format(stepName, this)
        );
        return this;
    }

    @Override
    public final <TH extends Throwable> NoCtxSteps step(
        final String stepName,
        final ThrowingRunnable<? extends TH> step
    ) throws TH {
        if (step == null) { throw new ArgumentException("step arg is null"); }
        this.sw.writeRunnableStep(
            this.sf.format(stepName, this),
            step
        );
        return this;
    }

    @Override
    public final <U, TH extends Throwable> CtxSteps<U> stepToContext(
        final String stepName,
        final ThrowingSupplier<? extends U, ? extends TH> step
    ) throws TH {
        if (step == null) { throw new ArgumentException("step arg is null"); }
        return new CtxStepsOf<>(
            this.sf, this.sw,
            this.sw.writeSupplierStep(
                this.sf.format(stepName, this),
                step
            )
        );
    }

    @Override
    public final <R, TH extends Throwable> R stepTo(
        final String stepName,
        final ThrowingSupplier<? extends R, ? extends TH> step
    ) throws TH {
        if (step == null) { throw new ArgumentException("step arg is null"); }
        return this.sw.writeSupplierStep(
            this.sf.format(stepName, this),
            step
        );
    }

    @Override
    public final <TH extends Throwable> NoCtxSteps nestedSteps(
        final String stepName,
        final ThrowingConsumer<NoCtxSteps, ? extends TH> steps
    ) throws TH {
        if (steps == null) { throw new ArgumentException("steps arg is null"); }
        this.sw.writeConsumerStep(
            this.sf.format(stepName, this),
            this,
            steps
        );
        return this;
    }

    @Override
    public final <R, TH extends Throwable> R nestedStepsTo(
        final String stepName,
        final ThrowingFunction<NoCtxSteps, ? extends R, ? extends TH> steps
    ) throws TH {
        if (steps == null) { throw new ArgumentException("steps arg is null"); }
        return this.sw.writeFunctionStep(
            this.sf.format(stepName, this),
            this,
            steps
        );
    }

    @Override
    public final <TH extends Throwable> NoCtxSteps separatedSteps(
        final ThrowingConsumer<NoCtxSteps, ? extends TH> steps
    ) throws TH {
        if (steps == null) { throw new ArgumentException("steps arg is null"); }
        steps.accept(this);
        return this;
    }

    @Override
    public final Map<String, Object> stepNameReplacements() {
        return Collections.emptyMap();
    }
}
