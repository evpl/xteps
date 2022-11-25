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
package com.plugatar.xteps.base.hook;

import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsBase;
import com.plugatar.xteps.base.XtepsException;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Thread hooks.
 */
public class ThreadHooks {

    /**
     * Utility class ctor.
     */
    private ThreadHooks() {
    }

    /**
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param hook the hook
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code hook} is null
     */
    public static void add(final ThrowingRunnable<?> hook) {
        XtepsBase.cached().threadHookInterval(); /* check Xteps configuration */
        if (hook == null) { throw new XtepsException("hook arg is null"); }
        Internal.addHook(Thread.currentThread(), hook);
    }

    private static final class Internal {
        private static final Map<Thread, Deque<ThrowingRunnable<?>>> HOOKS = new ConcurrentHashMap<>();
        private static final long INTERVAL_MILLIS = XtepsBase.cached().threadHookInterval();

        static {
            final Thread daemonThread = new Thread(() -> {
                long lastStart = System.currentTimeMillis();
                while (true) {
                    try {
                        final long currentMillis = System.currentTimeMillis();
                        sleep(lastStart, currentMillis);
                        lastStart = currentMillis;
                    } catch (final InterruptedException ignored) {
                    }
                    if (!HOOKS.isEmpty()) {
                        HOOKS.keySet().forEach(t -> {
                            if (!t.isAlive()) {
                                HOOKS.computeIfPresent(t, (thread, deque) -> {
                                    deque.descendingIterator().forEachRemaining(hook -> {
                                        try {
                                            hook.run();
                                        } catch (final Throwable ex) {
                                            ex.printStackTrace();
                                        }
                                    });
                                    return null;
                                });
                            }
                        });
                    }
                }
            }, "xteps-hook-daemon-thread");
            daemonThread.setDaemon(true);
            daemonThread.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
                HOOKS.values().forEach(queue -> queue.descendingIterator().forEachRemaining(hook -> {
                    try {
                        hook.run();
                    } catch (final Throwable ex) {
                        ex.printStackTrace();
                    }
                })),
                "xteps-shutdown-hook-thread"
            ));
        }

        private static void addHook(final Thread thread,
                                    final ThrowingRunnable<?> hook) {
            HOOKS.computeIfAbsent(thread, k -> new ConcurrentLinkedDeque<>()).add(hook);
        }

        private static void sleep(final long startMillis,
                                  final long currentMillis) throws InterruptedException {
            final long sleepMillis = INTERVAL_MILLIS - currentMillis + startMillis;
            if (sleepMillis > 0) {
                Thread.sleep(sleepMillis);
            }
        }
    }
}
