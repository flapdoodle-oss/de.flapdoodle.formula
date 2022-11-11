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

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueContainer;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.MappedValue;
import de.flapdoodle.formula.validation.ValidationError;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@org.immutables.value.Value.Immutable
public abstract class Context {
	@org.immutables.value.Value.Default
	protected ValueContainer unvalidated() {
		return ValueContainer.empty();
	}
	@org.immutables.value.Value.Default
	protected ValueContainer validatedValues() {
		return ValueContainer.empty();
	}

	protected abstract Map<Value<?>, ValidationError> validationErrorMap();

	@org.immutables.value.Value.Check
	protected void check() {
		Set<Value<?>> unvalidated = unvalidated().keys();
		Set<Value<?>> validated = validatedValues().keys();
		Set<Value<?>> invalid = validationErrorMap().keySet();

		Sets.SetView<Value<?>> validatedAndUnvalidated = Sets.intersection(unvalidated, validated);
		Sets.SetView<Value<?>> validatedAndInvalid = Sets.intersection(validated, invalid);
		Sets.SetView<Value<?>> unvalidAndInvalid = Sets.intersection(unvalidated, invalid);

		Preconditions.checkArgument(validatedAndUnvalidated.isEmpty(),"validated AND unvalidated: %s", validatedAndUnvalidated);
		Preconditions.checkArgument(validatedAndInvalid.isEmpty(),"validated AND invalid: %s", validatedAndInvalid);
		Preconditions.checkArgument(unvalidAndInvalid.isEmpty(),"unvalidated AND invalid: %s", unvalidAndInvalid);
	}

	public <T> Context add(Value<T> id, T value) {
		return ImmutableContext.copyOf(this)
			.withValidatedValues(validatedValues().add(id,value));
	}

	public <T> Context addUnvalidated(Value<T> id, @Nullable T value) {
		return ImmutableContext.copyOf(this)
			.withUnvalidated(unvalidated().add(id, value));
	}

	public <T> Context addInvalid(Value<T> id, ValidationError validationError) {
		return ImmutableContext.builder()
			.from(this)
			.putValidationErrorMap(id, validationError)
			.build();
	}
	public <T> Context addIfNotExist(MappedValue<T> mappedValue) {
		return (mappedValue.id() instanceof Unvalidated) || isValid(mappedValue.id()) || isInvalid(mappedValue.id())
			? this
			: add(mappedValue.id(), mappedValue.value());
	}

	public Context addIfNotExist(List<MappedValue<?>> entries) {
		Context current = this;
		for (MappedValue<?> entry : entries) {
			current=current.addIfNotExist(entry);
		}
		return current;
	}

	public <T> @Nullable T getUnvalidated(Value<T> id) {
		return unvalidated().get(id);
	}

	public <T> @Nullable T getValidated(Value<T> id) {
		return validatedValues().get(id);
	}

	public boolean isValid(Value<?> id) {
		return validatedValues().keys().contains(id);
	}
	public boolean isInvalid(Value<?> id) {
		return validationErrorMap().containsKey(id);
	}

	public <T> ValidationError validationError(ValueSource<T> id) {
		Preconditions.checkArgument(validationErrorMap().containsKey(id),"no validation error for %s", id);
		return validationErrorMap().get(id);
	}

	@org.immutables.value.Value.Auxiliary
	public Result asResult() {
		return new Result() {
			@Override
			public Set<Value<?>> validatedValues() {
				return Context.this.validatedValues().keys();
			}
			@Override
			public Map<Value<?>, ValidationError> validationErrors() {
				return validationErrorMap();
			}

			@Override
			public <T> @Nullable T get(Value<T> id) {
				return isInvalid(id)
					? null
					: Context.this.validatedValues().get(id);
			}
		};
	}

	public static Context empty() {
		return ImmutableContext.builder()
			.build();
	}
}
