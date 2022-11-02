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

import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import java.util.List;
import java.util.Optional;

public abstract class Validations {

	public interface V0<T> {
		List<ErrorMessage> validate(Validator validator, Optional<T> value);
	}

	public interface V1<T, A> {
		List<ErrorMessage> validate(Validator validator,Optional<T> value, ValidatedValue<A> a);
	}

	public interface V2<T, A, B> {
		List<ErrorMessage> validate(Validator validator,Optional<T> value, ValidatedValue<A> a, ValidatedValue<B> b);
	}

	@Immutable(builder = false)
	abstract static class Self<X> implements Validation<X> {

		@Parameter
		protected abstract V0<X> validation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of();
		}

		@Override
		public List<ErrorMessage> validate(Validator validator, Optional<X> unvalidatedValue, ValueLookup values) {
			return validation().validate(validator, unvalidatedValue);
		}

		public static <X> Self<X> with(
			Value<X> destination,
			V0<X> validation
		) {
			return ImmutableSelf.of(destination, validation);
		}
	}

	@Immutable(builder = false)
	abstract static class RelatedTo1<X, A> implements Validation<X> {
		@Parameter
		protected abstract ValueSource<A> source();

		@Parameter
		protected abstract V1<X, A> validation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(source());
		}

		@Override
		public List<ErrorMessage> validate(Validator validator, Optional<X> unvalidatedValue, ValueLookup values) {
			return validation().validate(validator, unvalidatedValue, values.get(source()));
		}

		public static <X, A> RelatedTo1<X, A> with(
			Value<X> destination,
			ValueSource<A> source,
			V1<X, A> validation
		) {
			return ImmutableRelatedTo1.of(destination, source, validation);
		}
	}

	@Immutable(builder = false)
	abstract static class RelatedTo2<X, A, B> implements Validation<X> {
		@Parameter
		protected abstract ValueSource<A> a();

		@Parameter
		protected abstract ValueSource<B> b();

		@Parameter
		protected abstract V2<X, A, B> validation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(a(), b());
		}

		@Override
		public List<ErrorMessage> validate(Validator validator, Optional<X> unvalidatedValue, ValueLookup values) {
			return validation().validate(validator, unvalidatedValue, values.get(a()), values.get(b()));
		}

		public static <X, A, B> RelatedTo2<X, A, B> with(
			Value<X> destination,
			ValueSource<A> a,
			ValueSource<B> b,
			V2<X, A, B> validation
		) {
			return ImmutableRelatedTo2.of(destination, a, b, validation);
		}
	}
}
