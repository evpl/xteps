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
 * Cached throwing supplier.
 *
 * @param <T> the type of the result
 */
public class CachedThrowingSupplier<T, TH extends Throwable> implements ThrowingSupplier<T, TH> {
    private volatile ThrowingSupplier<? extends T, ? extends TH> origin;
    private final Object lock;
    private volatile boolean init;
    private T value;

    /**
     * Ctor.
     *
     * @param origin the origin throwing supplier
     * @throws NullPointerException if {@code origin} is null
     */
    public CachedThrowingSupplier(final ThrowingSupplier<? extends T, ? extends TH> origin) {
        if (origin == null) { throw new NullPointerException("origin arg is null"); }
        this.origin = origin;
        this.lock = new Object();
        this.init = false;
        this.value = null;
    }

    @Override
    public final T get() throws TH {
        if (!this.init) {
            synchronized (this.lock) {
                if (!this.init) {
                    final T result = this.origin.get();
                    this.value = result;
                    this.init = true;
                    this.origin = null;
                    return result;
                }
            }
        }
        return this.value;
    }
}
