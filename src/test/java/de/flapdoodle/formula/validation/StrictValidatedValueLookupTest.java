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
package de.flapdoodle.formula.validation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static de.flapdoodle.formula.Value.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StrictValidatedValueLookupTest {
	@Test
	void canHaveNullValues() {
		StrictValidatedValueLookup testee = StrictValidatedValueLookup.with(Arrays.asList(
			ValidatedValue.of(named("a", String.class), "Foo"),
			ValidatedValue.of(named("b", String.class), (String) null),
			ValidatedValue.of(named("c", String.class), ValidationError.of(Arrays.asList(ErrorMessage.of("fail")), Collections.emptySet()))
		));

		assertThat(testee.get(named("a", String.class)).value()).isEqualTo("Foo");
		assertThat(testee.get(named("b", String.class)).value()).isNull();
		assertThat(testee.get(named("c", String.class)).errors()).containsExactly(ErrorMessage.of("fail"));

		assertThatThrownBy(() -> testee.get(named("d", String.class)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void detectKeyCollisions() {
		assertThatThrownBy(() -> StrictValidatedValueLookup.with(Arrays.asList(
			ValidatedValue.of(named("a", String.class), "Foo"),
			ValidatedValue.of(named("b", String.class), (String) null),
			ValidatedValue.of(named("a", String.class), (String) null)
		))).isInstanceOf(IllegalArgumentException.class);
	}

}