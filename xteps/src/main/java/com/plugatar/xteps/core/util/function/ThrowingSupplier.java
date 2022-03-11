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

/**
 * The {@link java.util.function.Supplier} specialization that might
 * throw a Throwable.
 *
 * @param <T>  the type of the result
 * @param <TH> the type of the thrown Throwable
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T, TH extends Throwable> {

    /**
     * Gets the result.
     *
     * @return the result
     * @throws TH if supplier threw exception
     */
    T get() throws TH;
}
