package de.flapdoodle.formula.values.properties;

import com.google.common.base.Preconditions;
import org.immutables.value.Value;

import java.util.function.Function;

@Value.Immutable
public abstract class ReadOnlyProperty<O, T> implements ReadableProperty<O, T> {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Override public String toString() {
		return getClass().getSimpleName()+"{"+type().getSimpleName()+"."+name()+"}";
	}

	@Override
	@Value.Auxiliary
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Value.Auxiliary
	public ReadOnlyValue<O, T> matching(Matcher<O> matcher) {
		return ReadOnlyValue.of(matcher, this);
	}

	public static <O, T> ImmutableReadOnlyProperty<O,T> of(Class<O> type, String name, Function<O, T> getter) {
		return ImmutableReadOnlyProperty.of(type, name, getter);
	}
}
