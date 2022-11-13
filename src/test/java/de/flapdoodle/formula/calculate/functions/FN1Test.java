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

class FN1Test {

	@Test
	void identity() {
		Object src = new Object();

		assertThat(FN1.identity().apply(src)).isSameAs(src);
		assertThat(FN1.identity().apply(null)).isNull();
	}

	@Test
	void withLabel() {
		FN1<String, Integer> testee = FN1.withLabel(new NullableStringToInt(), "label");
		assertThat(testee.apply("2")).isEqualTo(2);
		assertThat(testee.apply(null)).isNull();
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	@Test
	void mapOnlyIfNotNull() {
		FN1<String, Integer> testee = FN1.mapOnlyIfNotNull(new StringToInt());
		assertThat(testee.apply("2")).isEqualTo(2);
		assertThat(testee.apply(null)).isNull();
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("StringToInt");
	}

	@Test
	void checkNull() {
		FN1<String, Integer> testee = FN1.checkNull(new StringToInt());
		assertThat(testee.apply("2")).isEqualTo(2);
		assertThatThrownBy(() -> testee.apply(null))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringToInt: a is null");
	}

	static class StringToInt implements F1<String, Integer> {
		@Nonnull @Override public Integer apply(@Nonnull String s) {
			return Integer.valueOf(s);
		}
		@Override public String toString() {
			return StringToInt.class.getSimpleName();
		}
	}

	static class NullableStringToInt implements FN1<String, Integer> {
		@Nullable @Override public Integer apply(@Nullable String s) {
			return s!=null ? Integer.valueOf(s) : null;
		}
		@Override public String toString() {
			return NullableStringToInt.class.getSimpleName();
		}
	}
}