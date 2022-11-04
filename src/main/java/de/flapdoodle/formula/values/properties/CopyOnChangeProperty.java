package de.flapdoodle.formula.values.properties;

import com.google.common.base.Preconditions;
import org.immutables.value.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value.Immutable
public abstract class CopyOnChangeProperty<O, T> implements ReadableProperty<O, T>, CopyOnWriteProperty<O, T> {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Value.Parameter
	protected abstract BiFunction<O, T, O> copyOnWrite();

	@Override
	public String toString() {
		return getClass().getSimpleName()+"{"+type().getSimpleName()+"."+name()+"}";
	}
	@Override
	@Value.Auxiliary
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Override
	@Value.Auxiliary
	public O change(O instance, T value) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return copyOnWrite().apply(instance, value);
	}

	@Value.Auxiliary
	public CopyOnChangeValue<O, T> matching(Matcher<O> matcher) {
		return CopyOnChangeValue.of(matcher, this);
	}

	public static <O, T> ImmutableCopyOnChangeProperty<O,T> of(Class<O> type, String name, Function<O, T> getter, BiFunction<O, T, O> copyOnWrite) {
		return ImmutableCopyOnChangeProperty.of(type, name, getter, copyOnWrite);
	}
}
