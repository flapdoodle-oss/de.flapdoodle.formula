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
import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FN0Test {

	@Test
	void withLabel() {
		FN0<String> testee = FN0.withLabel(new NullableStringGenerator(false), "label");
		assertThat(testee.get()).isEqualTo("boo");
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	@Test
	void checkNull() {
		assertThat(FN0.checkNull(new StringGenerator(false)).get())
				.isEqualTo("boo");

		assertThatThrownBy(() -> FN0.checkNull(new StringGenerator(true)).get())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringGenerator: result is null");
	}

	static class StringGenerator implements F0<String> {
		private final boolean nullValue;

		public StringGenerator(boolean nullValue) {
			this.nullValue = nullValue;
		}

		@Nonnull @Override public String get() {
			return nullValue ? null : "boo";
		}
		@Override public String toString() {
			return StringGenerator.class.getSimpleName();
		}
	}

	static class NullableStringGenerator implements FN0<String> {
		private final boolean nullValue;

		public NullableStringGenerator(boolean nullValue) {
			this.nullValue = nullValue;
		}

		@Nullable @Override public String get() {
			return nullValue ? null : "boo";
		}
		@Override public String toString() {
			return NullableStringGenerator.class.getSimpleName();
		}
	}
}