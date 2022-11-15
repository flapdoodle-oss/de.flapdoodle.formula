package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import javax.annotation.Nullable;

@Immutable
public abstract class ChangeableInstanceValueLookup<O extends ChangeableInstance<O>> implements ValueLookup {

	@Parameter
	protected abstract O instance();

	@Parameter
	protected abstract ValueLookup fallback();

	@Override
	public <T> @Nullable T get(Value<T> id) {
		if (id instanceof ReadableValue) {
			Maybe<? extends T> value = instance().findValue((ReadableValue<?, ? extends T>) id);
			if (value.hasSome()) return value.get();
		}
		return fallback().get(id);
	}

	public static <O extends ChangeableInstance<O>> ChangeableInstanceValueLookup<O> of(O instance, ValueLookup fallback) {
		return ImmutableChangeableInstanceValueLookup.of(instance, fallback);
	}
}
