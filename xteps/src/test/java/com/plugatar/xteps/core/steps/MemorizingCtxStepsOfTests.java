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
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link MemorizingCtxStepsOf}.
 */
final class MemorizingCtxStepsOfTests {

    @Test
    void classIsNotFinal() {
        assertThat(MemorizingCtxStepsOf.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = MemorizingCtxStepsOf.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullSfArg() {
        final StepNameFormatter sf = null;
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final Object context = new Object();
        assertThatCode(() -> new MemorizingCtxStepsOf<>(sf, sw, context, previous))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullSwArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = null;
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final Object context = new Object();
        assertThatCode(() -> new MemorizingCtxStepsOf<>(sf, sw, context, previous))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullPreviousArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = null;
        final Object context = new Object();
        assertThatCode(() -> new MemorizingCtxStepsOf<>(sf, sw, context, previous))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorDoesntThrowExceptionForNullContextArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final Object context = null;
        assertThatCode(() -> new MemorizingCtxStepsOf<>(sf, sw, context, previous))
            .doesNotThrowAnyException();
    }

    @Test
    void previousMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, new Object(), previous);

        assertThat(memCtxSteps.previous()).isSameAs(previous);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void contextMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());

        assertThat(memCtxSteps.context()).isSameAs(context);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void supplyContextToMethodThrowsExceptionForNullConsumer() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.supplyContextTo(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void supplyContextToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, RuntimeException> consumer = mock(ThrowingConsumer.class);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memCtxSteps.supplyContextTo(consumer);
        assertThat(methodResult).isSameAs(memCtxSteps);
        verify(consumer, times(1)).accept(refEq(context));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void applyContextToMethodThrowsExceptionForNullFunction() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.applyContextTo(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void applyContextToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        final Object newContext = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(newContext);

        final Object methodResult = memCtxSteps.applyContextTo(function);
        assertThat(methodResult).isSameAs(newContext);
        verify(function, times(1)).apply(refEq(context));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void noContextStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, new Object(), new Ctx<>());

        final MemorizingNoCtxSteps<MemorizingCtxSteps<Object, BaseCtxSteps<Object>>> methodResult =
            memCtxSteps.noContextSteps();
        assertThat(methodResult.previous()).isSameAs(memCtxSteps);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextValueMethodDoesntThrowExceptionForNullContext() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );
        final Object context = null;

