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

import com.plugatar.xteps.base.HooksContainer;
import com.plugatar.xteps.base.HooksOrder;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.plugatar.xteps.base.HookPriority.MAX_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.MIN_HOOK_PRIORITY;

/**
 * Default HooksContainer.
 */
public class DefaultHooksContainer implements HooksContainer {
    private volatile HooksOrder order;
    private final Queue<HookItems> hookItemsQueue;

    /**
     * Ctor.
     *
     * @param order the hooks order
     */
    public DefaultHooksContainer(final HooksOrder order) {
        if (order == null) { throw new NullPointerException("order arg is null"); }
        this.order = order;
        this.hookItemsQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public final void addHook(final int priority,
                              final ThrowingRunnable<?> hook) {
        if (hook == null) { throwNullArgException("hook"); }
        if (priority < MIN_HOOK_PRIORITY || priority > MAX_HOOK_PRIORITY) {
            throw new XtepsException("priority arg not in the range " + MIN_HOOK_PRIORITY + " to " + MAX_HOOK_PRIORITY);
        }
        this.hookItemsQueue.add(new HookItems(priority, hook));
    }

    @Override
    public final void setOrder(final HooksOrder order) {
        if (order == null) { throwNullArgException("order"); }
        this.order = order;
    }

    @Override
    public final void callHooks() {
        if (!this.hookItemsQueue.isEmpty()) {
            final List<Throwable> exceptions = new ArrayList<>();
            for (final HookItems hookItems : this.orderedHooks()) {
                try {
                    hookItems.hook().run();
                } catch (final Throwable ex) {
                    exceptions.add(ex);
                }
            }
            if (!exceptions.isEmpty()) {
                final XtepsException baseEx = new XtepsException(
                    "One or more hooks threw exceptions (see suppressed exceptions)");
                exceptions.forEach(baseEx::addSuppressed);
                throw baseEx;
            }
        }
    }

    @Override
    public final void callHooks(final Throwable baseException) {
        if (baseException == null) { throwNullArgException("baseException"); }
        if (!this.hookItemsQueue.isEmpty()) {
            for (final HookItems hookItems : this.orderedHooks()) {
                try {
                    hookItems.hook().run();
                } catch (final Throwable ex) {
                    baseException.addSuppressed(ex);
                }
            }
        }
    }

    private List<HookItems> orderedHooks() {
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
        return hooksItemsList;
    }

    private static void throwNullArgException(final String argName) {
        throw new XtepsException(argName + " arg is null");
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
