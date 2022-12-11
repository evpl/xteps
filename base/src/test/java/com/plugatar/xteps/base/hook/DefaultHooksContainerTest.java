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
 * Tests for {@link DefaultHooksContainer}.
 */
final class DefaultHooksContainerTest {

    @Test
    void nullArgExceptionForCtor() {
        assertThatCode(() -> new DefaultHooksContainer((HooksOrder) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullArgExceptionForAddMethod() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_FIRST);

        assertThatCode(() -> container.addHook(NORM_HOOK_PRIORITY, (ThrowingRunnable<?>) null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void illegalArgExceptionForAddMethod() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_FIRST);

        assertThatCode(() -> container.addHook(MIN_HOOK_PRIORITY - 1, () -> { }))
            .isInstanceOf(XtepsException.class);
        assertThatCode(() -> container.addHook(MAX_HOOK_PRIORITY + 1, () -> { }))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void nullArgExceptionForCallHooksMethod() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_FIRST);

        assertThatCode(() -> container.callHooks((Throwable) null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void callHooksMethodForFromLastOrder() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_LAST);
        final List<String> executionLog = new ArrayList<>();
        final ThrowingRunnable<RuntimeException> hook1 = () -> executionLog.add("hook1");
        final ThrowingRunnable<RuntimeException> hook2 = () -> executionLog.add("hook2");
        final ThrowingRunnable<RuntimeException> hook3 = () -> executionLog.add("hook3");
        final ThrowingRunnable<RuntimeException> hook4 = () -> executionLog.add("hook4");
        container.addHook(MIN_HOOK_PRIORITY, hook1);
        container.addHook(NORM_HOOK_PRIORITY, hook2);
        container.addHook(MAX_HOOK_PRIORITY, hook3);
        container.addHook(NORM_HOOK_PRIORITY, hook4);

        container.callHooks();
        assertThat(executionLog).isEqualTo(Arrays.asList("hook3", "hook4", "hook2", "hook1"));
    }

    @Test
    void callHooksMethodForFromFirstOrder() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_FIRST);
        final List<String> executionLog = new ArrayList<>();
        final ThrowingRunnable<RuntimeException> hook1 = () -> executionLog.add("hook1");
        final ThrowingRunnable<RuntimeException> hook2 = () -> executionLog.add("hook2");
        final ThrowingRunnable<RuntimeException> hook3 = () -> executionLog.add("hook3");
        final ThrowingRunnable<RuntimeException> hook4 = () -> executionLog.add("hook4");
        container.addHook(MIN_HOOK_PRIORITY, hook1);
        container.addHook(NORM_HOOK_PRIORITY, hook2);
        container.addHook(MAX_HOOK_PRIORITY, hook3);
        container.addHook(NORM_HOOK_PRIORITY, hook4);

        container.callHooks();
        assertThat(executionLog).isEqualTo(Arrays.asList("hook3", "hook2", "hook4", "hook1"));
    }

    @Test
    void callHooksMethodForException() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_LAST);
        final Exception exception1 = new Exception("exception 1");
        final ThrowingRunnable<?> hook1 = () -> { throw exception1; };
        final Exception exception2 = new Exception("exception 2");
        final ThrowingRunnable<?> hook2 = () -> { throw exception2; };
        container.addHook(NORM_HOOK_PRIORITY, hook1);
        container.addHook(NORM_HOOK_PRIORITY, hook2);

        assertThatCode(() -> container.callHooks())
            .isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void callHooksMethodWithBaseExceptionForException() {
        final HooksContainer container = new DefaultHooksContainer(HooksOrder.FROM_LAST);
        final Exception exception1 = new Exception("exception 1");
        final ThrowingRunnable<?> hook1 = () -> { throw exception1; };
        final Exception exception2 = new Exception("exception 2");
        final ThrowingRunnable<?> hook2 = () -> { throw exception2; };
        final RuntimeException baseException = new RuntimeException();
        container.addHook(NORM_HOOK_PRIORITY, hook1);
        container.addHook(NORM_HOOK_PRIORITY, hook2);

        container.callHooks(baseException);
        assertThat(baseException)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }
}
