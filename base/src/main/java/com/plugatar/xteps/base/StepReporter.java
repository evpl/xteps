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
 * Step reporter.
 */
public interface StepReporter {

    /**
     * Reports given step and returns step result.
     *
     * @param exceptionHandler the exception handler
     * @param hooksContainer   the hooks container
     * @param name             the step name
     * @param description      the step description
     * @param params           the step params array
     * @param action           the step action
     * @param <R>              the type of the {@code action} result
     * @param <E>              the {@code action} exception type
     * @return the {@code action} result
     * @throws XtepsException if {@code name} or {@code description} or {@code params}
     *                        or {@code action} is null
     * @throws E              if {@code action} threw exception
     */
    <R, E extends Throwable> R report(
        HooksContainer hooksContainer,
        ExceptionHandler exceptionHandler,
        String name,
        String description,
        Object[] params,
        ThrowingSupplier<? extends R, ? extends E> action
    ) throws E;
}
