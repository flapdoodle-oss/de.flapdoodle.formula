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

import de.flapdoodle.formula.values.Named;
import de.flapdoodle.formula.values.Related;

public interface Value<T> {

	default <B> Related<T, B> relatedTo(B base) {
		return Related.to(this, base);
	}

	static <T> Named<T> ofType(Class<T> type) {
		return Named.ofType(type);
	}
	static <T> Named<T> named(String name, Class<T> type) {
		return Named.named(name, type);
	}
	static <T> Unvalidated<T> unvalidated(ValueSource<T> source) { return Unvalidated.wrap(source); }
}
