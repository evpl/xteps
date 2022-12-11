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
 * Hooks container.
 */
public interface HooksContainer {

    /**
     * Adds given hook to this container.
     *
     * @param priority the priority
     * @param hook     the hook
     * @throws XtepsException if {@code hook} is null
     *                        or if {@code priority} is not in the range {@link HookPriority#MIN_HOOK_PRIORITY} to
     *                        {@link HookPriority#MAX_HOOK_PRIORITY}
     */
    void addHook(int priority,
                 ThrowingRunnable<?> hook);

    /**
     * Sets given hooks order.
     *
     * @param order the hooks order
     * @throws XtepsException if {@code order} is null
     */
    void setOrder(HooksOrder order);

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
