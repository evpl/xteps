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
 * Xteps base.
 */
public interface XtepsBase {

    /**
     * Returns StepReporter.
     *
     * @return StepReporter
     */
    StepReporter stepReporter();

    /**
     * Returns ExceptionHandler.
     *
     * @return ExceptionHandler
     */
    ExceptionHandler exceptionHandler();

    /**
     * Returns HookContainer generator.
     *
     * @return HookContainer generator
     */
    ThrowingSupplier<HookContainer, RuntimeException> hookContainerGenerator();

    /**
     * Returns thread hook interval in milliseconds.
     *
     * @return thread hook interval
     */
    long threadHookInterval();

    /**
     * Returns thread hook priority in the range {@link Thread#MIN_PRIORITY} to
     * {@link Thread#MAX_PRIORITY}.
     *
     * @return thread hook priority
     */
    int threadHookPriority();

    /**
     * Returns cached XtepsBase instance.
     *
     * @return cached XtepsBase instance
     * @throws XtepsException if Xteps configuration is incorrect
     */
    static XtepsBase cached() {
        return XtepsBaseProvider.CACHED_XTEPS_BASE.get();
    }
}
