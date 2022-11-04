package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import java.util.Optional;

@Immutable
public abstract class CopyOnChangeValue<O, T> implements ReadableValue<O, T>, ValueSink<T> {
	@Parameter
	protected abstract Matcher<O> matcher();
	@Parameter
	protected abstract CopyOnChangeProperty<O, T> property();

	@Override
	public Optional<O> match(Object instance) {
		return matcher().match(instance);
	}

	@Override
	public T get(O instance) {
		return property().get(instance);
	}

	public static <O, T> ImmutableCopyOnChangeValue<O, T> of(Matcher<O> matcher, CopyOnChangeProperty<O, T> property) {
		return ImmutableCopyOnChangeValue.of(matcher, property);
	}
}
