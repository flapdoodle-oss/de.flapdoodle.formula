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
package de.flapdoodle.formula;

import org.immutables.builder.Builder;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Validator {

	@Value.Immutable
	abstract class ValidatedValue<T> {
		@Builder.Parameter
		public abstract ValueSource<T> source();
		public abstract Optional<T> value();
		public abstract Set<ValueSource<?>> invalidReferences();

		public static <T> ImmutableValidatedValue.Builder<T> builder(ValueSource<T> source) {
			return ImmutableValidatedValue.builder(source);
		}
	}


	interface Self<T> {
		List<ErrorMessage> validate(Validator validator, Optional<T> value);
	}

	interface RelatedTo1<T, A> {
		List<ErrorMessage> validate(Validator validator,Optional<T> value, ValidatedValue<A> a);
	}

	interface RelatedTo2<T, A, B> {
		List<ErrorMessage> validate(Validator validator,Optional<T> value, ValidatedValue<A> a, ValidatedValue<B> b);
	}
}
