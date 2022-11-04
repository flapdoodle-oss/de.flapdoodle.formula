package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import org.immutables.value.Value.Auxiliary;

import java.util.Optional;

public interface ReadableValue<O, T> extends Value<T>, ValueSource<T> {
	@Auxiliary
	Optional<O> match(Object instance);

	@Auxiliary
	T get(O instance);
}
