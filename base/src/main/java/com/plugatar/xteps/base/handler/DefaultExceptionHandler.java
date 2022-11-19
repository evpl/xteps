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
package com.plugatar.xteps.base.handler;

import com.plugatar.xteps.base.ExceptionHandler;
import com.plugatar.xteps.base.XtepsException;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Predicate;

/**
 * ThreadLocal memorizing clean stack trace ExceptionHandler.
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    private static final ThreadLocal<Throwable> LAST_EXCEPTION = new ThreadLocal<>();
    private final Predicate<StackTraceElement> notXtepsClassStackTraceElementFilter;

    public DefaultExceptionHandler() {
        final String xtepsClassPrefix = "com.plugatar.xteps";
        this.notXtepsClassStackTraceElementFilter = element -> !element.getClassName().startsWith(xtepsClassPrefix);
    }

    @Override
    public final void handle(final Throwable exception) {
        if (exception == null) { throw new XtepsException("exception arg is null"); }
        /* Array size = 32, max count of elements without resizing = 10 */
        final Set<Throwable> allRelatedExceptions = Collections.newSetFromMap(new IdentityHashMap<>(8));
        recursivelyAddAllRelatedExceptions(allRelatedExceptions, exception);
        for (final Throwable currentEx : allRelatedExceptions) {
            final Throwable lastEx = LAST_EXCEPTION.get();
            if (!(currentEx instanceof XtepsException) && currentEx != lastEx) {
                LAST_EXCEPTION.set(currentEx);
                final StackTraceElement[] originST = currentEx.getStackTrace();
                if (originST.length != 0) {
                    final StackTraceElement[] cleanST = Arrays.stream(originST)
                        .filter(this.notXtepsClassStackTraceElementFilter)
                        .toArray(StackTraceElement[]::new);
                    if (cleanST.length != originST.length) {
                        currentEx.setStackTrace(cleanST);
                    }
                }
            }
        }
    }

    private static void recursivelyAddAllRelatedExceptions(final Set<Throwable> exceptions,
                                                           final Throwable mainEx) {
        for (Throwable causeEx = mainEx; causeEx != null; causeEx = causeEx.getCause()) {
            if (exceptions.contains(causeEx)) {
                break;
            }
            exceptions.add(causeEx);
            for (final Throwable suppressedEx : causeEx.getSuppressed()) {
                recursivelyAddAllRelatedExceptions(exceptions, suppressedEx);
            }
        }
    }
}
