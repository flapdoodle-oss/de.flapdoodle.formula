package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value;

public interface ChangeableValue<O, T> extends ValueSink<T>, ReadableValue<O, T> {

	@Override
	@Value.Auxiliary <X> Maybe<? extends ChangeableValue<X, T>> matching(X instance);

	O change(O instance, T value);
}
