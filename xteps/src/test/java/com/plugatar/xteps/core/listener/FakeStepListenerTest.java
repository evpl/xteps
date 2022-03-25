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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link FakeStepListener}.
 */
final class FakeStepListenerTest {

    @Test
    void classIsNotFinal() {
        assertThat(FakeStepListener.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = FakeStepListener.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void stepStartedMethodDoesNotThrowAnyException() {
        final StepListener listener = new FakeStepListener();
        assertThatCode(() -> listener.stepStarted("uuid", "step name"))
            .doesNotThrowAnyException();
    }

    @Test
    void stepPassedMethodDoesNotThrowAnyException() {
        final StepListener listener = new FakeStepListener();
        assertThatCode(() -> listener.stepPassed("uuid", "step name"))
            .doesNotThrowAnyException();
    }

    @Test
    void stepFailedMethodDoesNotThrowAnyException() {
        final StepListener listener = new FakeStepListener();
        assertThatCode(() -> listener.stepFailed("uuid", "step name", new RuntimeException()))
            .doesNotThrowAnyException();
    }
}
