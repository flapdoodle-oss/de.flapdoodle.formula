package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.types.Id;

public interface HasId <T> {
	Id<T> id();
}
