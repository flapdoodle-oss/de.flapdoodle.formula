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
package de.flapdoodle.formula.validation;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Validation<D> {
	@Parameter
	Value<D> destination();

	@Lazy
	Set<? extends ValueSource<?>> sources();

	@Auxiliary
	List<ErrorMessage> validate(Optional<D> unvalidatedValue, ValidatedValueLookup values);

	static List<ErrorMessage> noErrors() {
		return Collections.emptyList();
	}

	static List<ErrorMessage> error(String key) {
		return Collections.singletonList(ErrorMessage.of(key));
	}

	static List<ErrorMessage> error(String key, Object ... args) {
		return Collections.singletonList(ErrorMessage.of(key, args));
	}
}
