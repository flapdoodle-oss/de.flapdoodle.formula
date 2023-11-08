/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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