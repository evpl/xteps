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
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import com.plugatar.xteps.core.util.function.ThrowingSupplier;
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
 * Tests for {@link MemorizingNoCtxStepsOf}.
 */
final class MemorizingNoCtxStepsOfTest {

    @Test
    void classIsNotFinal() {
        assertThat(MemorizingNoCtxStepsOf.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = MemorizingNoCtxStepsOf.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullSfArg() {
        final StepNameFormatter sf = null;
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        assertThatCode(() -> new MemorizingNoCtxStepsOf<>(sf, sw, previous))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullSwArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = null;
        final BaseCtxSteps<Object> previous = new Ctx<>();
        assertThatCode(() -> new MemorizingNoCtxStepsOf<>(sf, sw, previous))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullPreviousArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = null;
        assertThatCode(() -> new MemorizingNoCtxStepsOf<>(sf, sw, previous))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void previousMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, previous);

        final BaseCtxSteps<Object> methodResult = memNoCtxSteps.previous();
        assertThat(methodResult).isSameAs(previous);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextValueMethodDoesntThrowExceptionForNullContext() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );
        final Object context = null;

        assertThatCode(() -> memNoCtxSteps.toContext(context))
            .doesNotThrowAnyException();
    }

    @Test
    void toContextValueMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, previous);
        final Object context = new Object();

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memNoCtxSteps.toContext(context);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previous()).isSameAs(previous);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextSupplierMethodThrowsExceptionForNullSupplier() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );
        final ThrowingSupplier<Object, RuntimeException> supplier = null;

        assertThatCode(() -> memNoCtxSteps.toContext(supplier))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void toContextSupplierMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final BaseCtxSteps<Object> previous = new Ctx<>();
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, previous);
        final Object context = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> supplier = mock(ThrowingSupplier.class);
        when(supplier.get()).thenReturn(context);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memNoCtxSteps.toContext(supplier);
        assertThat(methodResult.context()).isSameAs(context);
        assertThat(methodResult.previous()).isSameAs(previous);
        verify(supplier, times(1)).get();
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void emptyStepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        when(sf.format(any(), any())).thenReturn(sfResult);

        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> methodResult = memNoCtxSteps.emptyStep(stepName);
        assertThat(methodResult).isSameAs(memNoCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memNoCtxSteps));
        verify(sw).writeEmptyStep(refEq(sfResult));
    }

    @Test
    void stepMethodThrowsExceptionForNullStep() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );

        assertThatCode(() -> memNoCtxSteps.step("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void stepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingRunnable<RuntimeException> step = () -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> methodResult = memNoCtxSteps.step(stepName, step);
        assertThat(methodResult).isSameAs(memNoCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memNoCtxSteps));
        verify(sw).writeRunnableStep(refEq(sfResult), refEq(step));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStep() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );

        assertThatCode(() -> memNoCtxSteps.stepToContext("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToContextMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingSupplier<Object, RuntimeException> step = Object::new;
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeSupplierStep(any(), (ThrowingSupplier<Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final MemorizingCtxSteps<Object, BaseCtxSteps<Object>> methodResult = memNoCtxSteps.stepToContext(stepName, step);
        assertThat(methodResult.context()).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(memNoCtxSteps));
        verify(sw).writeSupplierStep(refEq(sfResult), refEq(step));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStep() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );

        assertThatCode(() -> memNoCtxSteps.stepTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingSupplier<Object, RuntimeException> step = Object::new;
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeSupplierStep(any(), (ThrowingSupplier<Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final Object methodResult = memNoCtxSteps.stepTo(stepName, step);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(memNoCtxSteps));
        verify(sw).writeSupplierStep(refEq(sfResult), refEq(step));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullSteps() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );

        assertThatCode(() -> memNoCtxSteps.nestedSteps("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void nestedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingConsumer<MemorizingNoCtxSteps<BaseCtxSteps<Object>>, RuntimeException> steps = s -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> methodResult = memNoCtxSteps.nestedSteps(stepName, steps);
        assertThat(methodResult).isSameAs(memNoCtxSteps);
        verify(sf).format(refEq(stepName), refEq(memNoCtxSteps));
        verify(sw).writeConsumerStep(refEq(sfResult), refEq(memNoCtxSteps), refEq(steps));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullSteps() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );

        assertThatCode(() -> memNoCtxSteps.nestedStepsTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void nestedStepsToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<MemorizingNoCtxSteps<BaseCtxSteps<Object>>, Object, RuntimeException> steps =
            s -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(
            any(),
            any(),
            (ThrowingFunction<MemorizingNoCtxSteps<BaseCtxSteps<Object>>, Object, RuntimeException>) any()
        )).thenReturn(swResult);

        final Object methodResult = memNoCtxSteps.nestedStepsTo(stepName, steps);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(memNoCtxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(memNoCtxSteps), refEq(steps));
    }

    @Test
    void separatedStepsMethodThrowsExceptionForNullSteps() {
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Ctx<>()
        );

        assertThatCode(() -> memNoCtxSteps.separatedSteps(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void separatedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>());
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<MemorizingNoCtxSteps<BaseCtxSteps<Object>>, RuntimeException> steps =
            mock(ThrowingConsumer.class);

        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> methodResult = memNoCtxSteps.separatedSteps(steps);
        assertThat(methodResult).isSameAs(memNoCtxSteps);
        verify(steps, times(1)).accept(refEq(memNoCtxSteps));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void replacements1PreviousMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object previousContext = new Object();
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps =
            new MemorizingNoCtxStepsOf<>(sf, sw, new Ctx<>(previousContext));

        final Map<String, Object> methodResult = memNoCtxSteps.stepNameReplacements();
        assertThat(methodResult).containsOnly(
            entry("1", previousContext)
        );
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void replacements3PreviousMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object previousContext1 = new Object();
        final Object previousContext2 = new Object();
        final Object previousContext3 = new Object();
        final MemorizingNoCtxSteps<BaseCtxSteps<Object>> memNoCtxSteps = new MemorizingNoCtxStepsOf<>(
            sf,
            sw,
            new CtxMem<>(previousContext1, new CtxMem<>(previousContext2, new Ctx<>(previousContext3)))
        );

        final Map<String, Object> methodResult = memNoCtxSteps.stepNameReplacements();
        assertThat(methodResult).containsOnly(
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
