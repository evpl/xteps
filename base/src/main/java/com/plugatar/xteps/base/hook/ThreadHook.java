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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Thread hook.
 */
public class ThreadHook {

    /**
     * Utility class ctor.
     */
    private ThreadHook() {
    }

    /**
     * Adds given hook for the current thread. This hook will be called after current
     * thread is finished.
     *
     * @param hook the hook
     * @throws XtepsException if {@code hook} is null
     *                        or if Xteps configuration is incorrect
     */
    public static void add(final ThrowingRunnable<?> hook) {
        if (hook == null) { throw new XtepsException("hook arg is null"); }
        XtepsBase.cached().threadHookInterval(); /* check Xteps configuration before Inner class static block init */
        Inner.addHook(Thread.currentThread(), hook);
    }

    private static final class Inner {
        private static final Map<Thread, Deque<ThrowingRunnable<?>>> HOOKS = new ConcurrentHashMap<>();
        private static final long INTERVAL_MS = XtepsBase.cached().threadHookInterval();

        static {
            final Thread daemonThread = new Thread(() -> {
                long lastStart = System.currentTimeMillis();
                while (true) {
                    try {
                        final long currentMillis = System.currentTimeMillis();
                        sleep(lastStart, currentMillis);
                        lastStart = currentMillis;
                    } catch (final InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (!HOOKS.isEmpty()) {
                        final Set<Thread> aliveThreads = Thread.getAllStackTraces().keySet();
                        for (final Thread hookThread : HOOKS.keySet()) {
                            if (!aliveThreads.contains(hookThread)) {
                                HOOKS.computeIfPresent(hookThread, (thread, deque) -> {
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
                        }
                    }
                }
            }, "Xteps-hook-daemon-thread");
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
                "Xteps-shutdown-hook-thread"
            ));
        }

        private static void addHook(final Thread thread,
                                    final ThrowingRunnable<?> hook) {
            HOOKS.computeIfAbsent(thread, k -> new ConcurrentLinkedDeque<>()).add(hook);
        }

        private static void sleep(final long startMillis,
                                  final long currentMillis) throws InterruptedException {
            final long result = INTERVAL_MS - (currentMillis - startMillis);
            if (result > 0) {
                Thread.sleep(result);
            }
        }
    }
}
