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
 * Tests for {@link CtxStepsOf}.
 */
final class CtxStepsOfTest {

    @Test
    void classIsNotFinal() {
        assertThat(CtxStepsOf.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = CtxStepsOf.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullSfArg() {
        final StepNameFormatter sf = null;
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        assertThatCode(() -> new CtxStepsOf<>(sf, sw, context))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullSwArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = null;
        final Object context = new Object();
        assertThatCode(() -> new CtxStepsOf<>(sf, sw, context))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorDoesntThrowExceptionForNullContextArg() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = null;
        assertThatCode(() -> new CtxStepsOf<>(sf, sw, context))
            .doesNotThrowAnyException();
    }

    @Test
    void contextMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);

        assertThat(ctxSteps.context()).isSameAs(context);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void supplyContextToMethodThrowsExceptionForNullConsumer() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.supplyContextTo(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void supplyContextToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<Object, RuntimeException> consumer = mock(ThrowingConsumer.class);

        final CtxSteps<Object> methodResult = ctxSteps.supplyContextTo(consumer);
        assertThat(methodResult).isSameAs(ctxSteps);
        verify(consumer, times(1)).accept(refEq(context));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void applyContextToMethodThrowsExceptionForNullFunction() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.applyContextTo(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void applyContextToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final Object newContext = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(newContext);

        final Object methodResult = ctxSteps.applyContextTo(function);
        assertThat(methodResult).isSameAs(newContext);
        verify(function, times(1)).apply(refEq(context));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void noContextStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, new Object());

        final MemorizingNoCtxSteps<CtxSteps<Object>> methodResult = ctxSteps.noContextSteps();
        assertThat(methodResult.previous()).isSameAs(ctxSteps);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextValueMethodDoesntThrowExceptionForNullContext() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );
        final Object context = null;

        assertThatCode(() -> ctxSteps.toContext(context))
            .doesNotThrowAnyException();
    }

    @Test
    void toContextValueMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, new Object());
        final Object newContext = new Object();

        final MemorizingCtxSteps<Object, CtxSteps<Object>> methodResult = ctxSteps.toContext(newContext);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previous()).isSameAs(ctxSteps);
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void toContextSupplierMethodThrowsExceptionForNullFunction() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );
        final ThrowingFunction<Object, Object, RuntimeException> function = null;

        assertThatCode(() -> ctxSteps.toContext(function))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void toContextSupplierMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final Object newContext = new Object();
        @SuppressWarnings("unchecked")
        final ThrowingFunction<Object, Object, RuntimeException> function = mock(ThrowingFunction.class);
        when(function.apply(any())).thenReturn(newContext);

        final MemorizingCtxSteps<Object, CtxSteps<Object>> methodResult = ctxSteps.toContext(function);
        assertThat(methodResult.context()).isSameAs(newContext);
        assertThat(methodResult.previous()).isSameAs(ctxSteps);
        verify(function).apply(refEq(context));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void emptyStepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, new Object());
        final String sfResult = "sf result";
        final String stepName = "step name";
        when(sf.format(any(), any())).thenReturn(sfResult);

        final CtxSteps<Object> methodResult = ctxSteps.emptyStep(stepName);
        assertThat(methodResult).isSameAs(ctxSteps);
        verify(sf).format(refEq(stepName), refEq(ctxSteps));
        verify(sw).writeEmptyStep(refEq(sfResult));
    }

    @Test
    void stepMethodThrowsExceptionForNullStep() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.step("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void stepMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingConsumer<Object, RuntimeException> step = c -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final CtxSteps<Object> methodResult = ctxSteps.step(stepName, step);
        assertThat(methodResult).isSameAs(ctxSteps);
        verify(sf).format(refEq(stepName), refEq(ctxSteps));
        verify(sw).writeConsumerStep(refEq(sfResult), refEq(context), refEq(step));
    }

    @Test
    void stepToContextMethodThrowsExceptionForNullStep() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.stepToContext("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToContextMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> step = c -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(any(), any(), (ThrowingFunction<Object, Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final MemorizingCtxSteps<Object, CtxSteps<Object>> methodResult = ctxSteps.stepToContext(stepName, step);
        assertThat(methodResult.context()).isSameAs(swResult);
        assertThat(methodResult.previous()).isSameAs(ctxSteps);
        verify(sf).format(refEq(stepName), refEq(ctxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(context), refEq(step));
    }

    @Test
    void stepToMethodThrowsExceptionForNullStep() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.stepTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stepToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<Object, Object, RuntimeException> step = c -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(any(), any(), (ThrowingFunction<Object, Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final Object methodResult = ctxSteps.stepTo(stepName, step);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(ctxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(context), refEq(step));
    }

    @Test
    void nestedStepsMethodThrowsExceptionForNullSteps() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.nestedSteps("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void nestedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingConsumer<CtxSteps<Object>, RuntimeException> steps = c -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);

        final CtxSteps<Object> methodResult = ctxSteps.nestedSteps(stepName, steps);
        assertThat(methodResult).isSameAs(ctxSteps);
        verify(sf).format(refEq(stepName), refEq(ctxSteps));
        verify(sw).writeConsumerStep(refEq(sfResult), refEq(ctxSteps), refEq(steps));
    }

    @Test
    void nestedStepsToMethodThrowsExceptionForNullSteps() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.nestedStepsTo("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void nestedStepsToMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final Object swResult = new Object();
        final ThrowingFunction<CtxSteps<Object>, Object, RuntimeException> steps = s -> new Object();
        when(sf.format(any(), any())).thenReturn(sfResult);
        when(sw.writeFunctionStep(any(), any(), (ThrowingFunction<CtxSteps<Object>, Object, RuntimeException>) any()))
            .thenReturn(swResult);

        final Object methodResult = ctxSteps.nestedStepsTo(stepName, steps);
        assertThat(methodResult).isSameAs(swResult);
        verify(sf).format(refEq(stepName), refEq(ctxSteps));
        verify(sw).writeFunctionStep(refEq(sfResult), refEq(ctxSteps), refEq(steps));
    }

    @Test
    void separatedStepsMethodThrowsExceptionForNullSteps() {
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(
            mock(StepNameFormatter.class),
            mock(StepWriter.class),
            new Object()
        );

        assertThatCode(() -> ctxSteps.separatedSteps(null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void separatedStepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, new Object());
        @SuppressWarnings("unchecked")
        final ThrowingConsumer<CtxSteps<Object>, RuntimeException> steps = mock(ThrowingConsumer.class);

        final CtxSteps<Object> methodResult = ctxSteps.separatedSteps(steps);
        assertThat(methodResult).isSameAs(ctxSteps);
        verify(steps, times(1)).accept(refEq(ctxSteps));
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }

    @Test
    void replacementsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final Object context = new Object();
        final CtxSteps<Object> ctxSteps = new CtxStepsOf<>(sf, sw, context);

        final Map<String, Object> methodResult = ctxSteps.stepNameReplacements();
        assertThat(methodResult).containsOnly(
            entry("context", context),
            entry("0", context)
        );
        verifyNoInteractions(sf);
        verifyNoInteractions(sw);
    }
}
