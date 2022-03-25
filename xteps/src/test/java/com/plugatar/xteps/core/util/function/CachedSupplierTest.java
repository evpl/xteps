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
package com.plugatar.xteps.core.util.function;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link CachedSupplier}.
 */
final class CachedSupplierTest {

    @Test
    void classIsNotFinal() {
        assertThat(CachedSupplier.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = CachedSupplier.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .filteredOn(method -> !(method.getName().equals("get") && method.getReturnType() == Object.class))
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullOrigin() {
        assertThatCode(() -> new CachedSupplier<>(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getMethodForNullResult() {
        @SuppressWarnings("unchecked")
        final Supplier<Object> origin = mock(Supplier.class);
        when(origin.get()).thenReturn(null, new Object());
        final Supplier<Object> cachedSupplier = new CachedSupplier<>(origin);

        assertThat(cachedSupplier.get()).isNull();
        assertThat(cachedSupplier.get()).isNull();
        verify(origin, times(1)).get();
    }

    @Test
    void getMethodForNotNullResult() {
        @SuppressWarnings("unchecked")
        final Supplier<Object> origin = mock(Supplier.class);
        final Object firstResult = new Object();
        when(origin.get()).thenReturn(firstResult, new Object());
        final Supplier<Object> cachedSupplier = new CachedSupplier<>(origin);

        assertThat(cachedSupplier.get()).isSameAs(firstResult);
        assertThat(cachedSupplier.get()).isSameAs(firstResult);
        verify(origin, times(1)).get();
    }

    @Test
    void getMethodForException() {
        @SuppressWarnings("unchecked")
        final Supplier<Object> origin = mock(Supplier.class);
        doThrow(new RuntimeException()).when(origin).get();
        final Supplier<Object> cachedSupplier = new CachedSupplier<>(origin);

        assertThatCode(() -> cachedSupplier.get())
            .isInstanceOf(RuntimeException.class);
        assertThatCode(() -> cachedSupplier.get())
            .isInstanceOf(RuntimeException.class);
        verify(origin, times(2)).get();
    }
}
