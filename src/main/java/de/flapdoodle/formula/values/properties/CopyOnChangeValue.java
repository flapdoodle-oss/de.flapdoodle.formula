package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class CopyOnChangeValue<O, T> implements ChangeableValue<O, T> {
	@Parameter
	protected abstract Matcher<O> matcher();
	@Parameter
	protected abstract CopyOnChangeProperty<O, T> property();

	@Override
	public <X> Maybe<CopyOnChangeValue<X, T>> matching(X instance) {
		return matcher().match(instance)
			? Maybe.some((CopyOnChangeValue<X, T>) this)
			: Maybe.none();
	}

	@Override
	public T get(O instance) {
		return property().get(instance);
	}

	@Override
	public O change(O instance, T value) {
		return property().change(instance, value);
	}

	public static <O, T> ImmutableCopyOnChangeValue<O, T> of(Matcher<O> matcher, CopyOnChangeProperty<O, T> property) {
		return ImmutableCopyOnChangeValue.of(matcher, property);
	}
}
