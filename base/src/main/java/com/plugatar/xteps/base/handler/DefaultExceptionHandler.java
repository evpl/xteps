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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Default ExceptionHandler.
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    private static final ThreadLocal<FixedMaxSizeUniqueQueue<Throwable>> EXCEPTIONS =
        ThreadLocal.withInitial(FixedMaxSizeUniqueQueue::new);
    private final Predicate<StackTraceElement> notXtepsClassStackTraceElementFilter;

    /**
     * Ctor.
     */
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
            if (!(currentEx instanceof XtepsException) && EXCEPTIONS.get().offer(currentEx)) {
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

    private static final class FixedMaxSizeUniqueQueue<T> {
        private final Set<T> set;
        private final Queue<T> queue;

        private FixedMaxSizeUniqueQueue() {
            /* Array size = 32, max count of elements without resizing = 10 */
            this.set = Collections.newSetFromMap(new IdentityHashMap<>(8));
            /* Array size = 16, max count of elements without resizing = 15 */
            this.queue = new ArrayDeque<>(8);
        }

        private boolean offer(final T element) {
            if (this.set.add(element)) {
                this.queue.add(element);
                /* To avoid arrays resizing this queue contains only 9 elements, 10th will be removed */
                if (this.set.size() == 10) {
                    this.set.remove(this.queue.remove());
                }
                return true;
            }
            return false;
        }
    }
}
