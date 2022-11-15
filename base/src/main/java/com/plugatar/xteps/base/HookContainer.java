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

/**
 * Hook container.
 */
public interface HookContainer {

    /**
     * Adds given hook to this container.
     *
     * @param hook the hook
     * @throws XtepsException if {@code hook} is null
     */
    void add(ThrowingRunnable<?> hook);

    /**
     * Calls all hooks in this container. Exceptions will be added to the
     * base exception as suppressed exceptions.
     *
     * @throws XtepsException if one or more hooks threw exceptions
     */
    void callHooks();

    /**
     * Calls all hooks in this container. Exceptions will be added to the
     * given base exception as suppressed exceptions.
     *
     * @param baseException the base exception
     * @throws XtepsException if {@code baseException} is null
     */
    void callHooks(Throwable baseException);
}
