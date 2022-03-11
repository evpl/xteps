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
package com.plugatar.xteps;

import com.plugatar.xteps.core.NoCtxSteps;
import com.plugatar.xteps.core.StepListener;
import com.plugatar.xteps.core.StepNameFormatter;
import com.plugatar.xteps.core.StepWriter;
import com.plugatar.xteps.core.formatter.DefaultStepNameFormatter;
import com.plugatar.xteps.core.listener.ComplexStepListener;
import com.plugatar.xteps.core.steps.NoCtxStepsOf;
import com.plugatar.xteps.core.writer.DefaultStepWriter;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link StepsProvider}.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
final class StepsProviderTest {

    @BeforeAll
    static void beforeAll() {
        StepsProviderTest.clearProperties();
    }

    @AfterEach
    void afterEach() {
        StepsProviderTest.clearProperties();
    }

    private static void clearProperties() {
        System.clearProperty("xteps.enabled");
        System.clearProperty("xteps.replacementPattern");
        System.clearProperty("xteps.fieldForceAccess");
        System.clearProperty("xteps.methodForceAccess");
        System.clearProperty("xteps.useSPIListeners");
        System.clearProperty("xteps.listeners");
    }

    @Test
    void classIsFinal() {
        assertThat(StepsProvider.class).isFinal();
    }

    @Test
    void singlePrivateCtor() {
        assertThat(StepsProvider.class.getDeclaredConstructors())
            .singleElement()
            .is(new Condition<>(
                ctor -> Modifier.isPrivate(ctor.getModifiers()),
                "private"
            ));
    }

    private static Field accessibleField(final Field field) {
        field.setAccessible(true);
        return field;
    }

    @SafeVarargs
    private static void checkStateReflective(final Class<? extends StepListener>... expectedListeners) throws Exception {
        checkStateReflective(false, false, expectedListeners);
    }

    @SafeVarargs
    private static void checkStateReflective(final String pattern,
                                             final Class<? extends StepListener>... expectedListeners) throws Exception {
        checkStateReflective(pattern, false, false, expectedListeners);
    }

    @SafeVarargs
    private static void checkStateReflective(final boolean expectedFieldForceAccess,
                                             final boolean expectedMethodForceAccess,
                                             final Class<? extends StepListener>... expectedListeners) throws Exception {
        checkStateReflective("\\{([^}]*)}", expectedFieldForceAccess, expectedMethodForceAccess, expectedListeners);
    }

    @SafeVarargs
    private static void checkStateReflective(final String expectedPattern,
                                             final boolean expectedFieldForceAccess,
                                             final boolean expectedMethodForceAccess,
                                             final Class<? extends StepListener>... expectedListeners) throws Exception {
        final Class<?> expectedNoCtxStepsCls = NoCtxStepsOf.class;
        final Class<?> expectedStepNameFormatterCls = DefaultStepNameFormatter.class;
        final Class<?> expectedStepWriterCls = DefaultStepWriter.class;

        final NoCtxSteps steps = StepsProvider.stepsByConfig();
        assertThat(steps).isInstanceOf(expectedNoCtxStepsCls);

        final StepNameFormatter sf =
            (StepNameFormatter) accessibleField(expectedNoCtxStepsCls.getDeclaredField("sf")).get(steps);
        assertThat(sf).isExactlyInstanceOf(expectedStepNameFormatterCls);
        assertThat(
            ((Pattern) accessibleField(expectedStepNameFormatterCls.getDeclaredField("replacementPattern")).get(sf)).pattern()
        ).isEqualTo(expectedPattern);
        assertThat(
            accessibleField(expectedStepNameFormatterCls.getDeclaredField("fieldForceAccess")).get(sf)
        ).isEqualTo(expectedFieldForceAccess);
        assertThat(
            accessibleField(expectedStepNameFormatterCls.getDeclaredField("methodForceAccess")).get(sf)
        ).isEqualTo(expectedMethodForceAccess);

        final StepWriter sw = (StepWriter) accessibleField(expectedNoCtxStepsCls.getDeclaredField("sw")).get(steps);
        assertThat(sw).isExactlyInstanceOf(expectedStepWriterCls);
        final StepListener stepListener =
            (StepListener) accessibleField(expectedStepWriterCls.getDeclaredField("listener")).get(sw);
        if (expectedListeners.length == 0) {
            assertThat(stepListener.getClass().getTypeName())
                .isEqualTo("com.plugatar.xteps.StepsProvider$FakeStepListener");
        } else if (expectedListeners.length == 1) {
            assertThat(stepListener).isExactlyInstanceOf(expectedListeners[0]);
        } else {
            final Class<?> expectedStepListener = ComplexStepListener.class;
            assertThat(stepListener.getClass()).isSameAs(expectedStepListener);
            final List<Class<?>> listeners = Arrays.stream(
                (StepListener[]) accessibleField(expectedStepListener.getDeclaredField("listeners")).get(stepListener)
            ).map(StepListener::getClass).collect(Collectors.toList());
            assertThat(listeners).containsExactly(expectedListeners);
        }
    }

