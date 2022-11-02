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
package de.flapdoodle.formula.values;

import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable(builder = false)
public abstract class Named<T> implements de.flapdoodle.formula.Value<T>, ValueSink<T>, ValueSource<T> {
	@Value.Parameter
	protected abstract Optional<String> name();

	@Value.Parameter
	protected abstract Class<T> type();

	public static <T> Named<T> ofType(Class<T> type) {
		return ImmutableNamed.of(Optional.empty(), type);
	}

	public static <T> Named<T> named(String name, Class<T> type) {
		return ImmutableNamed.of(Optional.of(name), type);
	}
}

