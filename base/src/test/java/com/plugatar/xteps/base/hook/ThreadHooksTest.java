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

import com.plugatar.xteps.base.HooksOrder;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsBase;
import com.plugatar.xteps.base.XtepsException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.plugatar.xteps.base.HookPriority.MAX_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.MIN_HOOK_PRIORITY;
import static com.plugatar.xteps.base.HookPriority.NORM_HOOK_PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link ThreadHooks}.
 */
final class ThreadHooksTest {

    @Test
    void nullArgExceptionForAddMethod() {
        assertThatCode(() -> ThreadHooks.addHook(NORM_HOOK_PRIORITY, null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void illegalArgExceptionForAddMethod() {
        assertThatCode(() -> ThreadHooks.addHook(MIN_HOOK_PRIORITY - 1, () -> { }))
            .isInstanceOf(XtepsException.class);
        assertThatCode(() -> ThreadHooks.addHook(MAX_HOOK_PRIORITY + 1, () -> { }))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void executionForFromLastOrder() throws Throwable {
        final List<String> executionLog = new ArrayList<>();
        final ThrowingRunnable<RuntimeException> hook1 = () -> executionLog.add("hook1");
        final ThrowingRunnable<RuntimeException> hook2 = () -> executionLog.add("hook2");
        final ThrowingRunnable<RuntimeException> hook3 = () -> executionLog.add("hook3");
        final ThrowingRunnable<RuntimeException> hook4 = () -> executionLog.add("hook4");
        final Thread thread = new Thread(() -> {
            ThreadHooks.setOrder(HooksOrder.FROM_LAST);
            ThreadHooks.addHook(MIN_HOOK_PRIORITY, hook1);
            ThreadHooks.addHook(NORM_HOOK_PRIORITY, hook2);
            ThreadHooks.addHook(MAX_HOOK_PRIORITY, hook3);
            ThreadHooks.addHook(NORM_HOOK_PRIORITY, hook4);
        });

        thread.start();
        Thread.sleep(XtepsBase.cached().threadHooksThreadInterval() + 1000);
        assertThat(executionLog).isEqualTo(Arrays.asList("hook3", "hook4", "hook2", "hook1"));
    }

    @Test
    void executionForFromFirstOrder() throws Throwable {
        final List<String> executionLog = new ArrayList<>();
        final ThrowingRunnable<RuntimeException> hook1 = () -> executionLog.add("hook1");
        final ThrowingRunnable<RuntimeException> hook2 = () -> executionLog.add("hook2");
        final ThrowingRunnable<RuntimeException> hook3 = () -> executionLog.add("hook3");
        final ThrowingRunnable<RuntimeException> hook4 = () -> executionLog.add("hook4");
        final Thread thread = new Thread(() -> {
            ThreadHooks.setOrder(HooksOrder.FROM_FIRST);
            ThreadHooks.addHook(MIN_HOOK_PRIORITY, hook1);
            ThreadHooks.addHook(NORM_HOOK_PRIORITY, hook2);
            ThreadHooks.addHook(MAX_HOOK_PRIORITY, hook3);
            ThreadHooks.addHook(NORM_HOOK_PRIORITY, hook4);
        });

        thread.start();
        Thread.sleep(XtepsBase.cached().threadHooksThreadInterval() + 1000);
        assertThat(executionLog).isEqualTo(Arrays.asList("hook3", "hook2", "hook4", "hook1"));
    }

    @Test
    void executionForException() throws Throwable {
        final List<String> executionLog = new ArrayList<>();
        final ThrowingRunnable<RuntimeException> hook1 = () -> executionLog.add("hook1");
        final ThrowingRunnable<RuntimeException> hook2 = () -> { throw new RuntimeException("exception hook2"); };
        final ThrowingRunnable<RuntimeException> hook3 = () -> executionLog.add("hook3");
        final ThrowingRunnable<RuntimeException> hook4 = () -> { throw new RuntimeException("exception hook4"); };
        final Thread thread = new Thread(() -> {
            ThreadHooks.setOrder(HooksOrder.FROM_FIRST);
            ThreadHooks.addHook(MIN_HOOK_PRIORITY, hook1);
            ThreadHooks.addHook(NORM_HOOK_PRIORITY, hook2);
            ThreadHooks.addHook(MAX_HOOK_PRIORITY, hook3);
            ThreadHooks.addHook(NORM_HOOK_PRIORITY, hook4);
        });

        thread.start();
        Thread.sleep(XtepsBase.cached().threadHooksThreadInterval() + 1000);
        assertThat(executionLog).isEqualTo(Arrays.asList("hook3", "hook1"));
    }
}
