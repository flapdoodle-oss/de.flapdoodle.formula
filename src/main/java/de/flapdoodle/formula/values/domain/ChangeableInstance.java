package de.flapdoodle.formula.values.domain;

import org.immutables.value.Value;

public interface ChangeableInstance<O extends ChangeableInstance<O>> extends HasId<O> {
	@Value.Auxiliary
	<T> O change(ChangeableValue<?,T> id, T value);
}
