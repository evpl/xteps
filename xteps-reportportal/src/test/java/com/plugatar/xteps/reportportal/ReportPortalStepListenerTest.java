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
package com.plugatar.xteps.reportportal;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

final class ReportPortalStepListenerTest {

    @Test
    void classIsNotFinal() {
        assertThat(ReportPortalStepListener.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = ReportPortalStepListener.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void stepStartedMethod() {
        assertThatCode(() -> new ReportPortalStepListener().stepStarted(
            UUID.randomUUID().toString(),
            "step name"
        )).doesNotThrowAnyException();
    }

    @Test
    void stepPassedMethod() {
        assertThatCode(() -> new ReportPortalStepListener().stepPassed(
            UUID.randomUUID().toString(),
            "step name"
        )).doesNotThrowAnyException();
    }

    @Test
    void stepFailedMethod() {
        assertThatCode(() -> new ReportPortalStepListener().stepFailed(
            UUID.randomUUID().toString(),
            "step name",
            new Throwable()
        )).doesNotThrowAnyException();
    }
}
