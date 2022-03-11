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
package com.plugatar.xteps.core;

import com.plugatar.xteps.core.exception.ArgumentException;
import com.plugatar.xteps.core.exception.StepNameFormatException;

/**
 * Step name formatter.
 */
public interface StepNameFormatter {

    /**
     * Returns formatted step name.
     *
     * @param stepName             the step name
     * @param replacementsSupplier the step name replacements supplier
     * @return formatted step name
     * @throws ArgumentException       if {@code stepName} is null or empty
     *                                 or if {@code replacementsSupplier} is null
     * @throws StepNameFormatException if it's impossible to correctly format the step name
     */
    String format(String stepName,
                  StepNameReplacementsSupplier replacementsSupplier);
}