    @Test
    void enablePropertyCorrectValueTrue() throws Exception {
        System.setProperty("xteps.enabled", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(LoggedStepListener.class);
    }

    @Test
    void enablePropertyCorrectValueFalse() throws Exception {
        System.setProperty("xteps.enabled", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective();
    }

    @Test
    void enablePropertyDefaultValue() throws Exception {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(LoggedStepListener.class);
    }

    @Test
    void enablePropertyIncorrectValue() {
        System.setProperty("xteps.enabled", "value");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void patterPropertyCorrectValue1Group() throws Exception {
        final String pattern = "left([^right]*)right";
        System.setProperty("xteps.replacementPattern", pattern);
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(pattern, LoggedStepListener.class);
    }

    @Test
    void patterPropertyCorrectValue2Groups() throws Exception {
        final String pattern = "left([^right]*)right(abc|def)";
        System.setProperty("xteps.replacementPattern", pattern);
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(pattern, LoggedStepListener.class);
    }

    @Test
    void patterPropertyDefaultValue() throws Exception {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(LoggedStepListener.class);
    }

    @Test
    void patterPropertyIncorrectValue0Groups() {
        final String pattern = "left right";
        System.setProperty("xteps.replacementPattern", pattern);
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void fieldForceAccessPropertyCorrectValueTrue() throws Exception {
        System.setProperty("xteps.fieldForceAccess", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(true, false, LoggedStepListener.class);
    }

    @Test
    void fieldForceAccessPropertyCorrectValueFalse() throws Exception {
        System.setProperty("xteps.fieldForceAccess", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(false, false, LoggedStepListener.class);
    }

    @Test
    void fieldForceAccessPropertyDefaultValue() throws Exception {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(false, false, LoggedStepListener.class);
    }

    @Test
    void fieldForceAccessPropertyIncorrectValue() {
        System.setProperty("xteps.fieldForceAccess", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void methodForceAccessPropertyCorrectValueTrue() throws Exception {
        System.setProperty("xteps.methodForceAccess", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(false, true, LoggedStepListener.class);
    }

    @Test
    void methodForceAccessPropertyCorrectValueFalse() throws Exception {
        System.setProperty("xteps.methodForceAccess", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(false, false, LoggedStepListener.class);
    }

    @Test
    void methodForceAccessPropertyDefaultValue() throws Exception {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(false, false, LoggedStepListener.class);
    }

    @Test
    void methodForceAccessPropertyIncorrectValue() {
        System.setProperty("xteps.methodForceAccess", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void useSPIListenersPropertyCorrectValueTrue() {
        System.setProperty("xteps.useSPIListeners", "true");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .doesNotThrowAnyException();
    }

    @Test
    void useSPIListenersPropertyCorrectValueFalse() {
        System.setProperty("xteps.useSPIListeners", "false");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .doesNotThrowAnyException();
    }

    @Test
    void useSPIListenersPropertyIncorrectValue() {
        System.setProperty("xteps.useSPIListeners", "value");
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void listenersPropertyCorrectValue1Listener() throws Exception {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$LoggedStepListener");

        checkStateReflective(LoggedStepListener.class);
    }

    @Test
    void listenersPropertyCorrectValue2Listeners() throws Exception {
        System.setProperty(
            "xteps.listeners",
            "com.plugatar.xteps.StepsProviderTest$LoggedStepListener," +
                "com.plugatar.xteps.StepsProviderTest$LoggedStepListener2"
        );

        checkStateReflective(LoggedStepListener.class, LoggedStepListener2.class);
    }

    @Test
    void listenersPropertyDefaultValue() {
        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void listenersPropertyIncorrectValue0Listeners() {
        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void listenersPropertyIncorrectValueNotListener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$NotListener");
        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    @Test
    void listenersPropertyIncorrectValueNonExistentListener() {
        System.setProperty("xteps.listeners", "com.plugatar.xteps.StepsProviderTest$NonExistent");
        assertThatCode(() -> StepsProvider.stepsByConfig())
            .isInstanceOf(Xteps.ConfigException.class);
    }

    static final class LoggedStepListener implements StepListener {
        private static String lastStepName = null;

        public LoggedStepListener() {
        }

        static String lastStepName() {
            return LoggedStepListener.lastStepName;
        }

        @Override
        public void stepStarted(final String uuid,
                                final String stepName) {
            LoggedStepListener.lastStepName = stepName;
        }

        @Override
        public void stepPassed(final String uuid,
                               final String stepName) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final String stepName,
                               final Throwable throwable) {
        }
    }

    static final class LoggedStepListener2 implements StepListener {
        private static String lastStepName = null;

        public LoggedStepListener2() {
        }

        static String lastStepName() {
            return LoggedStepListener2.lastStepName;
        }

        @Override
        public void stepStarted(final String uuid,
                                final String stepName) {
            LoggedStepListener2.lastStepName = stepName;
        }

        @Override
        public void stepPassed(final String uuid,
                               final String stepName) {
        }

        @Override
        public void stepFailed(final String uuid,
                               final String stepName,
                               final Throwable throwable) {
        }
    }

    static final class NotListener {

        public NotListener() {
        }
    }
}
