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

import com.google.common.base.Preconditions;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.types.Checks;
import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class StrictValidatedValueLookup implements ValidatedValueLookup {
	@Value.Parameter
	protected abstract List<ValidatedValue<?>> validatedValues();

	@Value.Check
	protected void check() {
		Set<de.flapdoodle.formula.Value<?>> collidingIds = Checks.collisions(validatedValues(), ValidatedValue::source);
		Preconditions.checkArgument(collidingIds.isEmpty(),"multiple entries with following ids: %s", collidingIds);
	}

	@Lazy
	protected Map<ValueSource<?>, ValidatedValue<?>> validatedValueMap() {
		return validatedValues().stream()
			.collect(Collectors.toMap(ValidatedValue::source, Function.identity()));
	}


	@Override
	public <T> ValidatedValue<T> get(ValueSource<T> id) {
		Preconditions.checkArgument(validatedValueMap().containsKey(id),"value not set: %s", id);
		return (ValidatedValue<T>) validatedValueMap().get(id);
	}

	public static StrictValidatedValueLookup with(List<ValidatedValue<?>> validatedValues) {
		return ImmutableStrictValidatedValueLookup.of(validatedValues);
	}
}
