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
package com.plugatar.xteps.base;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link OptionalValue}.
 */
final class OptionalValueTest {

    @Test
    void emptyMethod() {
        final OptionalValue<Object> optionalValue = OptionalValue.empty();
        assertThat(optionalValue.isPresent()).isFalse();
        assertThatCode(() -> optionalValue.value())
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void emptyMethodReturnsSingleton() {
        final OptionalValue<Object> optionalValue = OptionalValue.empty();
        assertThat(OptionalValue.empty()).isSameAs(optionalValue);
        assertThat(OptionalValue.empty()).isSameAs(optionalValue);
    }

    @Test
    void ofMethodWithNullValue() {
        final OptionalValue<Object> optionalValue = OptionalValue.of(null);
        assertThat(optionalValue.isPresent()).isTrue();
        assertThat(optionalValue.value()).isNull();
    }

    @Test
    void ofMethodWithNotNullValue() {
        final Object value = new Object();
        final OptionalValue<Object> optionalValue = OptionalValue.of(value);
        assertThat(optionalValue.isPresent()).isTrue();
        assertThat(optionalValue.value()).isSameAs(value);
    }
}
