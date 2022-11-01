/*
 * Copyright (C) 2011
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
import de.flapdoodle.formula.ErrorMessage;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.types.Either;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@org.immutables.value.Value.Immutable
public abstract class Context {
	@org.immutables.value.Value.Default
	protected ValueContainer values() {
		return ValueContainer.empty();
	}
	@org.immutables.value.Value.Default
	protected ValueContainer validatedValues() {
		return ValueContainer.empty();
	}
	protected abstract Set<Value<?>> validatedValuesWithErrors();

	protected abstract Map<Value<?>, List<ErrorMessage>> errorMessages();

	public <T> Context addValue(Value<T> id, @Nullable T value) {
		return ImmutableContext.copyOf(this).withValues(values().add(id, value));
	}

	public <T> @Nullable T getValue(Value<T> id) {
		return values().get(id);
	}

	public <T> Context addValidated(Value<T> id, Either<T, List<ErrorMessage>> valueOrErrorMessages) {
		return valueOrErrorMessages.map(value -> ImmutableContext.copyOf(this)
			.withValidatedValues(validatedValues().add(id, value)), errors -> ImmutableContext.builder()
			.from(this)
			.validatedValues(validatedValues().add(id, null))
			.addValidatedValuesWithErrors(id)
			.putErrorMessages(id, errors)
			.build());
	}

	public <T> @Nullable T getValidated(Value<T> id) {
		return validatedValues().get(id);
	}

	public boolean hasValidationErrors(Value<?> id) {
		return validatedValuesWithErrors().contains(id);
	}

	public List<ErrorMessage> validationErrors(Value<?> id) {
		return Preconditions.checkNotNull(errorMessages().get(id),"no error messages set for %s", id);
	}

	public static Context empty() {
		return ImmutableContext.builder()
			.build();
	}
}
