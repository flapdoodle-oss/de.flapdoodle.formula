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
package de.flapdoodle.formula.validation.validations;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.StrictValidatedValueLookup;
import de.flapdoodle.formula.validation.ValidatedValue;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.formula.values.Named;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SelfTest {
	@Test
	void checkAttributes() {
		Named<Integer> destination = Value.named("dest", Integer.class);
		Named<Integer> valueA = Value.named("a", Integer.class);

		Self<Integer> testee = Self.with(destination, (it) -> Validation.error("error", it.get()));

		assertThat(testee.sources()).isEmpty();
		assertThat(testee.destination()).isEqualTo(destination);

		List<ErrorMessage> result = testee.validate(Optional.of(1), StrictValidatedValueLookup.with(
			Collections.emptyList()
		));

		assertThat(result).containsExactly(ErrorMessage.of("error", 1));
	}
}