        assertThatCode(() -> memCtxSteps.toContext(context))
            .doesNotThrowAnyException();
    }

    @Test
    void toContextValueMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, new Object(), new Ctx<>());
        final Object newContext = new Object();

        final MemorizingCtxSteps<Object, MemorizingCtxSteps<Object, BaseCtxSteps<Object>>> methodResult =
            memCtxSteps.toContext(newContext);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previous()).isSameAs(memCtxSteps);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextSupplierMethodThrowsExceptionForNullFunction() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );
        final ThrowingFunction<Object, Object, RuntimeException> function = null;

        assertThatCode(() -> memCtxSteps.toContext(function))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void toContextSupplierMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, new Object(), new Ctx<>());
        final Object newContext = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(newContext);

        final MemorizingCtxSteps<Object, MemorizingCtxSteps<Object, BaseCtxSteps<Object>>> methodResult =
            memCtxSteps.toContext(function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previous()).isSameAs(memCtxSteps);
        verify(function).apply(refEq(context));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void emptyStepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, new Object(), new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        when(sf.format(any(), any())).thenReturn(sfResult);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memCtxSteps.emptyStep(stepName);
        assertThat(methodResult).isSameAs(memCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memCtxSteps));
        verify(sw).writeEmptyStep(refEq(sfResult));
    }

    @Test
    void stepMethodThrowsExceptionForNullStep() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.step("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void stepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingConsumer<Object, RuntimeException> step = c -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memCtxSteps.step(stepName, step);
        assertThat(methodResult).isSameAs(memCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memCtxSteps));
        verify(sw).writeConsumerStep(refEq(sfResult), refEq(context), refEq(step));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStep() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.stepToContext("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToContextMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> step = c -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(any(), any(), (ThrowingFunction<Object, Object, RuntimeException>) any())).thenReturn(swResult);

        final MemorizingCtxSteps<Object, MemorizingCtxSteps<Object, BaseCtxSteps<Object>>> methodResult =
            memCtxSteps.stepToContext(stepName, step);
        assertThat(methodResult.context()).isSameAs(swResult);
        assertThat(methodResult.previous()).isSameAs(memCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memCtxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(context), refEq(step));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStep() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.stepTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> step = c -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(any(), any(), (ThrowingFunction<Object, Object, RuntimeException>) any())).thenReturn(swResult);

        final Object methodResult = memCtxSteps.stepTo(stepName, step);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(memCtxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(context), refEq(step));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullSteps() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.nestedSteps("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void nestedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingConsumer<MemorizingCtxSteps<Object, BaseCtxSteps<Object>>, RuntimeException> steps =
            c -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult =
            memCtxSteps.nestedSteps(stepName, steps);
        assertThat(methodResult).isSameAs(memCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memCtxSteps));
        verify(sw).writeConsumerStep(refEq(sfResult), refEq(memCtxSteps), refEq(steps));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullSteps() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.nestedStepsTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void nestedStepsToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<MemorizingCtxSteps<Object, BaseCtxSteps<Object>>, Object, RuntimeException> steps =
            s -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(
            any(),
            any(),
            (ThrowingFunction<MemorizingCtxSteps<Object, BaseCtxSteps<Object>>, Object, RuntimeException>) any()
        )).thenReturn(swResult);

        final Object methodResult = memCtxSteps.nestedStepsTo(stepName, steps);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(memCtxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(memCtxSteps), refEq(steps));
    }

    @Test
    void separatedStepsMethodThrowsExceptionForNullSteps() {
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object(),
            new Ctx<>()
        );

        assertThatCode(() -> memCtxSteps.separatedSteps(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void separatedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, new Object(), new Ctx<>());
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<MemorizingCtxSteps<Object, BaseCtxSteps<Object>>, RuntimeException> steps =
            mock(ThrowingConsumer.class);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memCtxSteps.separatedSteps(steps);
        assertThat(methodResult).isSameAs(memCtxSteps);
        verify(steps, times(1)).accept(refEq(memCtxSteps));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void replacements1PreviousMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final Object previousContext = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps =
            new MemorizingCtxStepsOf<>(sf, sw, context, new Ctx<>(previousContext));

        final Map<String, Object> methodResult = memCtxSteps.stepNameReplacements();
        assertThat(methodResult).containsOnly(
            entry("context", context),
            entry("0", context),
            entry("1", previousContext)
        );
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void replacements3PreviousMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final Object previousContext1 = new Object();
        final Object previousContext2 = new Object();
        final Object previousContext3 = new Object();
        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> memCtxSteps = new MemorizingCtxStepsOf<>(
            sf,
            sw,
            context,
            new CtxMem<>(previousContext1, new CtxMem<>(previousContext2, new Ctx<>(previousContext3)))
        );

        final Map<String, Object> methodResult = memCtxSteps.stepNameReplacements();
        assertThat(methodResult).containsOnly(
            entry("context", context),
            entry("0", context),
            entry("1", previousContext1),
            entry("2", previousContext2),
            entry("3", previousContext3)
        );
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    private static final class Ctx<T> implements BaseCtxSteps<T> {
        private final T context;

        Ctx() {
            this(null);
        }

        Ctx(final T context) {
            this.context = context;
        }

        @Override
        public T context() {
            return this.context;
        }
    }

    private static final class CtxMem<T, P> implements BaseCtxSteps<T>, BaseMemorizingSteps<BaseCtxSteps<P>> {
        private final T context;
        private final BaseCtxSteps<P> previous;

        CtxMem(final T context, final BaseCtxSteps<P> previous) {
            this.context = context;
            this.previous = previous;
        }

        @Override
        public T context() {
            return this.context;
        }

        @Override
        public BaseCtxSteps<P> previous() {
            return this.previous;
        }
    }
}
