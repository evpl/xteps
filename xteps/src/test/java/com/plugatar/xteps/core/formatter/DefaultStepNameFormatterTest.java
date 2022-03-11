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
package com.plugatar.xteps.core.formatter;

import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepNameFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DefaultStepNameFormatter}.
 */
final class DefaultStepNameFormatterTest {

    @Test
    void classIsNotFinal() {
        assertThat(DefaultStepNameFormatter.class).isNotFinal();
    }

    @Test
    void allDeclaredPublicMethodsAreFinal() {
        final Class<?> cls = DefaultStepNameFormatter.class;
        assertThat(cls.getMethods())
            .filteredOn(method -> method.getDeclaringClass() == cls)
            .allMatch(method -> Modifier.isFinal(method.getModifiers()));
    }

    @Test
    void ctorThrowsExceptionForNullPattern() {
        assertThatCode(() -> new DefaultStepNameFormatter(null, false, false))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorThrowsExceptionFor0GroupsPattern() {
        final Pattern pattern = Pattern.compile("[a-z]+");
        assertThatCode(() -> new DefaultStepNameFormatter(pattern, false, false))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void ctorDoesNotThrowExceptionFor1GroupsPattern() {
        final Pattern pattern = Pattern.compile("([a-z]+)");
        assertThatCode(() -> new DefaultStepNameFormatter(pattern, false, false))
            .doesNotThrowAnyException();
    }

    @Test
    void ctorDoesNotThrowExceptionFor2GroupsPattern() {
        final Pattern pattern = Pattern.compile("([a-z]+)([a-z]+)");
        assertThatCode(() -> new DefaultStepNameFormatter(pattern, false, false))
            .doesNotThrowAnyException();
    }

    @Test
    void formatMethodThrowsExceptionForNullStepName() {
        final DefaultStepNameFormatter sf = new DefaultStepNameFormatter(defaultPattern(), false, false);
        assertThatCode(() -> sf.format(null, HashMap::new))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void formatMethodThrowsExceptionForEmptyStepName() {
        final DefaultStepNameFormatter sf = new DefaultStepNameFormatter(defaultPattern(), false, false);
        assertThatCode(() -> sf.format("", HashMap::new))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void formatMethodThrowsExceptionForNullReplacementsSupplier() {
        final DefaultStepNameFormatter sf = new DefaultStepNameFormatter(defaultPattern(), false, false);
        assertThatCode(() -> sf.format("step name", null))
            .isInstanceOf(ArgumentException.class);
    }

    @Test
    void formatMethodConvertObjectToString() {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "{obj}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj", 111);
                    return replacements;
                }
            )
        ).isEqualTo("111");
    }

