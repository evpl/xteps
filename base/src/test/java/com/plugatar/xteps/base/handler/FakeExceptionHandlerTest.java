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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link FakeExceptionHandler}.
 */
final class FakeExceptionHandlerTest {

    @Test
    void handleMethodThrowsExceptionForNullArg() {
        final ExceptionHandler handler = new FakeExceptionHandler();

        assertThatCode(() -> handler.handle((Throwable) null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void handleMethod() {
        final ExceptionHandler handler = new FakeExceptionHandler();
        final Throwable exception = new Throwable();
        final StackTraceElement[] stackTrace = exception.getStackTrace();

        handler.handle(exception);
        assertThat(exception)
            .hasNoCause()
            .hasNoSuppressedExceptions();
        assertThat(exception.getStackTrace()).isEqualTo(stackTrace);
    }
}
