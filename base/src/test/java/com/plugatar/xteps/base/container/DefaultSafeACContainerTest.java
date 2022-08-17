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
package com.plugatar.xteps.base.container;

import com.plugatar.xteps.base.CloseException;
import com.plugatar.xteps.base.SafeACContainer;
import com.plugatar.xteps.base.XtepsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DefaultSafeACContainer}.
 */
final class DefaultSafeACContainerTest {

    @Test
    void nullArgExceptionForAddMethod() throws Exception {
        final SafeACContainer container = new DefaultSafeACContainer();
        final AutoCloseable autoCloseable1 = mock(AutoCloseable.class);
        final Exception exception1 = new Exception("exception 1");
        doThrow(exception1).when(autoCloseable1).close();
        container.add(autoCloseable1);
        final AutoCloseable autoCloseable2 = mock(AutoCloseable.class);
        final Exception exception2 = new Exception("exception 2");
        doThrow(exception2).when(autoCloseable2).close();
        container.add(autoCloseable2);

        assertThatCode(() -> container.add((AutoCloseable) null))
            .isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void nullArgExceptionForCloseMethod() throws Exception {
        final SafeACContainer container = new DefaultSafeACContainer();
        final AutoCloseable autoCloseable1 = mock(AutoCloseable.class);
        final Exception exception1 = new Exception("exception 1");
        doThrow(exception1).when(autoCloseable1).close();
        container.add(autoCloseable1);
        final AutoCloseable autoCloseable2 = mock(AutoCloseable.class);
        final Exception exception2 = new Exception("exception 2");
        doThrow(exception2).when(autoCloseable2).close();
        container.add(autoCloseable2);

        assertThatCode(() -> container.add((AutoCloseable) null))
            .isInstanceOf(XtepsException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void closeAutoCloseables() throws Exception {
        final SafeACContainer container = new DefaultSafeACContainer();
        final AutoCloseable autoCloseable1 = mock(AutoCloseable.class);
        final Exception exception1 = new Exception("exception 1");
        doThrow(exception1).when(autoCloseable1).close();
        container.add(autoCloseable1);
        final AutoCloseable autoCloseable2 = mock(AutoCloseable.class);
        final Exception exception2 = new Exception("exception 2");
        doThrow(exception2).when(autoCloseable2).close();
        container.add(autoCloseable2);

        assertThatCode(() -> container.close())
            .isInstanceOf(CloseException.class)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }

    @Test
    void closeAutoCloseablesWithBaseException() throws Exception {
        final Throwable baseException = new Throwable();
        final SafeACContainer container = new DefaultSafeACContainer();
        final AutoCloseable autoCloseable1 = mock(AutoCloseable.class);
        final Exception exception1 = new Exception("exception 1");
        doThrow(exception1).when(autoCloseable1).close();
        container.add(autoCloseable1);
        final AutoCloseable autoCloseable2 = mock(AutoCloseable.class);
        final Exception exception2 = new Exception("exception 2");
        doThrow(exception2).when(autoCloseable2).close();
        container.add(autoCloseable2);

        assertThatCode(() -> container.close(baseException))
            .doesNotThrowAnyException();
        assertThat(baseException)
            .hasSuppressedException(exception1)
            .hasSuppressedException(exception2);
    }
}
