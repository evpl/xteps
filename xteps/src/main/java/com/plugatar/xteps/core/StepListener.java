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

/**
 * Step listener.
 */
public interface StepListener {

    /**
     * Invoked each time a step starts.
     *
     * @param uuid     the uuid
     * @param stepName the step name
     */
    void stepStarted(String uuid, String stepName);

    /**
     * Invoked each time a step passes.
     *
     * @param uuid     the uuid
     * @param stepName the step name
     */
    void stepPassed(String uuid, String stepName);

    /**
     * Invoked each time a step fails.
     *
     * @param uuid     the uuid
     * @param stepName the step name
     */
    void stepFailed(String uuid, String stepName, Throwable throwable);
}