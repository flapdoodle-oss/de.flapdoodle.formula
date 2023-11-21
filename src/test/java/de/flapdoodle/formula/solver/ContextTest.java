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
package de.flapdoodle.formula.solver;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.ValidationError;
import de.flapdoodle.formula.values.Named;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContextTest {

	@Test
	void detectCollisions() {
		Named<String> foo = Value.named("foo", String.class);

		Context testee = Context.empty()
			.add(foo, "foo");

		assertThat(testee.isInvalid(foo)).isFalse();
		assertThat(testee.isValid(foo)).isTrue();
		assertThat(testee.getValidated(foo)).isEqualTo("foo");

		assertThatThrownBy(() -> testee.add(foo,"bar"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("already set to");

		assertThatThrownBy(() -> testee.addUnvalidated(foo,"bar"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("validated AND unvalidated");

		assertThatThrownBy(() -> testee.addInvalid(foo, ValidationError.of(Arrays.asList(
			ErrorMessage.of("kaboom")
		), Collections.emptySet())))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("validated AND invalid");
	}
}