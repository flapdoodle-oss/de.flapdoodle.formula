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

import de.flapdoodle.formula.calculate.Calculate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static de.flapdoodle.formula.Value.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculationMapTest {

	@Test
	void calculationsByDestination() {
		CalculationMap testee = CalculationMap.empty().addAll(Arrays.asList(
			Calculate.value(named("a", String.class)).from(named("b", String.class)),
			Calculate.value(named("b", String.class)).from(named("c", String.class))
		));

		assertThatThrownBy(() -> testee.add(Calculate.value(named("a", String.class))
			.from(named("x", String.class))));

		assertThat(testee.contains(named("a", String.class)))
			.isTrue();
		assertThat(testee.get(named("a", String.class)))
			.isNotNull();
		assertThat(testee.get(named("b", String.class)).sources())
			.hasSize(1);
		assertThat(testee.contains(named("c", String.class))).isFalse();
		assertThat(testee.get(named("c", String.class))).isNull();

		assertThat(testee.contains(named("a", String.class)));
		assertThat(testee.keys())
			.containsExactlyInAnyOrder(
				named("a", String.class),
				named("b", String.class)
			);
	}

	@Test
	void detectKeyCollisions() {
		assertThatThrownBy(() -> CalculationMap.empty().addAll(Arrays.asList(
			Calculate.value(named("a", String.class)).from(named("b", String.class)),
			Calculate.value(named("a", String.class)).from(named("c", String.class))
		))).isInstanceOf(IllegalArgumentException.class);
	}

}