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

import java.util.NoSuchElementException;

/**
 * Optional value.
 *
 * @param <V> the value type
 */
public interface OptionalValue<V> {

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     * @see #value()
     */
    boolean isPresent();

    /**
     * If a value is present in this {@code OptionalValue}, returns value,
     * otherwise throws {@link NoSuchElementException}. Value may be null.
     *
     * @return value, may be null
     * @throws NoSuchElementException if there is no value present
     * @see #isPresent()
     */
    V value();

    /**
     * Returns an {@code OptionalValue} of given value.
     *
     * @param value the value
     * @param <V>   the value type
     * @return optional value
     * @see #empty()
     */
    static <V> OptionalValue<V> of(final V value) {
        return new Of<>(value);
    }

    /**
     * Returns an empty {@code OptionalValue}.
     *
     * @param <V> the value type
     * @return optional value
     * @see #of(Object)
     */
    @SuppressWarnings("unchecked")
    static <V> OptionalValue<V> empty() {
        return (OptionalValue<V>) Empty.INSTANCE;
    }

    /**
     * OptionalValue with value.
     *
     * @param <V> the value type
     */
    final class Of<V> implements OptionalValue<V> {
        private final V value;

        /**
         * Ctor.
         *
         * @param value the value
         */
        private Of(final V value) {
            this.value = value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public V value() {
            return this.value;
        }
    }

    /**
     * Empty OptionalValue.
     *
     * @param <V> the value type
     */
    final class Empty<V> implements OptionalValue<V> {
        private static final OptionalValue<?> INSTANCE = new Empty<>();

        /**
         * Ctor.
         */
        private Empty() {
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public V value() {
            throw new NoSuchElementException("No value present");
        }
    }
}
