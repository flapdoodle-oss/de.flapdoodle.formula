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
import de.flapdoodle.formula.types.Either;
import org.immutables.builder.Builder;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Immutable
public abstract class ValidatedValue<T> {
	@Builder.Parameter
	public abstract ValueSource<T> source();
	public abstract Either<T, ValidationError> valueOrError();

	@Lazy
	public boolean isValid() {
		return valueOrError().isLeft();
	}

	@Lazy
	public List<ErrorMessage> errors() {
		Preconditions.checkArgument(!isValid(),"%s is valid", source());
		return valueOrError().right().errorMessages();
	}

	@Lazy
	public @Nullable T value() {
		Preconditions.checkArgument(isValid(),"%s is not valid", source());
		return valueOrError().left();
	}

	@Lazy
	public Set<? extends ValueSource<?>> invalidReferences() {
		Preconditions.checkArgument(!isValid(),"%s is valid", source());
		return valueOrError().right().invalidReferences();
	}

	public static <T> ImmutableValidatedValue.Builder<T> builder(ValueSource<T> source) {
		return ImmutableValidatedValue.builder(source);
	}

	public static <T> ValidatedValue<T> of(ValueSource<T> id, @Nullable T value) {
		return ImmutableValidatedValue.<T>builder()
			.source(id)
			.valueOrError(Either.left(value))
			.build();
	}

	@Deprecated
	public static <T> ValidatedValue<T> of(ValueSource<T> id, List<ErrorMessage> errorMessages, Set<? extends ValueSource<?>> invalidReferences) {
		return ImmutableValidatedValue.<T>builder()
			.source(id)
			.valueOrError(Either.right(ValidationError.of(errorMessages, invalidReferences)))
			.build();
	}

	public static <T> ValidatedValue<T> of(ValueSource<T> id, ValidationError validationError) {
		return ImmutableValidatedValue.<T>builder()
			.source(id)
			.valueOrError(Either.right(validationError))
			.build();
	}
}