    @Test
    void formatMethodConvertNullToString() {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "{obj}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj", null);
                    return replacements;
                }
            )
        ).isEqualTo("null");
    }

    @Test
    void formatMethodConvertObjectToStringWithPrefix() {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "prefix {obj}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj", 111);
                    return replacements;
                }
            )
        ).isEqualTo("prefix 111");
    }

    @Test
    void formatMethodConvertObjectToStringWithPostfix() {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "{obj} postfix",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj", 111);
                    return replacements;
                }
            )
        ).isEqualTo("111 postfix");
    }

    @Test
    void formatMethodConvertObjectsToString() {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "{obj1} {obj1} {obj2} {obj2} {obj1} {obj2}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj1", 111);
                    replacements.put("obj2", 222);
                    return replacements;
                }
            )
        ).isEqualTo("111 111 222 222 111 222");
    }

    @Test
    void formatMethodWorksCorrectlyForUnusedReplacementPointers() {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "{obj1} {obj1} {obj2} {obj2} {obj1} {obj2}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("fake1", 111);
                    replacements.put("fake2", 222);
                    return replacements;
                }
            )
        ).isEqualTo("{obj1} {obj1} {obj2} {obj2} {obj1} {obj2}");
    }

    @Test
    void formatMethodWrapObjectToStringMethodException() {
        final DefaultStepNameFormatter sf = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final RuntimeException exception = new RuntimeException();
        final Object obj = mock(Object.class);
        doThrow(exception).when(obj).toString();
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", obj);
        assertThatCode(() -> sf.format("{obj}", () -> replacements))
            .isInstanceOf(StepNameFormatException.class)
            .hasCause(exception);
    }

    @Test
    void formatMethodDoesNotCallObjectToStringMethodWithoutReplacementPointer() {
        final DefaultStepNameFormatter sf = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Object obj = mock(Object.class);
        doThrow(new RuntimeException()).when(obj).toString();
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("fake", obj);
        assertThatCode(() -> sf.format("{obj}", () -> replacements))
            .doesNotThrowAnyException();
    }

    private static Stream<Arguments> arrayParams() {
        final Object[] complexArray = new Object[3];
        complexArray[0] = new Integer[]{30, 50, 100};
        complexArray[1] = new int[]{40, 60};
        complexArray[2] = complexArray;
        return Stream.of(
            Arguments.of(new byte[0], "[]", new byte[]{30, 50, 100}, "[30, 50, 100]"),
            Arguments.of(new short[0], "[]", new short[]{30, 50, 100}, "[30, 50, 100]"),
            Arguments.of(new int[0], "[]", new int[]{30, 50, 100}, "[30, 50, 100]"),
            Arguments.of(new long[0], "[]", new long[]{30L, 50L, 100L}, "[30, 50, 100]"),
            Arguments.of(new char[0], "[]", new char[]{'a', 'b', 'c'}, "[a, b, c]"),
            Arguments.of(new float[0], "[]", new float[]{30.0f, 50.0f, 100.0f}, "[30.0, 50.0, 100.0]"),
            Arguments.of(new double[0], "[]", new double[]{30.0, 50.0, 100.0}, "[30.0, 50.0, 100.0]"),
            Arguments.of(new boolean[0], "[]", new boolean[]{true, false, true}, "[true, false, true]"),
            Arguments.of(new Object[0], "[]", new Object[]{30, 50, 100}, "[30, 50, 100]"),
            Arguments.of(new Object[0][0][0], "[]", complexArray, "[[30, 50, 100], [40, 60], [...]]")
        );
    }

    @ParameterizedTest
    @MethodSource("arrayParams")
    void formatMethodConvertArraysToString(final Object emptyArray,
                                           final String emptyArrayToString,
                                           final Object nonEmptyArray,
                                           final String nonEmptyArrayToString) {
        assertThat(
            new DefaultStepNameFormatter(defaultPattern(), false, false).format(
                "Empty array = {empty_array}, non empty array = {non_empty_array} !",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("empty_array", emptyArray);
                    replacements.put("non_empty_array", nonEmptyArray);
                    return replacements;
                }
            )
        ).isEqualTo("Empty array = " + emptyArrayToString + ", non empty array = " + nonEmptyArrayToString + " !");
    }

    @Test
    void formatMethodUseOnlyFirstGroup() {
        final Pattern pattern = Pattern.compile("\\{([^}]*)} \\{([^}]*)}");
        assertThat(
            new DefaultStepNameFormatter(pattern, false, false).format(
                "{obj1} {obj2}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj1", 111);
                    replacements.put("obj2", 222);
                    return replacements;
                }
            )
        ).isEqualTo("111");
    }

    @Test
    void formatMethodUseOnlyFirstOuterGroup() {
        final Pattern pattern = Pattern.compile("\\{(([a-z])*([0-9])*[^}]*)}");
        assertThat(
            new DefaultStepNameFormatter(pattern, false, false).format(
                "{obj1} {obj2}",
                () -> {
                    final Map<String, Object> replacements = new HashMap<>();
                    replacements.put("obj1", 111);
                    replacements.put("obj2", 222);
                    return replacements;
                }
            )
        ).isEqualTo("111 222");
    }

    @Test
    void formatMethodWithoutFieldForceAccessFindPublicFields() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format("{obj.publicChildField}", () -> replacements)
        ).isEqualTo("child obj public field value");

        assertThat(
            formatter.format("{obj.publicIntermediateField}", () -> replacements)
        ).isEqualTo("intermediate obj public field value");

        assertThat(
            formatter.format("{obj.publicParentField}", () -> replacements)
        ).isEqualTo("parent obj public field value");
    }

    @Test
    void formatMethodWithoutFieldForceAccessFailedForPrivateFields() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThatCode(() ->
            formatter.format("{obj.privateChildField}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);

        assertThatCode(() ->
            formatter.format("{obj.privateIntermediateField}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);

        assertThatCode(() ->
            formatter.format("{obj.privateParentField}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void formatMethodWithoutFieldForceAccessFailedForMissingFields() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThatCode(() ->
            formatter.format("{obj.missingField}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void formatMethodWithFieldForceAccessFindPublicFields() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), true, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format("{obj.publicChildField}", () -> replacements)
        ).isEqualTo("child obj public field value");

        assertThat(
            formatter.format("{obj.publicIntermediateField}", () -> replacements)
        ).isEqualTo("intermediate obj public field value");

        assertThat(
            formatter.format("{obj.publicParentField}", () -> replacements)
        ).isEqualTo("parent obj public field value");
    }

    @Test
    void formatMethodWithFieldForceAccessFindPrivateFields() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), true, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format("{obj.privateChildField}", () -> replacements)
        ).isEqualTo("child obj private field value");

        assertThat(
            formatter.format("{obj.privateIntermediateField}", () -> replacements)
        ).isEqualTo("intermediate obj private field value");

        assertThat(
            formatter.format("{obj.privateParentField}", () -> replacements)
        ).isEqualTo("parent obj private field value");
    }

    @Test
    void formatMethodWitFieldForceAccessFailedForMissingFields() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), true, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThatCode(() ->
            formatter.format("{obj.missingField}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void formatMethodWithoutMethodForceAccessFindPublicMethods() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format("{obj.publicChildMethod()}", () -> replacements)
        ).isEqualTo("child obj public method value");

        assertThat(
            formatter.format("{obj.publicIntermediateMethod()}", () -> replacements)
        ).isEqualTo("intermediate obj public method value");

        assertThat(
            formatter.format("{obj.publicParentMethod()}", () -> replacements)
        ).isEqualTo("parent obj public method value");
    }

    @Test
    void formatMethodWithoutMethodForceAccessFailedForPrivateMethods() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThatCode(() ->
            formatter.format("{obj.privateChildMethod()}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);

        assertThatCode(() ->
            formatter.format("{obj.privateIntermediateMethod()}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);

        assertThatCode(() ->
            formatter.format("{obj.privateParentMethod()}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void formatMethodWithoutMethodForceAccessFailedForMissingMethods() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThatCode(() ->
            formatter.format("{obj.missingMethod()}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void formatMethodWithMethodForceAccessFindPublicMethods() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, true);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format("{obj.publicChildMethod()}", () -> replacements)
        ).isEqualTo("child obj public method value");

        assertThat(
            formatter.format("{obj.publicIntermediateMethod()}", () -> replacements)
        ).isEqualTo("intermediate obj public method value");

        assertThat(
            formatter.format("{obj.publicParentMethod()}", () -> replacements)
        ).isEqualTo("parent obj public method value");
    }

    @Test
    void formatMethodWithMethodForceAccessFindPrivateMethods() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, true);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format("{obj.privateChildMethod()}", () -> replacements)
        ).isEqualTo("child obj private method value");

        assertThat(
            formatter.format("{obj.privateIntermediateMethod()}", () -> replacements)
        ).isEqualTo("intermediate obj private method value");

        assertThat(
            formatter.format("{obj.privateParentMethod()}", () -> replacements)
        ).isEqualTo("parent obj private method value");
    }

    @Test
    void formatMethodWithMethodForceAccessFailedForMissingMethods() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, true);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThatCode(() ->
            formatter.format("{obj.missingMethod()}", () -> replacements)
        ).isInstanceOf(StepNameFormatException.class);
    }

    @Test
    void formatMethodComplexWithoutForceAccess() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), false, false);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format(
                "{obj.publicChildMethod().publicSeparateMethod().publicSeparateMethod().publicSeparateField}",
                () -> replacements
            )
        ).isEqualTo("separate obj public field value");
    }

    @Test
    void formatMethodComplexWithForceAccess() {
        final DefaultStepNameFormatter formatter = new DefaultStepNameFormatter(defaultPattern(), true, true);
        final Map<String, Object> replacements = new HashMap<>();
        replacements.put("obj", new ChildObj());

        assertThat(
            formatter.format(
                "{obj.privateChildMethod().privateSeparateMethod().privateSeparateMethod().privateSeparateField}",
                () -> replacements
            )
        ).isEqualTo("separate obj private field value");
    }

    private static Pattern defaultPattern() {
        return Pattern.compile("\\{([^}]*)}");
    }

    static class ParentObj {
        public final SeparateObj publicParentField =
            new SeparateObj("parent obj public field value");
        private final SeparateObj privateParentField =
            new SeparateObj("parent obj private field value");

        public SeparateObj publicParentMethod() {
            return new SeparateObj("parent obj public method value");
        }

        private SeparateObj privateParentMethod() {
            return new SeparateObj("parent obj private method value");
        }
    }

    static class IntermediateObj extends ParentObj {
        public final SeparateObj publicIntermediateField =
            new SeparateObj("intermediate obj public field value");
        private final SeparateObj privateIntermediateField =
            new SeparateObj("intermediate obj private field value");

        public SeparateObj publicIntermediateMethod() {
            return new SeparateObj("intermediate obj public method value");
        }

        private SeparateObj privateIntermediateMethod() {
            return new SeparateObj("intermediate obj private method value");
        }
    }

    static class ChildObj extends IntermediateObj {
        public final SeparateObj publicChildField =
            new SeparateObj("child obj public field value");
        private final SeparateObj privateChildField =
            new SeparateObj("child obj private field value");

        public SeparateObj publicChildMethod() {
            return new SeparateObj("child obj public method value");
        }

        private SeparateObj privateChildMethod() {
            return new SeparateObj("child obj private method value");
        }
    }

    static class SeparateObj {
        public final String publicSeparateField = "separate obj public field value";
        private final String privateSeparateField = "separate obj private field value";
        private final String toStringValue;

        SeparateObj(final String toStringValue) {
            this.toStringValue = toStringValue;
        }

        public SeparateObj publicSeparateMethod() {
            return new SeparateObj("separate obj public method value");
        }

        private SeparateObj privateSeparateMethod() {
            return new SeparateObj("separate obj private method value");
        }

        @Override
        public String toString() {
            return this.toStringValue;
        }
    }
}
