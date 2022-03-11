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
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NoCtxStepsOf}.
 */
final class NoCtxStepsOfTest {

    @Test
    void classIsNotFinal() {
        assertThat(NoCtxStepsOf.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = NoCtxStepsOf.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullSfArg() {
        final StepNameFormatter sf = null;
        final StepWriter sw = mock(StepWriter.class);
        assertThatCode(() -> new NoCtxStepsOf(sf, sw))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullSwArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = null;
        assertThatCode(() -> new NoCtxStepsOf(sf, sw))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void toContextValueMethodDoesntThrowExceptionForNullContext() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );
        final Object context = new Object();

        assertThatCode(() -> noCtxSteps.toContext(context))
            .doesNotThrowAnyException();
    }

    @Test
    void toContextValueMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final Object context = new Object();

        final CtxSteps<Object> methodResult = noCtxSteps.toContext(context);
        assertThat(methodResult.context()).isSameAs(context);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextSupplierMethodThrowsExceptionForNullSupplier() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );
        final ThrowingSupplier<Object, RuntimeException> supplier = null;

        assertThatCode(() -> noCtxSteps.toContext(supplier))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void toContextSupplierMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final Object context = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingSupplier<Object, RuntimeException> supplier = mock(ThrowingSupplier.class);
        when(supplier.get()).thenReturn(context);

        final CtxSteps<Object> methodResult = noCtxSteps.toContext(supplier);
        assertThat(methodResult.context()).isSameAs(context);
        verify(supplier, times(1)).get();
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void emptyStepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        when(sf.format(any(), any())).thenReturn(sfResult);

        final NoCtxSteps methodResult = noCtxSteps.emptyStep(stepName);
        assertThat(methodResult).isSameAs(noCtxSteps);
        verify(sf).format(refEq(stepName), refEq(noCtxSteps));
        verify(sw).writeEmptyStep(refEq(sfResult));
    }

    @Test
    void stepMethodThrowsExceptionForNullStep() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );

        assertThatCode(() -> noCtxSteps.step("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void stepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingRunnable<RuntimeException> step = () -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final NoCtxSteps methodResult = noCtxSteps.step(stepName, step);
        assertThat(methodResult).isSameAs(noCtxSteps);
        verify(sf).format(refEq(stepName), refEq(noCtxSteps));
        verify(sw).writeRunnableStep(refEq(sfResult), refEq(step));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStep() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );

        assertThatCode(() -> noCtxSteps.stepToContext("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToContextMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingSupplier<Object, RuntimeException> step = Object::new;
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeSupplierStep(any(), (ThrowingSupplier<Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final CtxSteps<Object> methodResult = noCtxSteps.stepToContext(stepName, step);
        assertThat(methodResult.context()).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(noCtxSteps));
        verify(sw).writeSupplierStep(refEq(sfResult), refEq(step));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStep() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );

        assertThatCode(() -> noCtxSteps.stepTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingSupplier<Object, RuntimeException> step = Object::new;
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeSupplierStep(any(), (ThrowingSupplier<Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final Object methodResult = noCtxSteps.stepTo(stepName, step);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(noCtxSteps));
        verify(sw).writeSupplierStep(refEq(sfResult), refEq(step));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullSteps() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );

        assertThatCode(() -> noCtxSteps.nestedSteps("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void nestedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingConsumer<NoCtxSteps, RuntimeException> steps = s -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final NoCtxSteps methodResult = noCtxSteps.nestedSteps(stepName, steps);
        assertThat(methodResult).isSameAs(noCtxSteps);
        verify(sf).format(refEq(stepName), refEq(noCtxSteps));
        verify(sw).writeConsumerStep(refEq(sfResult), refEq(noCtxSteps), refEq(steps));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullSteps() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );

        assertThatCode(() -> noCtxSteps.nestedStepsTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void nestedStepsToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<NoCtxSteps, Object, RuntimeException> steps = s -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(any(), any(), (ThrowingFunction<NoCtxSteps, Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final Object methodResult = noCtxSteps.nestedStepsTo(stepName, steps);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(noCtxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(noCtxSteps), refEq(steps));
    }

    @Test
    void separatedStepsMethodThrowsExceptionForNullSteps() {
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(
            mock(StepNameFormatter.class),
            mock(StepWriter.class)
        );

        assertThatCode(() -> noCtxSteps.separatedSteps(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void separatedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<NoCtxSteps, RuntimeException> steps = mock(ThrowingConsumer.class);

        final NoCtxSteps methodResult = noCtxSteps.separatedSteps(steps);
        assertThat(methodResult).isSameAs(noCtxSteps);
        verify(steps, times(1)).accept(refEq(noCtxSteps));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void replacementsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final NoCtxSteps noCtxSteps = new NoCtxStepsOf(sf, sw);

        final Map<String, Object> methodResult = noCtxSteps.stepNameReplacements();
        assertThat(methodResult).isEmpty();
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }
}
