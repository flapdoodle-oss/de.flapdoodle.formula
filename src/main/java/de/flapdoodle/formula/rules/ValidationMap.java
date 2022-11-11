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
package de.flapdoodle.formula.rules;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.types.Checks;
import de.flapdoodle.formula.validation.Validation;
import org.immutables.value.Value.Check;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Immutable
public abstract class ValidationMap {
	public abstract List<Validation<?>> all();

	@Check
	protected void check() {
		Set<Value<?>> collidingIds = Checks.collisions(all(), Validation::destination);
		Preconditions.checkArgument(collidingIds.isEmpty(),"multiple entries with following ids: %s", collidingIds);
	}

	@Lazy
	protected Map<Value<?>, Validation<?>> map() {
		return all().stream()
			.collect(ImmutableMap.toImmutableMap(Validation::destination, Function.identity()));
	}

	@Lazy
	public Set<Value<?>> keys() {
		return map().keySet();
	}
	public <T> Validation<T> get(Value<T> key) {
		return (Validation<T>) map().get(key);
	}
	public boolean contains(Value<?> it) {
		return map().containsKey(it);
	}


	public ValidationMap add(Validation<?> validation) {
		return ImmutableValidationMap.builder()
			.from(this)
			.addAll(validation)
			.build();
	}

	public ValidationMap addAll(List<Validation<?>> validations) {
		return ImmutableValidationMap.builder()
			.from(this)
			.addAllAll(validations)
			.build();
	}

	public static ValidationMap empty() {
		return ImmutableValidationMap.builder().build();
	}
}
