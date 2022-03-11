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

import com.plugatar.xteps.core.StepNameFormatter;
import com.plugatar.xteps.core.StepNameReplacementsSupplier;
import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepNameFormatException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default step name formatter.
 */
public class DefaultStepNameFormatter implements StepNameFormatter {
    private final Pattern replacementPattern;
    private final boolean fieldForceAccess;
    private final boolean methodForceAccess;

    /**
     * Ctor.
     *
     * @param replacementPattern the replacement pattern
     * @param fieldForceAccess   the field force access flag
     * @param methodForceAccess  the method force access flag
     * @throws ArgumentException if {@code replacementPattern} is null or
     *                           contains less than one group
     */
    public DefaultStepNameFormatter(final Pattern replacementPattern,
                                    final boolean fieldForceAccess,
                                    final boolean methodForceAccess) {
        this.replacementPattern = patternContainsCapturingGroups(replacementPattern);
        this.fieldForceAccess = fieldForceAccess;
        this.methodForceAccess = methodForceAccess;
    }

    private static Pattern patternContainsCapturingGroups(final Pattern pattern) {
        if (pattern == null) { throw new ArgumentException("replacementPattern arg is null"); }
        if (pattern.matcher("").groupCount() < 1) {
            throw new ArgumentException("replacementPattern arg " + pattern + " doesn't contain groups, " +
                "pattern must contain at least one group");
        }
        return pattern;
    }

