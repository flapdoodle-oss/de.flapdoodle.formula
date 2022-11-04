package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value.Auxiliary;

public interface ReadableValue<O, T> extends Value<T>, ValueSource<T> {

	@Auxiliary
	<X> Maybe<? extends ReadableValue<X, T>> matching(X instance);

	@Auxiliary
	T get(O instance);

}
