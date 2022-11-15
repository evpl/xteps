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
package com.plugatar.xteps.base.reporter;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.HookContainer;
import com.plugatar.xteps.base.StepListener;
import com.plugatar.xteps.base.StepReporter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link DefaultStepReporter}.
 */
final class DefaultStepReporterTest {

    @Test
    void ctorThrowsExceptionForNullStepListenerArray() {
        assertThatCode(() -> new DefaultStepReporter((StepListener[]) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForNullStepListenerInArray() {
        final StepListener[] stepListeners = {mock(StepListener.class), (StepListener) null, mock(StepListener.class)};

        assertThatCode(() -> new DefaultStepReporter(stepListeners))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ctorThrowsExceptionForEmptyStepListenerArray() {
        final StepListener[] stepListeners = {};

        assertThatCode(() -> new DefaultStepReporter(stepListeners))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reportStepWithoutException() {
        final StepListener stepListener1 = mock(StepListener.class);
        final StepListener stepListener2 = mock(StepListener.class);
        final StepReporter reporter = new DefaultStepReporter(new StepListener[]{stepListener1, stepListener2});
        final HookContainer container = mock(HookContainer.class);
        final ExceptionHandler handler = mock(ExceptionHandler.class);
        final Object[] contexts = new Object[]{};
        final Object expectedResult = new Object();

        final Object methodResult =
            reporter.report(container, handler, "step name", "step description", contexts, () -> expectedResult);
        assertThat(methodResult).isSameAs(expectedResult);
        verify(stepListener1).stepStarted(any(), eq("step name"), eq("step description"), same(contexts));
        verify(stepListener1).stepPassed(any());
        verify(stepListener2).stepStarted(any(), eq("step name"), eq("step description"), same(contexts));
        verify(stepListener2).stepPassed(any());
    }

    @Test
    void reportStepWithException() {
        final StepListener stepListener1 = mock(StepListener.class);
        final StepListener stepListener2 = mock(StepListener.class);
        final StepReporter reporter = new DefaultStepReporter(new StepListener[]{stepListener1, stepListener2});
        final HookContainer container = mock(HookContainer.class);
        final ExceptionHandler handler = mock(ExceptionHandler.class);
        final Object[] contexts = new Object[]{};
        final RuntimeException expectedException = new RuntimeException();

        assertThatCode(() -> {
            reporter.report(container, handler, "step name", "step description", contexts, () -> {
                throw expectedException;
            });
        }).isSameAs(expectedException);
        verify(stepListener1).stepStarted(any(), eq("step name"), eq("step description"), same(contexts));
        verify(stepListener1).stepFailed(any(), same(expectedException));
        verify(stepListener2).stepStarted(any(), eq("step name"), eq("step description"), same(contexts));
        verify(stepListener2).stepFailed(any(), same(expectedException));
    }
}
