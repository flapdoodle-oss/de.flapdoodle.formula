package de.flapdoodle.formula.values.properties;

import java.util.Optional;

public interface Matcher<O> {
	Optional<O> match(Object instance);
}
