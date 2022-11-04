package de.flapdoodle.formula.values.properties;

import de.flapdoodle.formula.types.Maybe;

public interface Matcher<O> {
	boolean match(Object instance);
}
