package de.flapdoodle.formula.calculate;

import de.flapdoodle.formula.Value;

import java.util.Set;

public interface HasSetOfKnownValues {
	@org.immutables.value.Value.Lazy
	Set<Value<?>> keySet();
}
