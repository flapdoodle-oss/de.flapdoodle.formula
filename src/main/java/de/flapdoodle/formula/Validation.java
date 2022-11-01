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

import com.google.common.collect.ImmutableList;

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
		<T> Validator.ValidatedValue<T> get(ValueSource<T> id);
	}

	@org.immutables.value.Value.Immutable(builder = false)
	abstract class Self<X> implements Validation<X> {

		@org.immutables.value.Value.Parameter
		protected abstract Validator.Self<X> validation();

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
			Validator.Self<X> validation
		) {
			return ImmutableSelf.of(destination, validation);
		}
	}

	@org.immutables.value.Value.Immutable(builder = false)
	abstract class RelatedTo1<X, A> implements Validation<X> {
		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<A> source();

		@org.immutables.value.Value.Parameter
		protected abstract Validator.RelatedTo1<X, A> validation();

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
			Validator.RelatedTo1<X, A> validation
		) {
			return ImmutableRelatedTo1.of(destination, source, validation);
		}
	}

	@org.immutables.value.Value.Immutable(builder = false)
	abstract class RelatedTo2<X, A, B> implements Validation<X> {
		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<A> a();

		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<B> b();

		@org.immutables.value.Value.Parameter
		protected abstract Validator.RelatedTo2<X, A, B> validation();

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
			Validator.RelatedTo2<X, A, B> validation
		) {
			return ImmutableRelatedTo2.of(destination, a, b, validation);
		}
	}
}
