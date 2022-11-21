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

class FN3Test {

	@Test
	void withLabel() {
		FN3<String, String, String, Integer> testee = FN3.withLabel(new NullableStringsToSum(), "label");
		assertThat(testee.apply("2", "3","4")).isEqualTo(9);
		assertThat(testee.apply(null, "x","y")).isNull();
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	@Test
	void mapOnlyIfNotNull() {
		FN3<String, String, String, Integer> testee = FN3.mapOnlyIfNotNull(new StringsToSum());
		assertThat(testee.apply("2","3","4")).isEqualTo(9);
		assertThat(testee.apply(null, "x", "y")).isNull();
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("StringsToSum");
	}

	@Test
	void checkNull() {
		FN3<String, String, String, Integer> testee = FN3.checkNull(new StringsToSum(),"a", "b", "c");
		assertThat(testee.apply("2","3", "4")).isEqualTo(9);
		assertThatThrownBy(() -> testee.apply(null,"x", "y"))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: a is null");
		assertThatThrownBy(() -> testee.apply("x",null, "y"))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: b is null");
		assertThatThrownBy(() -> testee.apply("x","y", null))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: c is null");
	}

	static class StringsToSum implements F3<String, String, String, Integer> {
		@Nonnull @Override public Integer apply(@Nonnull String a, @Nonnull String b, @Nonnull String c) {
			return Integer.parseInt(a) + Integer.parseInt(b) + Integer.parseInt(c);
		}
		@Override public String toString() {
			return StringsToSum.class.getSimpleName();
		}
	}

	static class NullableStringsToSum implements FN3<String, String, String, Integer> {
		@Nullable @Override public Integer apply(@Nullable String a, @Nullable String b, @Nullable String c) {
			return (a!=null && b!=null && c!=null) ? Integer.parseInt(a) + Integer.parseInt(b) + Integer.parseInt(c) : null;
		}
		@Override public String toString() {
			return NullableStringsToSum.class.getSimpleName();
		}
	}
}