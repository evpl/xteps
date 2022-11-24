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
package com.plugatar.xteps.base.hook.container;

import com.plugatar.xteps.base.HookContainer;
import com.plugatar.xteps.base.ThrowingRunnable;
import com.plugatar.xteps.base.XtepsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link DefaultHookContainer}.
 */
final class DefaultHookContainerTest {

    @Test
    void nullArgExceptionForAddMethod() {
        final HookContainer container = new DefaultHookContainer();

        assertThatCode(() -> container.add((ThrowingRunnable<?>) null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void nullArgExceptionForCallHooksMethod() {
        final HookContainer container = new DefaultHookContainer();

        assertThatCode(() -> container.callHooks((Throwable) null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void callHooksMethod() {
        final HookContainer container = new DefaultHookContainer();
        final Exception exception1 = new Exception("exception 1");
        final ThrowingRunnable<?> hook1 = () -> { throw exception1; };
        final Exception exception2 = new Exception("exception 2");
        final ThrowingRunnable<?> hook2 = () -> { throw exception2; };
        container.add(hook1);
        container.add(hook2);

        assertThatCode(() -> container.callHooks())
            .isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void callHooksMethodWithBaseException() {
        final HookContainer container = new DefaultHookContainer();
        final Exception exception1 = new Exception("exception 1");
        final ThrowingRunnable<?> hook1 = () -> { throw exception1; };
        final Exception exception2 = new Exception("exception 2");
        final ThrowingRunnable<?> hook2 = () -> { throw exception2; };
        final RuntimeException baseException = new RuntimeException();
        container.add(hook1);
        container.add(hook2);

        container.callHooks(baseException);
        assertThat(baseException)
            .isInstanceOf(RuntimeException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }
}
