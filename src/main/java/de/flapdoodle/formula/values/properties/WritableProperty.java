package de.flapdoodle.formula.values.properties;

import org.immutables.value.Value;

public interface WritableProperty<O, T> {

	@Value.Auxiliary
	void set(O instance, T value);
}
