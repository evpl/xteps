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
package com.plugatar.xteps.core.exception;

/**
 * Thrown to indicate incorrect Xteps API configuration.
 */
public class ConfigException extends XtepsException {

    /**
     * Ctor.
     */
    public ConfigException() {
        super();
    }

    /**
     * Ctor.
     *
     * @param message the message
     */
    public ConfigException(final String message) {
        super(message);
    }

    /**
     * Ctor.
     *
     * @param cause the cause
     */
    public ConfigException(final Throwable cause) {
        super(cause);
    }

    /**
     * Ctor.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ConfigException(final String message,
                           final Throwable cause) {
        super(message, cause);
    }
}
