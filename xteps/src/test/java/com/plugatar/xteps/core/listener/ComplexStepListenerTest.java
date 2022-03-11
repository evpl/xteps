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
package com.plugatar.xteps.core.listener;

import com.plugatar.xteps.core.StepListener;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ComplexStepListener}.
 */
final class ComplexStepListenerTest {

    @Test
    void classIsNotFinal() {
        assertThat(ComplexStepListener.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = ComplexStepListener.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullArray() {
        assertThatCode(() -> new ComplexStepListener((StepListener[]) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void stepStarted1ListenerMethod() {
        final StepListener listener = mock(StepListener.class);
        final StepListener complexListener = new ComplexStepListener(listener);
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        complexListener.stepStarted(uuid, stepName);
        verify(listener).stepStarted(refEq(uuid), refEq(stepName));
        verify(listener, never()).stepPassed(any(), any());
        verify(listener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void stepStarted3ListenersMethod() {
        final StepListener listener1 = mock(StepListener.class);
        final StepListener listener2 = mock(StepListener.class);
        final StepListener listener3 = mock(StepListener.class);
        final StepListener complexListener = new ComplexStepListener(listener1, listener2, listener3);
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        complexListener.stepStarted(uuid, stepName);
        verify(listener1).stepStarted(refEq(uuid), refEq(stepName));
        verify(listener1, never()).stepPassed(any(), any());
        verify(listener1, never()).stepFailed(any(), any(), any());
        verify(listener2).stepStarted(refEq(uuid), refEq(stepName));
        verify(listener2, never()).stepPassed(any(), any());
        verify(listener2, never()).stepFailed(any(), any(), any());
        verify(listener3).stepStarted(refEq(uuid), refEq(stepName));
        verify(listener3, never()).stepPassed(any(), any());
        verify(listener3, never()).stepFailed(any(), any(), any());
    }

    @Test
    void stepPassed1ListenerMethod() {
        final StepListener listener = mock(StepListener.class);
        final StepListener complexListener = new ComplexStepListener(listener);
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        complexListener.stepPassed(uuid, stepName);
        verify(listener).stepPassed(refEq(uuid), refEq(stepName));
        verify(listener, never()).stepStarted(any(), any());
        verify(listener, never()).stepFailed(any(), any(), any());
    }

    @Test
    void stepPassed3ListenersMethod() {
        final StepListener listener1 = mock(StepListener.class);
        final StepListener listener2 = mock(StepListener.class);
        final StepListener listener3 = mock(StepListener.class);
        final StepListener complexListener = new ComplexStepListener(listener1, listener2, listener3);
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";

        complexListener.stepPassed(uuid, stepName);
        verify(listener1).stepPassed(refEq(uuid), refEq(stepName));
        verify(listener1, never()).stepStarted(any(), any());
        verify(listener1, never()).stepFailed(any(), any(), any());
        verify(listener2).stepPassed(refEq(uuid), refEq(stepName));
        verify(listener2, never()).stepStarted(any(), any());
        verify(listener2, never()).stepFailed(any(), any(), any());
        verify(listener3).stepPassed(refEq(uuid), refEq(stepName));
        verify(listener3, never()).stepStarted(any(), any());
        verify(listener3, never()).stepFailed(any(), any(), any());
    }

    @Test
    void stepFailed1ListenerMethod() {
        final StepListener listener = mock(StepListener.class);
        final StepListener complexListener = new ComplexStepListener(listener);
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final RuntimeException exception = new RuntimeException();

        complexListener.stepFailed(uuid, stepName, exception);
        verify(listener).stepFailed(refEq(uuid), refEq(stepName), refEq(exception));
        verify(listener, never()).stepStarted(any(), any());
        verify(listener, never()).stepPassed(any(), any());
    }

    @Test
    void stepFailed3ListenersMethod() {
        final StepListener listener1 = mock(StepListener.class);
        final StepListener listener2 = mock(StepListener.class);
        final StepListener listener3 = mock(StepListener.class);
        final StepListener complexListener = new ComplexStepListener(listener1, listener2, listener3);
        final String uuid = UUID.randomUUID().toString();
        final String stepName = "step name";
        final RuntimeException exception = new RuntimeException();

        complexListener.stepFailed(uuid, stepName, exception);
        verify(listener1).stepFailed(refEq(uuid), refEq(stepName), refEq(exception));
        verify(listener1, never()).stepStarted(any(), any());
        verify(listener1, never()).stepPassed(any(), any());
        verify(listener2).stepFailed(refEq(uuid), refEq(stepName), refEq(exception));
        verify(listener2, never()).stepStarted(any(), any());
        verify(listener2, never()).stepPassed(any(), any());
        verify(listener3).stepFailed(refEq(uuid), refEq(stepName), refEq(exception));
        verify(listener3, never()).stepStarted(any(), any());
        verify(listener3, never()).stepPassed(any(), any());
    }
}
