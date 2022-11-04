package de.flapdoodle.formula.values.properties;

import com.google.common.base.Preconditions;
import org.immutables.value.Value;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Value.Immutable
public abstract class ChangableProperty<O, T> implements ReadableProperty<O, T>, WritableProperty<O, T> {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Value.Parameter
	protected abstract BiConsumer<O, T> setter();

	@Override
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Override
	public void set(O instance, T value) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		setter().accept(instance, value);
	}

	public static <O, T> ImmutableChangableProperty<O,T> of(Class<O> type, String name, Function<O, T> getter, BiConsumer<O, T> setter) {
		return ImmutableChangableProperty.of(type, name, getter, setter);
	}
}
