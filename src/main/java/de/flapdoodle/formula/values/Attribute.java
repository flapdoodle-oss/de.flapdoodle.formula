package de.flapdoodle.formula.values;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import org.immutables.value.Value.Immutable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Immutable
public abstract class Attribute<O, T> implements Value<T>, ValueSource<T>, ValueSink<T> {
	protected abstract Class<O> objectType();

	protected abstract String name();

	protected abstract Function<O, T> getter();

	protected abstract BiConsumer<O, T> setter();

	public boolean isMatchingInstance(Object instance) {
		return objectType().isInstance(instance);
	}

	public T get(O domainObject) {
		return getter().apply(domainObject);
	}

	public void set(O domainObject, T value) {
		setter().accept(domainObject, value);
	}

	@Override
	public String toString() {
		return "Attribute{" + objectType() + "." + name() + '}';
	}

	public static <T, O> Attribute<O, T> of(Class<O> objectType, String name, Function<O, T> getter, BiConsumer<O, T> setter) {
		return ImmutableAttribute.<O, T>builder()
			.objectType(objectType)
			.name(name)
			.getter(getter)
			.setter(setter)
			.build();
	}
}
