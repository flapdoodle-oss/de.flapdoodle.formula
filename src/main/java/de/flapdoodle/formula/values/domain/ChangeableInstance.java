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
package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.solver.Result;
import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value;

public interface ChangeableInstance<O extends ChangeableInstance<O>> extends HasId<O> {
	@Value.Auxiliary
	<T> O change(ChangeableValue<?,T> id, T value);

	@Value.Auxiliary
	<T> Maybe<T> findValue(ReadableValue<? , T> id);

	static <O extends ChangeableInstance<O>> O change(O instance, Result result) {
		O current = instance;
		for (de.flapdoodle.formula.Value<?> value : result.validatedValues()) {
			if (value instanceof ChangeableValue) {
				current = (O) current.change((ChangeableValue) value, result.get(value));
			}
		}
		return current;
	}
}
