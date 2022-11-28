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
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link ThreadHooks}.
 */
final class ThreadHookTest {

    @Test
    void nullArgExceptionForAddMethod() {
        assertThatCode(() -> ThreadHooks.add(null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void addMethod() throws Throwable {
        System.setProperty("xteps.threadHookInterval", "-100");
        assertThatCode(() -> ThreadHooks.add(() -> { }))
            .isInstanceOf(XtepsException.class);
        System.clearProperty("xteps.threadHookInterval");

        final AtomicBoolean isHook1Executed = new AtomicBoolean();
        final ThrowingRunnable<RuntimeException> hook1 = () -> isHook1Executed.set(true);
        final Thread thread1 = new Thread(() -> ThreadHooks.add(hook1));
        final AtomicBoolean isHook2Executed = new AtomicBoolean();
        final ThrowingRunnable<RuntimeException> hook2 = () -> isHook2Executed.set(true);
        final Thread thread2 = new Thread(() -> ThreadHooks.add(hook2));

        assertThat(isHook1Executed).isFalse();
        assertThat(isHook2Executed).isFalse();
        thread1.start();
        thread2.start();
        Thread.sleep(XtepsBase.cached().threadHookInterval() + 1000);
        assertThat(isHook1Executed).isTrue();
        assertThat(isHook2Executed).isTrue();
    }
}
