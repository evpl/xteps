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
package com.plugatar.xteps.base.testit;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link TestITStepListener}.
 */
final class TestITStepListenerTest {

    @Test
    void stepStartedMethod() {
        assertThatCode(() -> new TestITStepListener().stepStarted(
            UUID.randomUUID().toString(),
            "step name",
            "step description",
            new Object[]{}
        )).doesNotThrowAnyException();
    }

    @Test
    void stepPassedMethod() {
        assertThatCode(() -> new TestITStepListener().stepPassed(
            UUID.randomUUID().toString()
        )).doesNotThrowAnyException();
    }

    @Test
    void stepFailedMethod() {
        assertThatCode(() -> new TestITStepListener().stepFailed(
            UUID.randomUUID().toString(),
            new Throwable()
        )).doesNotThrowAnyException();
    }
}
