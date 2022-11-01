package de.flapdoodle.formula.types;

import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class Either<L, R> {

	public abstract boolean isLeft();

	@Nullable
	public abstract L left();

	@Nullable
	public abstract R right();

	@Value.Immutable
	static abstract class Left<L, R> extends Either<L, R> {

		@Override
		@Value.Parameter
		@Nullable
		public abstract L left();

		@Value.Auxiliary
		@Override
		public R right() {
			throw new NoSuchElementException("is left");
		}
		@Override
		public boolean isLeft() {
			return true;
		}
	}

	@Value.Immutable
	static abstract class Right<L, R> extends Either<L, R> {

		@Override
		@Value.Parameter
		@Nullable
		public abstract R right();

		@Value.Auxiliary
		@Override
		public L left() {
			throw new NoSuchElementException("is right");
		}
		@Override
		public boolean isLeft() {
			return false;
		}
	}

	public <T> Either<T, R> mapLeft(Function<L, T> transformation) {
		return isLeft()
			? left(transformation.apply(left()))
			: (Either<T, R>) this;
	}

	public <T> Either<L, T> mapRight(Function<R, T> transformation) {
		return isLeft()
			? (Either<L, T>) this
			: right(transformation.apply(right()));
	}

	public <T> T map(Function<L, T> leftTransformation, Function<R, T> rightTransformation) {
		Either<T, T> mapped = mapLeft(leftTransformation).mapRight(rightTransformation);
		return mapped.isLeft() ? mapped.left() : mapped.right();
	}

	public static <L, R> Either<L, R> left(L left) {
		return ImmutableLeft.<L, R>of(left);
	}

	public static <L, R> Either<L, R> right(R right) {
		return ImmutableRight.<L, R>of(right);
	}
}
