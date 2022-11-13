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

class F2Test {

	@Test
	void withLabel() {
		F2<String, String, Integer> testee = F2.withLabel(new StringsToSum(), "label");
		assertThat(testee.apply("2","3")).isEqualTo(5);
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	static class StringsToSum implements F2<String, String, Integer> {

		@Nonnull @Override public Integer apply(@Nonnull String a, @Nonnull String b) {
			return Integer.valueOf(a) + Integer.valueOf(b);
		}

		@Override public String toString() {
			return StringsToSum.class.getSimpleName();
		}
	}

}