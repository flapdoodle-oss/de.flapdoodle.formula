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
package de.flapdoodle.formula;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import java.util.Optional;

public interface Value<T> {

	@Immutable(builder = false)
	abstract class Named<T> implements Value<T>, ValueSink<T>, ValueSource<T> {
		@Parameter
		protected abstract Optional<String> name();

		@Parameter
		protected abstract Class<T> type();
	}

	static <T> Named<T> ofType(Class<T> type) {
		return ImmutableNamed.of(Optional.empty(), type);
	}

	static <T> Named<T> named(String name, Class<T> type) {
		return ImmutableNamed.of(Optional.of(name), type);
	}
}
