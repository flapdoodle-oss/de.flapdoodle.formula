package de.flapdoodle.formula.values.properties;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import java.util.Optional;

@Immutable
public abstract class ReadOnlyValue<O, T> implements ReadableValue<O, T> {
	@Parameter
	protected abstract Matcher<O> matcher();
	@Parameter
	protected abstract ReadOnlyProperty<O, T> property();

	@Override
	@Value.Auxiliary
	public Optional<O> match(Object instance) {
		return matcher().match(instance);
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
