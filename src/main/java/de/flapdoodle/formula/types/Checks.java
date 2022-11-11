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
package de.flapdoodle.formula.types;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Checks {
	private Checks() {
		// no instance
	}

	public static <T, ID> Set<ID> collisions(Collection<T> values, Function<T, ID> idFunction) {
		return values.stream()
			.collect(Collectors.groupingBy(idFunction))
			.entrySet()
			.stream()
			.filter(it -> it.getValue().size() != 1)
			.map(Map.Entry::getKey)
			.collect(Collectors.toSet());
	}
}
