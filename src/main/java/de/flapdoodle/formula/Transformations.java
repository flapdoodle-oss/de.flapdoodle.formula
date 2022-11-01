package de.flapdoodle.formula;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Transformations {
	static <X> F1<X, X> identity() {
		return a -> a;
	}

	@FunctionalInterface
	interface F1<A, R> extends Function<A, R> {
		@Override @Nullable R apply(@Nullable A a);
	}

	@FunctionalInterface
	interface F2<A, B, R> extends BiFunction<A, B, R> {
		@Override @Nullable R apply(@Nullable A a,@Nullable B b);
	}

	@FunctionalInterface
	interface F3<A, B, C, R> {
		@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c);
	}
}
