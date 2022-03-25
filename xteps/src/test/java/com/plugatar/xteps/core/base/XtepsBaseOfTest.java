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
package com.plugatar.xteps.core.base;

import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.StepNameFormatter;
import com.plugatar.xteps.core.StepWriter;
import com.plugatar.xteps.core.XtepsBase;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.util.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link XtepsBaseOf}.
 */
final class XtepsBaseOfTest {

    @Test
    void classIsNotFinal() {
        assertThat(XtepsBaseOf.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = XtepsBaseOf.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullConfigArg() {
        final Map<String, String> config = null;
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        assertThatCode(() -> new XtepsBaseOf(config, sf, sw))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullSfArg() {
        final Map<String, String> config = new HashMap<>();
        final StepNameFormatter sf = null;
        final StepWriter sw = mock(StepWriter.class);
        assertThatCode(() -> new XtepsBaseOf(config, sf, sw))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionForNullSwArg() {
        final Map<String, String> config = new HashMap<>();
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = null;
        assertThatCode(() -> new XtepsBaseOf(config, sf, sw))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void configMethod() {
        final Map<String, String> config = new HashMap<>();
        config.put("arg1", "1");
        config.put("arg2", "value");
        final XtepsBaseOf xtepsBase = new XtepsBaseOf(config, mock(StepNameFormatter.class), mock(StepWriter.class));

        final Map<String, String> result = xtepsBase.config();
        assertThat(result).containsOnly(entry("arg1", "1"), entry("arg2", "value"));
    }

    @Test
    void stepNameFormatterMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final XtepsBaseOf xtepsBase = new XtepsBaseOf(new HashMap<>(), sf, mock(StepWriter.class));

        assertThat(xtepsBase.stepNameFormatter()).isSameAs(sf);
    }

    @Test
    void stepWriterMethod() {
        final StepWriter sw = mock(StepWriter.class);
        final XtepsBaseOf xtepsBase = new XtepsBaseOf(new HashMap<>(), mock(StepNameFormatter.class), sw);
        assertThat(xtepsBase.stepWriter()).isSameAs(sw);
    }

    @Test
    void stepsMethod() {
        final StepNameFormatter sf = mock(StepNameFormatter.class);
        final StepWriter sw = mock(StepWriter.class);
        final XtepsBase xtepsBase = new XtepsBaseOf(new HashMap<>(), sf, sw);
        final String sfResult = "sf result";
        final String stepName = "step name";
        final ThrowingRunnable<RuntimeException> step = () -> {};
        when(sf.format(any(), any())).thenReturn(sfResult);

        final NoCtxSteps result = xtepsBase.steps();
        result.step(stepName, step);
        verify(sf).format(refEq(stepName), refEq(result));
        verify(sw).writeRunnableStep(refEq(sfResult), refEq(step));
    }
}
