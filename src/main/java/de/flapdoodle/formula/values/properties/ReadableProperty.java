package de.flapdoodle.formula.values.properties;

import org.immutables.value.Value;

public interface ReadableProperty<O, T> {
	@Value.Auxiliary
	T get(O instance);
}
