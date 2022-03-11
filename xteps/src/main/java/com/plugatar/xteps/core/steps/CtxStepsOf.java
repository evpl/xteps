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
import com.plugatar.xteps.core.MemorizingCtxSteps;
import com.plugatar.xteps.core.MemorizingNoCtxSteps;
import com.plugatar.xteps.core.StepNameFormatter;
import com.plugatar.xteps.core.StepWriter;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.util.function.ThrowingConsumer;
import com.plugatar.xteps.core.util.function.ThrowingFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link CtxSteps} implementation.
 *
 * @param <T> the context type
 */
public class CtxStepsOf<T> implements CtxSteps<T> {
    private final StepNameFormatter sf;
    private final StepWriter sw;
    private final T context;

    /**
     * Ctor.
     *
     * @param sf      the step name formatter
     * @param sw      the step writer
     * @param context the context, may be null
     * @throws ArgumentException if {@code sf} or {@code sw} is null
     */
    public CtxStepsOf(final StepNameFormatter sf,
                      final StepWriter sw,
                      final T context) {
        if (sf == null) { throw new ArgumentException("sf arg is null"); }
        if (sw == null) { throw new ArgumentException("sw arg is null"); }
        this.sf = sf;
        this.sw = sw;
        this.context = context;
    }

    @Override
    public final T context() {
        return this.context;
    }

    @Override
    public final <TH extends Throwable> CtxSteps<T> supplyContextTo(
        final ThrowingConsumer<? super T, ? extends TH> consumer
    ) throws TH {
        if (consumer == null) { throw new ArgumentException("consumer arg is null"); }
        consumer.accept(this.context);
        return this;
    }

    @Override
    public final <R, TH extends Throwable> R applyContextTo(
        final ThrowingFunction<? super T, ? extends R, ? extends TH> function
    ) throws TH {
        if (function == null) { throw new ArgumentException("function arg is null"); }
        return function.apply(this.context);
    }

    @Override
    public final MemorizingNoCtxSteps<CtxSteps<T>> noContextSteps() {
        return new MemorizingNoCtxStepsOf<>(
            this.sf, this.sw,
            this
        );
    }

    @Override
    public final <U> MemorizingCtxSteps<U, CtxSteps<T>> toContext(final U context) {
        return new MemorizingCtxStepsOf<>(
            this.sf, this.sw,
            context,
            this
        );
    }

    @Override
    public final <U, TH extends Throwable> MemorizingCtxSteps<U, CtxSteps<T>> toContext(
        final ThrowingFunction<? super T, ? extends U, ? extends TH> contextFunction
    ) throws TH {
        if (contextFunction == null) { throw new ArgumentException("contextFunction arg is null"); }
        return new MemorizingCtxStepsOf<>(
            this.sf, this.sw,
            contextFunction.apply(this.context),
            this
        );
    }

    @Override
    public final CtxSteps<T> emptyStep(final String stepName) {
        this.sw.writeEmptyStep(
            this.sf.format(stepName, this)
        );
        return this;
    }

    @Override
    public final <TH extends Throwable> CtxSteps<T> step(
        final String stepName,
        final ThrowingConsumer<? super T, ? extends TH> step
    ) throws TH {
        if (step == null) { throw new ArgumentException("step arg is null"); }
        this.sw.writeConsumerStep(
            this.sf.format(stepName, this),
            this.context,
            step
        );
        return this;
    }

    @Override
    public final <U, TH extends Throwable> MemorizingCtxSteps<U, CtxSteps<T>> stepToContext(
        final String stepName,
        final ThrowingFunction<? super T, ? extends U, ? extends TH> step
    ) throws TH {
        if (step == null) { throw new ArgumentException("step arg is null"); }
        return new MemorizingCtxStepsOf<>(
            this.sf, this.sw,
            this.sw.writeFunctionStep(
                this.sf.format(stepName, this),
                this.context,
                step
            ),
            this
        );
    }

    @Override
    public final <R, TH extends Throwable> R stepTo(
        final String stepName,
        final ThrowingFunction<? super T, ? extends R, ? extends TH> step
    ) throws TH {
        if (step == null) { throw new ArgumentException("step arg is null"); }
        return this.sw.writeFunctionStep(
            this.sf.format(stepName, this),
            this.context,
            step
        );
    }

    @Override
    public final <TH extends Throwable> CtxSteps<T> nestedSteps(
        final String stepName,
        final ThrowingConsumer<CtxSteps<T>, ? extends TH> steps
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
        final ThrowingFunction<CtxSteps<T>, ? extends R, ? extends TH> steps
    ) throws TH {
        if (steps == null) { throw new ArgumentException("steps arg is null"); }
        return this.sw.writeFunctionStep(
            this.sf.format(stepName, this),
            this,
            steps
        );
    }

    @Override
    public final <TH extends Throwable> CtxSteps<T> separatedSteps(
        final ThrowingConsumer<CtxSteps<T>, ? extends TH> steps
    ) throws TH {
        if (steps == null) { throw new ArgumentException("steps arg is null"); }
        steps.accept(this);
        return this;
    }

    @Override
    public final Map<String, Object> stepNameReplacements() {
        final Map<String, Object> map = new HashMap<>();
        map.put("context", this.context);
        map.put("0", this.context);
        return map;
    }
}
