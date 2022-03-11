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

import com.plugatar.xteps.core.BaseCtxSteps;
import com.plugatar.xteps.core.BaseMemorizingSteps;
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
 * {@link MemorizingCtxSteps} implementation.
 *
 * @param <T> the context type
 * @param <P> the previous steps type
 */
public class MemorizingCtxStepsOf<T, P extends BaseCtxSteps<?>> implements MemorizingCtxSteps<T, P> {
    private final StepNameFormatter sf;
    private final StepWriter sw;
    private final T context;
    private final P previous;

    /**
     * Ctor.
     *
     * @param sf       the step name formatter
     * @param sw       the step writer
     * @param context  the context, may be null
     * @param previous the previous steps
     * @throws ArgumentException if {@code sf} or {@code sw} or {@code previous} is null
     */
    public MemorizingCtxStepsOf(final StepNameFormatter sf,
                                final StepWriter sw,
                                final T context,
                                final P previous) {
        if (sf == null) { throw new ArgumentException("sf arg is null"); }
        if (sw == null) { throw new ArgumentException("sw arg is null"); }
        if (previous == null) { throw new ArgumentException("previous arg is null"); }
        this.sf = sf;
        this.sw = sw;
        this.context = context;
        this.previous = previous;
    }

    @Override
    public final P previous() {
        return this.previous;
    }

    @Override
    public final T context() {
        return this.context;
    }

    @Override
    public final <TH extends Throwable> MemorizingCtxSteps<T, P> supplyContextTo(
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
    public final MemorizingNoCtxSteps<MemorizingCtxSteps<T, P>> noContextSteps() {
        return new MemorizingNoCtxStepsOf<>(
            this.sf, this.sw,
            this
        );
    }

    @Override
    public final <U> MemorizingCtxSteps<U, MemorizingCtxSteps<T, P>> toContext(final U context) {
        return new MemorizingCtxStepsOf<>(
            this.sf, this.sw,
            context,
            this
        );
    }

    @Override
    public final <U, TH extends Throwable> MemorizingCtxSteps<U, MemorizingCtxSteps<T, P>> toContext(
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
    public final MemorizingCtxSteps<T, P> emptyStep(final String stepName) {
        this.sw.writeEmptyStep(
            this.sf.format(stepName, this)
        );
        return this;
    }

    @Override
    public final <TH extends Throwable> MemorizingCtxSteps<T, P> step(
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
    public final <U, TH extends Throwable> MemorizingCtxSteps<U, MemorizingCtxSteps<T, P>> stepToContext(
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
    public final <TH extends Throwable> MemorizingCtxSteps<T, P> nestedSteps(
        final String stepName,
        final ThrowingConsumer<MemorizingCtxSteps<T, P>, ? extends TH> steps
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
        final ThrowingFunction<MemorizingCtxSteps<T, P>, ? extends R, ? extends TH> steps
    ) throws TH {
        if (steps == null) { throw new ArgumentException("steps arg is null"); }
        return this.sw.writeFunctionStep(
            this.sf.format(stepName, this),
            this,
            steps
        );
    }

    @Override
    public final <TH extends Throwable> MemorizingCtxSteps<T, P> separatedSteps(
        final ThrowingConsumer<MemorizingCtxSteps<T, P>, ? extends TH> steps
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
        BaseCtxSteps<?> currentPrevious = this.previous;
        for (int level = 1; currentPrevious != null; ++level) {
            map.put(Integer.toString(level), currentPrevious.context());
            currentPrevious = currentPrevious instanceof BaseMemorizingSteps<?>
                ? ((BaseMemorizingSteps<?>) currentPrevious).previous()
                : null;
        }
        return map;
    }
}
