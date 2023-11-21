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
package de.flapdoodle.formula;

import de.flapdoodle.formula.values.Named;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueContainerTest {

	@Test
	void addedValueCanBeGet() {
		Named<String> foo = Value.ofType(String.class);
		Named<Integer> bar = Value.named("bar", Integer.class);

		ValueContainer testee = ValueContainer.empty()
			.add(foo, "foo")
			.add(bar, null);

		assertThat(testee.get(foo))
			.isEqualTo("foo");

		assertThat(testee.get(bar))
			.isNull();

		assertThat(testee.keys())
			.containsExactly(foo, bar);
	}

	@Test
	void canNotAddAValueTwice() {
		Named<String> foo = Value.ofType(String.class);
		Named<Integer> bar = Value.ofType(Integer.class);

		ValueContainer testee = ValueContainer.empty()
			.add(foo, "foo")
			.add(bar, null);

		assertThatThrownBy(() -> testee.add(foo, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("already set to");

		assertThatThrownBy(() -> testee.add(bar, 2))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("already set to");
	}
}