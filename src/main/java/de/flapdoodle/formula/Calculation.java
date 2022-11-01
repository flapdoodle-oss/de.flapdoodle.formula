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
import java.util.stream.Collectors;

public interface Calculation<D> {
	@org.immutables.value.Value.Parameter
	Value<D> destination();

	@org.immutables.value.Value.Lazy
	List<? extends ValueSource<?>> sources();

	@org.immutables.value.Value.Auxiliary
	D calculate(ValueLookup values);

	@FunctionalInterface
	interface ValueLookup {
		<T> T get(Value<T> id);
	}

	@org.immutables.value.Value.Immutable(builder = false)
	abstract class Direct<A, X> implements Calculation<X> {
		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<A> source();

		@org.immutables.value.Value.Parameter
		protected abstract Transformations.F1<A, X> transformation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(source());
		}

		@Override
		public X calculate(ValueLookup values) {
			return transformation().apply(values.get(source()));
		}

		public static <A, X> Direct<A, X> with(
			ValueSource<A> source,
			ValueSink<X> destination,
			Transformations.F1<A, X> transformation
		) {
			return ImmutableDirect.of(destination, source, transformation);
		}
	}

	@org.immutables.value.Value.Immutable(builder = false)
	abstract class Merge2<A, B, X> implements Calculation<X> {
		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<A> a();

		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<B> b();

		@org.immutables.value.Value.Parameter
		protected abstract Transformations.F2<A, B, X> transformation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(a(), b());
		}

		@Override
		public X calculate(ValueLookup values) {
			return transformation().apply(values.get(a()), values.get(b()));
		}

		public static <A, B, X> Merge2<A, B, X> with(
			ValueSource<A> a,
			ValueSource<B> b,
			ValueSink<X> destination,
			Transformations.F2<A, B, X> transformation
		) {
			return ImmutableMerge2.of(destination, a, b, transformation);
		}
	}

	@org.immutables.value.Value.Immutable(builder = false)
	abstract class Merge3<A, B, C, X> implements Calculation<X> {
		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<A> a();

		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<B> b();

		@org.immutables.value.Value.Parameter
		protected abstract ValueSource<C> c();

		@org.immutables.value.Value.Parameter
		protected abstract Transformations.F3<A, B, C, X> transformation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(a(), b(), c());
		}

		@Override
		public X calculate(ValueLookup values) {
			return transformation().apply(values.get(a()), values.get(b()), values.get(c()));
		}

		public static <A, B, C, X> Merge3<A, B, C, X> with(
			ValueSource<A> a,
			ValueSource<B> b,
			ValueSource<C> c,
			ValueSink<X> destination,
			Transformations.F3<A, B, C, X> transformation
		) {
			return ImmutableMerge3.of(destination, a, b, c, transformation);
		}
	}

	// TODO is this of any use?
	@org.immutables.value.Value.Immutable(builder = false)
	abstract class Aggregated<X> implements Calculation<List<X>> {
		@org.immutables.value.Value.Parameter
		protected abstract List<ValueSource<X>> sourceList();

		@Override
		public List<? extends ValueSource<?>> sources() {
			return sourceList();
		}

		@Override
		public List<X> calculate(ValueLookup values) {
			return sourceList().stream()
				.map(id -> values.get(id))
				.collect(Collectors.toList());
		}

		public static <X> Aggregated<X> with(
			List<? extends ValueSource<X>> sourceList,
			ValueSink<List<X>> destination
		) {
			return ImmutableAggregated.of(destination, sourceList);
		}
	}
}
