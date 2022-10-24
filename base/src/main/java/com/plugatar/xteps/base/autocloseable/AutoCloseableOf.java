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
package com.plugatar.xteps.base.autocloseable;

import com.plugatar.xteps.base.ThrowingRunnable;

import static com.plugatar.xteps.base.ThrowingRunnable.uncheckedRunnable;

/**
 * AutoCloseable of another type object.
 */
public class AutoCloseableOf implements AutoCloseable {
    private final ThrowingRunnable<?> close;

    /**
     * Ctor.
     *
     * @param close the close action
     */
    public AutoCloseableOf(final ThrowingRunnable<?> close) {
        if (close == null) { throw new NullPointerException("close arg is null"); }
        this.close = close;
    }

    @Override
    public final void close() {
        uncheckedRunnable(this.close).run();
    }
}