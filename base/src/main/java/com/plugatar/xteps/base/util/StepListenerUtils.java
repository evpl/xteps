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
package com.plugatar.xteps.base.util;

import com.plugatar.xteps.base.XtepsException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Step listener utils.
 */
public final class StepListenerUtils {

    /**
     * Utility class ctor.
     */
    private StepListenerUtils() {
    }

    /**
     * Returns a map of parameters and indexes.
     *
     * @param params the params array
     * @return map of parameters and indexes
     */
    public static Map<String, Object> paramArrayAsMap(final Object[] params) {
        if (params == null) { throw new XtepsException("params arg is null"); }
        if (params.length != 0) {
            final Map<String, Object> map = new HashMap<>(params.length, 1.0f);
            for (int idx = 0; idx < params.length; ++idx) {
                map.put(String.valueOf(idx), params[idx]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * Returns a map of parameters and indexes.
     *
     * @param leftNameBorder  the left param name border
     * @param rightNameBorder the right param name border
     * @param params          the params array
     * @return map of parameters and indexes
     */
    public static Map<String, Object> paramArrayAsMap(final char leftNameBorder,
                                                      final char rightNameBorder,
                                                      final Object[] params) {
        if (params == null) { throw new XtepsException("params arg is null"); }
        if (params.length != 0) {
            final Map<String, Object> map = new HashMap<>(params.length, 1.0f);
            for (int idx = 0; idx < params.length; ++idx) {
                map.put(leftNameBorder + String.valueOf(idx) + rightNameBorder, params[idx]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * Returns processed template.
     *
     * @param template     the template
     * @param replacements the replacements map
     * @return processed template
     */
    public static String processedTemplate(final String template,
                                           final Map<String, Object> replacements) {
        if (template == null) { throw new XtepsException("template arg is null"); }
        if (replacements == null) { throw new XtepsException("replacements arg is null"); }
        if (replacements.isEmpty()) {
            return template;
        }
        String processedTemplate = template;
        for (final Map.Entry<String, Object> entry : replacements.entrySet()) {
            final String key = entry.getKey();
            if (processedTemplate.contains(key)) {
                processedTemplate = processedTemplate.replace(key, objAsString(entry.getValue()));
            }
        }
        return processedTemplate;
    }

    /**
     * Returns {@code String} representation of given object.
     *
     * @param obj the object
     * @return {@code String} representation of given object
     */
    public static String objAsString(final Object obj) {
        if (obj == null) {
            return "null";
        }
        final Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            if (cls == int[].class) {
                return Arrays.toString((int[]) obj);
            } else if (cls == long[].class) {
                return Arrays.toString((long[]) obj);
            } else if (cls == double[].class) {
                return Arrays.toString((double[]) obj);
            } else if (cls == char[].class) {
                return Arrays.toString((char[]) obj);
            } else if (cls == boolean[].class) {
                return Arrays.toString((boolean[]) obj);
            } else if (cls == byte[].class) {
                return Arrays.toString((byte[]) obj);
            } else if (cls == short[].class) {
                return Arrays.toString((short[]) obj);
            } else if (cls == float[].class) {
                return Arrays.toString((float[]) obj);
            } else {
                return Arrays.toString((Object[]) obj);
            }
        }
        return String.valueOf(obj);
    }
}
