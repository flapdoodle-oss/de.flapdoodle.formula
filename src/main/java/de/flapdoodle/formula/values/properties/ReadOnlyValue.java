package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class ReadOnlyValue<O, T> implements ReadableValue<O, T> {
	@Parameter
	protected abstract Matcher<O> matcher();
	@Parameter
	protected abstract ReadOnlyProperty<O, T> property();

	@Override
	public <X> Maybe<ReadOnlyValue<X, T>> matching(X instance) {
		return matcher().match(instance)
			? Maybe.some((ReadOnlyValue<X, T>) this)
			: Maybe.none();
	}

	@Override
	@Value.Auxiliary
	public T get(O instance) {
		return property().get(instance);
	}

	public static <O, T> ImmutableReadOnlyValue<O, T> of(Matcher<O> matcher, ReadOnlyProperty<O, T> property) {
		return ImmutableReadOnlyValue.of(matcher, property);
	}
}
