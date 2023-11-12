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
package de.flapdoodle.formula.calculate.functions;

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

class F0Test {

	@Test
	void withLabel() {
		F0<String> testee = F0.withLabel(new StringGenerator(), "label");
		assertThat(testee.get()).isEqualTo("boo");
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	static class StringGenerator implements F0<String> {

		@Nonnull @Override public String get() {
			return "boo";
		}

		@Override public String toString() {
			return StringGenerator.class.getSimpleName();
		}
	}
}