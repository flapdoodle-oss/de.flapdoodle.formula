package de.flapdoodle.formula.values.properties;

import org.immutables.value.Value;

public interface CopyOnWriteProperty<O, T> {
	@Value.Auxiliary
	O change(O instance, T value);
}
