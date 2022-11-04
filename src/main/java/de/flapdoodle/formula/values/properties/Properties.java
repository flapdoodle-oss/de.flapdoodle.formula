/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.formula.values.properties;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Properties {
	private Properties() {
		// no instance
	}

	public static <O, T> ReadOnlyProperty<O, T> readOnly(Class<O> type, String name, Function<O, T> getter) {
		return ReadOnlyProperty.of(type,name,getter);
	}

	public static <O, T> CopyOnChangeProperty<O, T> copyOnChange(Class<O> type, String name, Function<O, T> getter, BiFunction<O, T, O> copyOnWrite) {
		return CopyOnChangeProperty.of(type,name,getter,copyOnWrite);
	}
}
