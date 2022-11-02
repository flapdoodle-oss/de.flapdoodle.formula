package de.flapdoodle.formula.calculate;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
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
	abstract static class Direct<A, X> implements Calculation<X> {
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

		public static <A, X> Direct<A, X> with(
			ValueSource<A> source,
			ValueSink<X> destination,
			F1<A, X> transformation
		) {
			return ImmutableDirect.of(destination, source, transformation);
		}
	}

	@Immutable(builder = false)
	abstract static class Merge2<A, B, X> implements Calculation<X> {
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
	abstract static class Merge3<A, B, C, X> implements Calculation<X> {
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

	// TODO is this of any use?
	@Immutable(builder = false)
	abstract static class Aggregated<X> implements Calculation<List<X>> {
		@Parameter
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