    @Override
    public final String format(final String stepName,
                               final StepNameReplacementsSupplier replacementsSupplier) {
        if (stepName == null) { throw new ArgumentException("stepName is null"); }
        if (stepName.isEmpty()) { throw new ArgumentException("stepName is empty"); }
        if (replacementsSupplier == null) { throw new ArgumentException("replacementsSupplier is null"); }
        final StringBuffer stringBuffer = new StringBuffer();
        final Matcher matcher = this.replacementPattern.matcher(stepName);
        Map<String, Object> replacements = null;
        while (matcher.find()) {
            if (replacements == null) {
                replacements = replacementsSupplier.stepNameReplacements();
            }
            final String[] path = matcher.group(1).split("\\.");
            final String replacementPointer = path[0];
            if (replacements.containsKey(replacementPointer)) {
                final Object replacementValue = replacements.get(replacementPointer);
                matcher.appendReplacement(
                    stringBuffer,
                    Matcher.quoteReplacement(objectAsString(
                        path.length == 1 ? replacementValue : extractValue(
                            path, replacementValue, this.fieldForceAccess, this.methodForceAccess
                        )
                    ))
                );
            }
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static Object extractValue(final String[] path,
                                       final Object firstPartValue,
                                       final boolean fieldForceAccess,
                                       final boolean methodForceAccess) {
        Object lastValue = firstPartValue;
        for (int idx = 1; idx < path.length; ++idx) {
            final String pathPart = path[idx];
            if (pathPart.indexOf('(') != -1 || pathPart.indexOf(')') != -1) {
                lastValue = methodValue(lastValue, pathPart, methodForceAccess);
            } else {
                lastValue = fieldValue(lastValue, pathPart, fieldForceAccess);
            }
        }
        return lastValue;
    }

    private static Object methodValue(final Object obj,
                                      final String pathPart,
                                      final boolean forceAccess) {
        if (pathPart.length() < 3 || !pathPart.endsWith("()")) {
            throw new StepNameFormatException(String.format("Incorrect method %s", pathPart));
        }
        if (obj == null) {
            throw new StepNameFormatException(String.format("Cannot invoke %s method on null", pathPart));
        }
        final Class<?> cls = obj.getClass();
        final Method method;
        try {
            method = findMethod(cls, pathPart.substring(0, pathPart.length() - 2), forceAccess);
        } catch (final Exception ex) {
            throw new StepNameFormatException(String.format("Cannot get %s %s cause %s",
                methodDesc(cls, pathPart), forceAccessDesc(forceAccess), ex), ex);
        }
        if (method == null) {
            throw new StepNameFormatException(String.format("Cannot find %s %s",
                methodDesc(cls, pathPart), forceAccessDesc(forceAccess)));
        }
        if (method.getReturnType() == void.class) {
            throw new StepNameFormatException(String.format("Return type of %s is void", methodDesc(cls, pathPart)));
        }
        try {
            return invokeMethod(obj, method, forceAccess);
        } catch (final InvocationTargetException ex) {
            Throwable targetEx = (targetEx = ex.getCause()) != null ? targetEx : ex;
            throw new StepNameFormatException(String.format("%s %s threw %s",
                methodDesc(cls, pathPart), forceAccessDesc(forceAccess), targetEx), targetEx);
        } catch (final Exception ex) {
            throw new StepNameFormatException(String.format("Cannot invoke %s %s cause %s",
                methodDesc(cls, pathPart), forceAccessDesc(forceAccess), ex), ex);
        }
    }

    private static Method findMethod(final Class<?> cls, final String methodName, final boolean forceAccess) {
        try {
            return cls.getMethod(methodName);
        } catch (final NoSuchMethodException ignored) { }
        if (forceAccess) {
            for (Class<?> current = cls; current != null; current = current.getSuperclass()) {
                try {
                    return current.getDeclaredMethod(methodName);
                } catch (NoSuchMethodException ignored) { }
            }
        }
        return null;
    }

    private static Object invokeMethod(final Object obj, final Method method, final boolean forceAccess)
        throws InvocationTargetException, IllegalAccessException {
        if (forceAccess) {
            try {
                return method.invoke(obj);
            } catch (final IllegalAccessException ex) {
                method.setAccessible(true);
            }
        }
        return method.invoke(obj);
    }

    private static Object fieldValue(final Object obj,
                                     final String pathPart,
                                     final boolean forceAccess) {
        if (obj == null) {
            throw new StepNameFormatException(String.format("Cannot get %s field value of null", pathPart));
        }
        final Class<?> cls = obj.getClass();
        final Field field;
        try {
            field = findField(cls, pathPart, forceAccess);
        } catch (final Exception ex) {
            throw new StepNameFormatException(String.format("Cannot get %s cause %s",
                fieldDesc(cls, pathPart), ex), ex);
        }
        if (field == null) {
            throw new StepNameFormatException(String.format("Cannot find %s %s",
                fieldDesc(cls, pathPart), forceAccessDesc(forceAccess)));
        }
        try {
            return getFieldValue(obj, field, forceAccess);
        } catch (final Exception ex) {
            throw new StepNameFormatException(String.format("Cannot get %s value %s cause %s",
                fieldDesc(cls, pathPart), forceAccessDesc(forceAccess), ex), ex);
        }
    }

    private static Field findField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
        try {
            return cls.getField(fieldName);
        } catch (final NoSuchFieldException ignored) { }
        if (forceAccess) {
            for (Class<?> current = cls; current != null; current = current.getSuperclass()) {
                try {
                    return current.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) { }
            }
        }
        return null;
    }

    private static Object getFieldValue(final Object obj, final Field field, final boolean forceAccess)
        throws IllegalAccessException {
        if (forceAccess) {
            try {
                return field.get(obj);
            } catch (final IllegalAccessException ex) {
                field.setAccessible(true);
            }
        }
        return field.get(obj);
    }

    private static String objectAsString(final Object obj) {
        if (obj == null) {
            return "null";
        }
        final Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            if (cls == byte[].class) {
                return Arrays.toString((byte[]) obj);
            } else if (cls == short[].class) {
                return Arrays.toString((short[]) obj);
            } else if (cls == int[].class) {
                return Arrays.toString((int[]) obj);
            } else if (cls == long[].class) {
                return Arrays.toString((long[]) obj);
            } else if (cls == char[].class) {
                return Arrays.toString((char[]) obj);
            } else if (cls == float[].class) {
                return Arrays.toString((float[]) obj);
            } else if (cls == double[].class) {
                return Arrays.toString((double[]) obj);
            } else if (cls == boolean[].class) {
                return Arrays.toString((boolean[]) obj);
            } else {
                try {
                    return Arrays.deepToString((Object[]) obj);
                } catch (final Exception ex) {
                    throw new StepNameFormatException(methodDesc(cls, "toString()") + " threw " + ex, ex);
                }
            }
        }
        try {
            return obj.toString();
        } catch (final Exception ex) {
            throw new StepNameFormatException(methodDesc(cls, "toString()") + " threw " + ex, ex);
        }
    }

    private static String methodDesc(final Class<?> cls, final String method) {
        return cls.getTypeName() + " object " + method + " method";
    }

    private static String fieldDesc(final Class<?> cls, final String field) {
        return cls.getTypeName() + " object " + field + " field";
    }

    private static String forceAccessDesc(final boolean forceAccess) {
        return forceAccess ? "with force access" : "without force access";
    }
}
