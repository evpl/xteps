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
package com.plugatar.xteps.base.reporter;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.OptionalValue;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.StepReporter;
import com.plugatar.xteps.base.ThrowingSupplier;
import com.plugatar.xteps.base.XtepsException;

/**
 * Fake StepReporter.
 */
public class FakeStepReporter implements StepReporter {

    /**
     * Ctor.
     */
    public FakeStepReporter() {
    }

    @Override
    public <R, E extends Throwable> R report(
        final ExceptionHandler exceptionHandler,
        final String stepName,
        final String stepDescription,
        final OptionalValue<?> optionalContext,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        if (exceptionHandler == null) { throwNullArgException("exceptionHandler"); }
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (optionalContext == null) { throwNullArgException("optionalContext"); }
        if (step == null) { throwNullArgException("step"); }
        try {
            return step.get();
        } catch (final Throwable stepEx) {
            exceptionHandler.handle(stepEx);
            throw stepEx;
        }
    }

    @Override
    public final <R, E extends Throwable> R report(
        final SafeACContainer safeACContainer,
        final ExceptionHandler exceptionHandler,
        final String stepName,
        final String stepDescription,
        final OptionalValue<?> optionalContext,
        final ThrowingSupplier<? extends R, ? extends E> step
    ) throws E {
        if (safeACContainer == null) { throwNullArgException("safeACContainer"); }
        if (exceptionHandler == null) { throwNullArgException("exceptionHandler"); }
        if (stepName == null) { throwNullArgException("stepName"); }
        if (stepDescription == null) { throwNullArgException("stepDescription"); }
        if (optionalContext == null) { throwNullArgException("optionalContext"); }
        if (step == null) { throwNullArgException("step"); }
        try {
            return step.get();
        } catch (final Throwable stepEx) {
            safeACContainer.close(stepEx);
            exceptionHandler.handle(stepEx);
            throw stepEx;
        }
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }
}
