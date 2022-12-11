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
import com.plugatar.xteps.base.HooksContainer;
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
    public final <R, E extends Throwable> R report(
        final HooksContainer hooksContainer,
        final ExceptionHandler exceptionHandler,
        final String name,
        final String description,
        final Object[] params,
        final ThrowingSupplier<? extends R, ? extends E> action
    ) throws E {
        if (hooksContainer == null) { throwNullArgException("hooksContainer"); }
        if (exceptionHandler == null) { throwNullArgException("exceptionHandler"); }
        if (name == null) { throwNullArgException("name"); }
        if (description == null) { throwNullArgException("description"); }
        if (params == null) { throwNullArgException("params"); }
        if (action == null) { throwNullArgException("action"); }
        try {
            return action.get();
        } catch (final Throwable stepEx) {
            hooksContainer.callHooks(stepEx);
            exceptionHandler.handle(stepEx);
            throw stepEx;
        }
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
    }
}
