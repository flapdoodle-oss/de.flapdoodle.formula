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
package de.flapdoodle.formula.calculate;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Calculations {
	private Calculations() {
		// no instance
	}
	public static <X> F1<X, X> identity() {
		return a -> a;
	}
	@FunctionalInterface public
	interface F1<A, R> extends Function<A, R> {
		@Override @Nullable R apply(@Nullable A a);
	}

	@FunctionalInterface public
	interface F2<A, B, R> extends BiFunction<A, B, R> {
		@Override @Nullable R apply(@Nullable A a,@Nullable B b);
	}

	@FunctionalInterface public
	interface F3<A, B, C, R> {
		@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c);
	}

	@Immutable(builder = false)
	abstract static class Direct<A, X> implements Calculation<X>, HasHumanReadableLabel {
		@Parameter
		protected abstract ValueSource<A> source();

		@Parameter
		protected abstract F1<A, X> transformation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(source());
		}

		@Override
		public X calculate(ValueLookup values) {
			return transformation().apply(values.get(source()));
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(transformation());
		}

		public static <A, X> Direct<A, X> with(
			ValueSource<A> source,
			ValueSink<X> destination,
			F1<A, X> transformation
		) {
			return ImmutableDirect.of(destination, source, transformation);
		}
	}

	@Immutable(builder = false)
	abstract static class Merge2<A, B, X> implements Calculation<X>, HasHumanReadableLabel {
		@Parameter
		protected abstract ValueSource<A> a();

		@Parameter
		protected abstract ValueSource<B> b();

		@Parameter
		protected abstract F2<A, B, X> transformation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(a(), b());
		}

		@Override
		public X calculate(ValueLookup values) {
			return transformation().apply(values.get(a()), values.get(b()));
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(transformation());
		}

		public static <A, B, X> Merge2<A, B, X> with(
			ValueSource<A> a,
			ValueSource<B> b,
			ValueSink<X> destination,
			F2<A, B, X> transformation
		) {
			return ImmutableMerge2.of(destination, a, b, transformation);
		}
	}

	@Immutable(builder = false)
	abstract static class Merge3<A, B, C, X> implements Calculation<X>, HasHumanReadableLabel {
		@Parameter
		protected abstract ValueSource<A> a();

		@Parameter
		protected abstract ValueSource<B> b();

		@Parameter
		protected abstract ValueSource<C> c();

		@Parameter
		protected abstract F3<A, B, C, X> transformation();

		@Override
		public List<ValueSource<?>> sources() {
			return ImmutableList.of(a(), b(), c());
		}

		@Override
		public X calculate(ValueLookup values) {
			return transformation().apply(values.get(a()), values.get(b()), values.get(c()));
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(transformation());
		}

		public static <A, B, C, X> Merge3<A, B, C, X> with(
			ValueSource<A> a,
			ValueSource<B> b,
			ValueSource<C> c,
			ValueSink<X> destination,
			F3<A, B, C, X> transformation
		) {
			return ImmutableMerge3.of(destination, a, b, c, transformation);
		}
	}

	@Immutable(builder = false)
	abstract static class Aggregated<S, X> implements Calculation<X>, HasHumanReadableLabel {
		@Parameter
		protected abstract List<ValueSource<S>> sourceList();

		@Parameter
		protected abstract F1<List<S>, X> aggregation();

		@Override
		public List<? extends ValueSource<?>> sources() {
			return sourceList();
		}

		@Override
		public X calculate(ValueLookup values) {
			List<S> sourceValues = sourceList().stream()
				.map(id -> values.get(id))
				.collect(Collectors.toList());
			return aggregation().apply(sourceValues);
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(aggregation());
		}

		public static <S, X> Aggregated<S, X> with(
			List<? extends ValueSource<S>> sourceList,
			ValueSink<X> destination,
			F1<List<S>, X> aggregation
		) {
			return ImmutableAggregated.of(destination, sourceList, aggregation);
		}
	}

	@Immutable
	static abstract class F1Explained<A,R> implements F1<A,R>, HasHumanReadableLabel {
		@Parameter
		protected abstract F1<A,R> delegate();
		@Parameter
		protected abstract String humanReadable();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a) {
			return delegate().apply(a);
		}

		@Override
		public String asHumanReadable() {
			return humanReadable();
		}
	}

	@Immutable
	static abstract class F2Explained<A,B,R> implements F2<A,B,R>, HasHumanReadableLabel {
		@Parameter
		protected abstract F2<A,B,R> delegate();
		@Parameter
		protected abstract String humanReadable();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b) {
			return delegate().apply(a,b);
		}

		@Override
		public String asHumanReadable() {
			return humanReadable();
		}

	}

	public static <A,R> F1<A,R> explained(F1<A,R> delegate, String explanation) {
		return ImmutableF1Explained.of(delegate, explanation);
	}

	public static <A,B,R> F2<A,B,R> explained(F2<A,B,R> delegate, String explanation) {
		return ImmutableF2Explained.of(delegate, explanation);
	}
}
