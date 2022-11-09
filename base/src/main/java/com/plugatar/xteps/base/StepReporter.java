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
     * @param exceptionHandler the step name
     * @param stepName         the safe AutoCloseable container
     * @param stepDescription  the step description
     * @param contexts         the contexts array
     * @param step             the origin supplier
     * @param <R>              the type of the {@code step} result
     * @param <E>              the {@code step} exception type
     * @return the {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code contexts}
     *                        or {@code step} is null
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R report(
        ExceptionHandler exceptionHandler,
        String stepName,
        String stepDescription,
        Object[] contexts,
        ThrowingSupplier<? extends R, ? extends E> step
    ) throws E;


    /**
     * Reports given step and returns step result.
     *
     * @param exceptionHandler the step name
     * @param safeACContainer  the exception handler
     * @param stepName         the safe AutoCloseable container
     * @param stepDescription  the step description
     * @param contexts         the contexts array
     * @param step             the origin supplier
     * @param <R>              the type of the {@code step} result
     * @param <E>              the {@code step} exception type
     * @return the {@code step} result
     * @throws XtepsException if {@code stepName} or {@code stepDescription} or {@code contexts}
     *                        or {@code step} is null
     * @throws E              if {@code step} threw exception
     */
    <R, E extends Throwable> R report(
        SafeACContainer safeACContainer,
        ExceptionHandler exceptionHandler,
        String stepName,
        String stepDescription,
        Object[] contexts,
        ThrowingSupplier<? extends R, ? extends E> step
    ) throws E;
}
