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
package de.flapdoodle.formula.validation;

import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.validation.validations.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static de.flapdoodle.formula.Value.named;
import static org.assertj.core.api.Assertions.assertThat;

class ValidateTest {
	@Nested
	class SelfTests {
		ValueSink<String> destination = named("destination", String.class);

		@Test
		void valueBy() {
			Self<String> testee = Validate.value(destination).by(it -> Validation.error("error", it.get()));

			assertThat(testee.sources()).isEmpty();
			assertThat(testee.destination()).isEqualTo(destination);
		}

		@Test
		void valueByWithLabel() {
			Self<String> testee = Validate.value(destination).by(it -> Validation.error("error", it.get()),"label");

			assertThat(testee.sources()).isEmpty();
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");
		}
	}

	@Nested
	class RelatedTo1Tests {
		ValueSource<Integer> valueA = named("a", Integer.class);
		ValueSink<String> destination = named("destination", String.class);

		@Test
		void valueBy() {
			RelatedTo1<String, Integer> testee = Validate.value(destination).using(valueA)
				.by((it, a) -> Validation.error("error", it.get(), a.value()));

			assertThat(testee.sources()).containsExactly(valueA);
			assertThat(testee.destination()).isEqualTo(destination);
		}

		@Test
		void valueByWithLabel() {
			RelatedTo1<String, Integer> testee = Validate.value(destination).using(valueA)
				.by((it, a) -> Validation.error("error", it.get(), a.value()), "label");

			assertThat(testee.sources()).containsExactly(valueA);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");
		}
	}

	@Nested
	class RelatedTo2Tests {
		ValueSource<Integer> valueA = named("a", Integer.class);
		ValueSource<Integer> valueB = named("b", Integer.class);
		ValueSink<String> destination = named("destination", String.class);

		@Test
		void valueBy() {
			RelatedTo2<String, Integer, Integer> testee = Validate.value(destination).using(valueA, valueB)
				.by((it, a, b) -> Validation.error("error", it.get(), a.value(), b.value()));

			assertThat(testee.sources()).containsExactly(valueA, valueB);
			assertThat(testee.destination()).isEqualTo(destination);
		}

		@Test
		void valueByWithLabel() {
			RelatedTo2<String, Integer, Integer> testee = Validate.value(destination).using(valueA, valueB)
				.by((it, a, b) -> Validation.error("error", it.get(), a.value(), b.value()), "label");

			assertThat(testee.sources()).containsExactly(valueA, valueB);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");
		}
	}

	@Nested
	class RelatedTo3Tests {
		ValueSource<Integer> valueA = named("a", Integer.class);
		ValueSource<Integer> valueB = named("b", Integer.class);
		ValueSource<Integer> valueC = named("c", Integer.class);
		ValueSink<String> destination = named("destination", String.class);

		@Test
		void valueBy() {
			RelatedTo3<String, Integer, Integer, Integer> testee = Validate.value(destination)
				.using(valueA, valueB, valueC)
				.by((it, a, b, c) -> Validation.error("error", it.get(), a.value(), b.value(), c.value()));

			assertThat(testee.sources()).containsExactly(valueA, valueB, valueC);
			assertThat(testee.destination()).isEqualTo(destination);
		}

		@Test
		void valueByWithLabel() {
			RelatedTo3<String, Integer, Integer, Integer> testee = Validate.value(destination).using(valueA, valueB, valueC)
				.by((it, a, b, c) -> Validation.error("error", it.get(), a.value(), b.value(), c.value()), "label");

			assertThat(testee.sources()).containsExactly(valueA, valueB, valueC);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");
		}
	}

	@Nested
	class RelatedTo4Tests {
		ValueSource<Integer> valueA = named("a", Integer.class);
		ValueSource<Integer> valueB = named("b", Integer.class);
		ValueSource<Integer> valueC = named("c", Integer.class);
		ValueSource<Integer> valueD = named("d", Integer.class);
		ValueSink<String> destination = named("destination", String.class);

		@Test
		void valueBy() {
			RelatedTo4<String, Integer, Integer, Integer, Integer> testee = Validate.value(destination)
				.using(valueA, valueB, valueC, valueD)
				.by((it, a, b, c, d) -> Validation.error("error", it.get(), a.value(), b.value(), c.value(), d.value()));

			assertThat(testee.sources()).containsExactly(valueA, valueB, valueC, valueD);
			assertThat(testee.destination()).isEqualTo(destination);
		}

		@Test
		void valueByWithLabel() {
			RelatedTo4<String, Integer, Integer, Integer, Integer> testee = Validate.value(destination).using(valueA, valueB, valueC, valueD)
				.by((it, a, b, c, d) -> Validation.error("error", it.get(), a.value(), b.value(), c.value(), d.value()), "label");

			assertThat(testee.sources()).containsExactly(valueA, valueB, valueC, valueD);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");
		}
	}
}