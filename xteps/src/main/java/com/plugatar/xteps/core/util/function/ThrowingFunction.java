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
package com.plugatar.xteps.core.util.function;

/**
 * The {@link java.util.function.Function} specialization that might
 * throw a Throwable.
 *
 * @param <T>  the type of the input argument
 * @param <R>  the type of the result
 * @param <TH> the type of the thrown Throwable
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, TH extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the input argument
     * @return the result
     * @throws TH if function threw exception
     */
    R apply(T t) throws TH;
}