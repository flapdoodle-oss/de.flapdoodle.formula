package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.Rules;
import org.immutables.value.Value;

public interface HasRules {
	@Value.Auxiliary
	Rules addRulesTo(Rules rules);
}
