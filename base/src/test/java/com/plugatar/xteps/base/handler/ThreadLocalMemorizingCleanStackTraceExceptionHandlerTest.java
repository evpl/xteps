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
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link ThreadLocalMemorizingCleanStackTraceExceptionHandler}.
 */
final class ThreadLocalMemorizingCleanStackTraceExceptionHandlerTest {

    @Test
    void handleMethodThrowsExceptionForNullArg() {
        final ExceptionHandler handler = new ThreadLocalMemorizingCleanStackTraceExceptionHandler();

        assertThatCode(() -> handler.handle((Throwable) null))
            .isInstanceOf(XtepsException.class);
    }

    @Test
    void handleMethod() {
        final ExceptionHandler handler = new ThreadLocalMemorizingCleanStackTraceExceptionHandler();
        final Throwable baseException = new Throwable("base exception");
        final Throwable causeException = new Throwable("cause exception");
        final Throwable suppressedException = new Throwable("suppressed exception");
        final Throwable suppressedCauseException = new Throwable("suppressed cause throwable");
        suppressedException.initCause(suppressedCauseException);
        baseException.initCause(causeException);
        baseException.addSuppressed(suppressedException);

        handler.handle(baseException);
        assertThat(baseException).isNot(containsXtepsStackTrace());
        assertThat(causeException).isNot(containsXtepsStackTrace());
        assertThat(suppressedException).isNot(containsXtepsStackTrace());
        assertThat(suppressedCauseException).isNot(containsXtepsStackTrace());
    }

    private static Condition<Throwable> containsXtepsStackTrace() {
        return new Condition<>(
            t -> Arrays.stream(t.getStackTrace()).anyMatch(el -> el.getClassName().startsWith("com.plugatar.xteps")),
            "contains Xteps stack trace"
        );
    }
}
