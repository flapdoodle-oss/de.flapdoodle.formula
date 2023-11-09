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
package de.flapdoodle.formula.types;

import de.flapdoodle.reflection.TypeInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TypeCounterTest {
	@Test
	void countTypes() {
		TypeCounter testee = new TypeCounter();

		assertThat(testee.count(TypeInfo.of(String.class))).isEqualTo(0);
		assertThat(testee.count(TypeInfo.of(String.class))).isEqualTo(1);
		assertThat(testee.count(TypeInfo.of(Integer.class))).isEqualTo(0);
	}
}