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
package com.plugatar.xteps.core.chain;

import com.plugatar.xteps.core.XtepsException;

import java.util.Deque;

/**
 * Utility class. Contains methods for {@link com.plugatar.xteps.core.chain} package
 * {@link com.plugatar.xteps.core.BaseStepsChain} implementations.
 */
final class StepsChainUtils {

    /**
     * Utility class ctor.
     */
    private StepsChainUtils() {
    }

    /**
     * Close all {@link AutoCloseable}s from given deque in the descending order and
     * rethrow given exception.
     *
     * @param acDeque       the {@link AutoCloseable} deque
     * @param baseException the base exception
     * @return fake value
     * @throws Throwable {@code baseException} if any case
     */
    static RuntimeException closeAllAutoCloseablesAndSneakyRethrow(final Deque<AutoCloseable> acDeque,
                                                                   final Throwable baseException) {
        for (AutoCloseable ac = acDeque.pollLast(); ac != null; ac = acDeque.pollLast()) {
            try {
                ac.close();
            } catch (final Throwable ex) {
                baseException.addSuppressed(ex);
            }
        }
        throw sneakyThrow(baseException);
    }

    /**
     * Close all {@link AutoCloseable}s from given deque in the descending order.
     *
     * @param acDeque the {@link AutoCloseable} deque
     * @throws XtepsException if one of {@link AutoCloseable#close()} methods
     *                        invocation throws any exception
     */
    static void closeAllAutoCloseables(final Deque<AutoCloseable> acDeque) {
        XtepsException baseException = null;
        for (AutoCloseable ac = acDeque.pollLast(); ac != null; ac = acDeque.pollLast()) {
            try {
                ac.close();
            } catch (final Throwable ex) {
                if (baseException == null) {
                    baseException = new XtepsException(
                        "One or more AutoCloseable contexts cannot be closed (see suppressed exceptions)"
                    );
                }
                baseException.addSuppressed(ex);
            }
        }
        if (baseException != null) {
            throw baseException;
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException sneakyThrow(final Throwable ex) throws E {
        throw (E) ex;
    }
}
