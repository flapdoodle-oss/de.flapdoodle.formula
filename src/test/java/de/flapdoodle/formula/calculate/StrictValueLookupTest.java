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
package de.flapdoodle.formula.calculate;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static de.flapdoodle.formula.Value.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StrictValueLookupTest {

	@Test
	void canHaveNullValues() {
		StrictValueLookup testee = StrictValueLookup.of(Arrays.asList(
			MappedValue.of(named("a", String.class), "Foo"),
			MappedValue.of(named("b", String.class), null)
		));

		assertThat(testee.get(named("a", String.class))).isEqualTo("Foo");
		assertThat(testee.get(named("b", String.class))).isNull();

		assertThatThrownBy(() -> testee.get(named("c", String.class)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void detectKeyCollisions() {
		assertThatThrownBy(() -> StrictValueLookup.of(Arrays.asList(
			MappedValue.of(named("a", String.class), "Foo"),
			MappedValue.of(named("b", String.class), null),
			MappedValue.of(named("a", String.class), null)
		))).isInstanceOf(IllegalArgumentException.class);
	}


	@Test
	void keySetMustContainAllOtherKeys() {
		StrictValueLookup testee = StrictValueLookup.of(Arrays.asList(
			MappedValue.of(named("a", String.class), "Foo"),
			MappedValue.of(named("b", Integer.class), 2),
			MappedValue.of(named("c", String.class), null),
			MappedValue.of(named("d", Integer.class), null)
		));

		assertThat(testee.keySet())
			.containsAll(testee.values().keySet());

		assertThat(testee.keySet())
			.containsAll(testee.nullValues());
	}
}