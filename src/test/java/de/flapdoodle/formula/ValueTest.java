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
import de.flapdoodle.formula.values.Related;
import de.flapdoodle.reflection.TypeInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValueTest {

	@Test
	void baseValueProperties() {
		Named<String> noName = Value.ofType(String.class);
		Named<String> otherNoName = Named.ofType(String.class);
		Named<String> thirdNoName = Named.ofType(TypeInfo.of(String.class));
		Named<String> fourthNoName = Value.ofType(TypeInfo.of(String.class));
		assertThat(noName).isNotNull();
		assertThat(noName).isEqualTo(otherNoName);
		assertThat(otherNoName).isEqualTo(thirdNoName);
		assertThat(thirdNoName).isEqualTo(fourthNoName);

		Named<String> foo = Value.named("foo", String.class);
		Named<String> otherFoo = Value.named("foo", String.class);
		Named<String> thirdFoo = Value.named("foo", TypeInfo.of(String.class));
		assertThat(foo).isNotNull();
		assertThat(foo).isEqualTo(otherFoo);
		assertThat(otherFoo).isEqualTo(thirdFoo);

		Related<String, String> relatedToBar = foo.relatedTo("bar");
		Related<String, String> otherRelatedToBar = Related.to(foo, "bar");
		assertThat(relatedToBar).isNotNull();
		assertThat(relatedToBar).isEqualTo(otherRelatedToBar);
		
		assertThat(relatedToBar).isNotEqualTo(foo);
	}
}