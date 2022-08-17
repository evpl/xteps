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
import com.plugatar.xteps.base.OptionalValue;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.StepReporter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link FakeStepReporter}.
 */
final class FakeStepReporterTest {

    @Test
    void reportStepWithoutException() {
        final StepReporter reporter = new FakeStepReporter();
        final SafeACContainer container = mock(SafeACContainer.class);
        final ExceptionHandler handler = mock(ExceptionHandler.class);
        final OptionalValue<Object> optionalContext = OptionalValue.of(new Object());
        final Object expectedResult = new Object();

        final Object methodResult =
            reporter.report(container, handler, "step name", "step description", optionalContext, () -> expectedResult);
        assertThat(methodResult).isSameAs(expectedResult);
        verifyNoInteractions(container);
        verifyNoInteractions(handler);
    }

    @Test
    void reportStepWithException() {
        final StepReporter reporter = new FakeStepReporter();
        final SafeACContainer container = mock(SafeACContainer.class);
        final ExceptionHandler handler = mock(ExceptionHandler.class);
        final OptionalValue<Object> optionalContext = OptionalValue.of(new Object());
        final RuntimeException expectedException = new RuntimeException();

        assertThatCode(() -> {
            reporter.report(container, handler, "step name", "step description", optionalContext, () -> {
                throw expectedException;
            });
        }).isSameAs(expectedException);
        verify(container).close(same(expectedException));
        verify(handler).handle(same(expectedException));
    }
}
