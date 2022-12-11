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

import com.plugatar.xteps.base.HookPriority;
import com.plugatar.xteps.base.HooksOrder;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsBase;
import com.plugatar.xteps.base.XtepsException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.plugatar.xteps.base.HookPriority.MAX_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.MIN_HOOK_PRIORITY;

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
     * @param priority the priority
     * @param hook     the hook
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code hook} is null
     *                        or if {@code priority} is not in the range {@link HookPriority#MIN_HOOK_PRIORITY} to
     *                        {@link HookPriority#MAX_HOOK_PRIORITY}
     */
    public static void addHook(final int priority,
                               final ThrowingRunnable<?> hook) {
        XtepsBase.cached(); /* check Xteps configuration */
        if (hook == null) { throw new XtepsException("hook arg is null"); }
        if (priority < MIN_HOOK_PRIORITY || priority > MAX_HOOK_PRIORITY) {
            throw new XtepsException("priority arg not in the range " + MIN_HOOK_PRIORITY + " to " + MAX_HOOK_PRIORITY);
        }
        Internal.addHook(Thread.currentThread(), priority, hook);
    }

    /**
     * Sets given hooks order for the current thread.
     *
     * @param order the hooks order
     * @throws XtepsException if Xteps configuration is incorrect
     *                        or if {@code order} is null
     */
    public static void setOrder(final HooksOrder order) {
        XtepsBase.cached(); /* check Xteps configuration */
        if (order == null) { throw new XtepsException("order arg is null"); }
        Internal.setOrder(Thread.currentThread(), order);
    }

    private static final class Internal {
        private static final Map<Thread, ThreadItems> HOOKS;
        private static final long INTERVAL_MILLIS;
        private static final HooksOrder DEFAULT_HOOKS_ORDER;

        static {
            /* Field initialization */
            final XtepsBase xtepsBase = XtepsBase.cached();
            HOOKS = new ConcurrentHashMap<>();
            INTERVAL_MILLIS = xtepsBase.threadHooksThreadInterval();
            DEFAULT_HOOKS_ORDER = xtepsBase.defaultHooksOrder();
            /* Daemon thread */
            final Thread daemonThread = new Thread(() -> {
                long lastStartMillis;
                while (true) {
                    lastStartMillis = System.currentTimeMillis();
                    if (!HOOKS.isEmpty()) {
                        HOOKS.keySet().forEach(thread -> {
                            if (!thread.isAlive()) {
                                HOOKS.computeIfPresent(thread, (t, threadItems) -> {
                                    threadItems.callHooks();
                                    return null;
                                });
                            }
                        });
                    }
                    try {
                        final long startMillis = System.currentTimeMillis();
                        final long millisToSleep = INTERVAL_MILLIS - startMillis + lastStartMillis;
                        if (millisToSleep > 0) {
                            Thread.sleep(millisToSleep);
                        }
                    } catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "xteps-thread-hooks-daemon-thread");
            daemonThread.setDaemon(true);
            daemonThread.setPriority(XtepsBase.cached().threadHooksThreadPriority());
            daemonThread.start();
            /* Shutdown hook */
            Runtime.getRuntime().addShutdownHook(new Thread(
                () -> HOOKS.values().forEach(ThreadItems::callHooks),
                "xteps-shutdown-hook-thread"
            ));
        }

        private static void addHook(final Thread thread,
                                    final int priority,
                                    final ThrowingRunnable<?> hook) {
            HOOKS.computeIfAbsent(thread, t -> new ThreadItems(DEFAULT_HOOKS_ORDER))
                .hooksItems()
                .add(new HookItems(priority, hook));
        }

        private static void setOrder(final Thread thread,
                                     final HooksOrder order) {
            HOOKS.computeIfAbsent(thread, t -> new ThreadItems(DEFAULT_HOOKS_ORDER))
                .setOrder(order);
        }
    }

    private static final class ThreadItems {
        private volatile HooksOrder order;
        private final Queue<HookItems> hookItemsQueue;

        private ThreadItems(final HooksOrder order) {
            this.order = order;
            this.hookItemsQueue = new ConcurrentLinkedQueue<>();
        }

        private Queue<HookItems> hooksItems() {
            return this.hookItemsQueue;
        }

        private void setOrder(final HooksOrder order) {
            this.order = order;
        }

        private void callHooks() {
            final List<HookItems> hooksItemsList = Arrays.asList(
                this.hookItemsQueue.toArray(new HookItems[this.hookItemsQueue.size()])
            );
            switch (this.order) {
                case FROM_FIRST:
                    break;
                case FROM_LAST:
                    Collections.reverse(hooksItemsList);
                    break;
                default:
                    throw new Error("Impossible");
            }
            Collections.sort(hooksItemsList);
            for (final HookItems hookItems : hooksItemsList) {
                try {
                    hookItems.hook().run();
                } catch (final Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static final class HookItems implements Comparable<HookItems> {
        private final int priority;
        private final ThrowingRunnable<?> hook;

        private HookItems(final int priority,
                          final ThrowingRunnable<?> hook) {
            this.priority = priority;
            this.hook = hook;
        }

        private ThrowingRunnable<?> hook() {
            return this.hook;
        }

        @Override
        public int compareTo(final HookItems another) {
            return Integer.compare(another.priority, this.priority);
        }
    }
}
