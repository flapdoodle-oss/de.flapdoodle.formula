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

import java.util.List;
import java.util.Optional;

public interface Validation<D> {
	@org.immutables.value.Value.Parameter
	Value<D> destination();

	@org.immutables.value.Value.Lazy
	List<? extends ValueSource<?>> sources();

	@org.immutables.value.Value.Auxiliary
	List<ErrorMessage> validate(Validator validator, Optional<D> unvalidatedValue, ValueLookup values);

	interface ValueLookup {
		<T> ValidatedValue<T> get(ValueSource<T> id);
	}
}
