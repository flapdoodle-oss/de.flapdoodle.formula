package de.flapdoodle.formula.rules;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.values.Named;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RulesTest {

	@Test
	void checkIfApiWorksAsExpected() {
		Named<String> foo = Value.named("foo", String.class);
		Named<String> bar = Value.named("bar", String.class);
		Named<Integer> number = Value.named("number", Integer.class);

		Rules rules = Rules.empty()
			.add(Calculate.value(foo)
				.using(bar, number)
				.ifAllSetBy((s, i) -> s + i));

		Calculation<String> calculation = rules.calculations().get(foo);
		assertThat(calculation).isNotNull();

		assertThat(calculation.sources())
			.asInstanceOf(InstanceOfAssertFactories.collection(Named.class))
			.containsExactlyInAnyOrder(bar, number);
	}
}