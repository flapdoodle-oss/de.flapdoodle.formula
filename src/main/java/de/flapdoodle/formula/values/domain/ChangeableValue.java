package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.ValueSink;
import org.immutables.value.Value;

public interface ChangeableValue<O, T> extends ValueSink<T>, ReadableValue<O, T>, HasId<O> {

	@Value.Auxiliary
	O change(O instance, T value);
}

