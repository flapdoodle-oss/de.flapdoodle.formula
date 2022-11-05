package de.flapdoodle.formula.values;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class Related<T, B> implements Value<T>, ValueSink<T>, ValueSource<T>, HasHumanReadableLabel {
	@Parameter
	protected abstract Value<T> destination();

	@Parameter
	protected abstract B reference();

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(destination()) + "->"+HasHumanReadableLabel.asHumanReadable(reference());
	}

	public static <T, B> Related<T, B> to(Value<T> destination, B base) {
		return ImmutableRelated.of(destination, base);
	}
}
