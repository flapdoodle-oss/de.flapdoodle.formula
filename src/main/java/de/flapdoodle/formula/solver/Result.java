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
package de.flapdoodle.formula.solver;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.validation.ValidationError;
import de.flapdoodle.types.Either;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public interface Result {
	Set<Value<?>> validatedValues();

	Map<Value<?>, ValidationError> validationErrors();

	@org.immutables.value.Value.Auxiliary <T> @Nullable T get(Value<T> id);

	@org.immutables.value.Value.Auxiliary
	default <T> Either<T, ValidationError> valueOrError(Value<T> id) {
		ValidationError errors = validationErrors().get(id);
		return errors != null ? Either.right(errors) : Either.left(get(id));
	}
}
