package de.flapdoodle.formula.types;

import org.immutables.value.Value;

public interface HasHumanReadableLabel {
	@Value.Lazy
	String asHumanReadable();

	static String asHumanReadable(Object instance) {
		if (instance instanceof HasHumanReadableLabel) {
			return ((HasHumanReadableLabel) instance).asHumanReadable();
		}
		return instance.toString();
	}
}
