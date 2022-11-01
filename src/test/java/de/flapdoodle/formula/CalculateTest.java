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

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateTest {
	@Test
	void calculateValueFromSourceGivesUnmappedSourceValue() {
		ValueSink<String> destination = Value.named("dest", String.class);
		ValueSource<String> source = Value.named("source", String.class);

		Calculation.Direct<String, String> calculation = Calculate.value(destination).from(source);

		assertThat(calculation.sources()).containsExactly(source);
		assertThat(calculation.destination()).isEqualTo(destination);
		assertThat(calculation.calculate(valueResolver(ImmutableMap.of(source, "expected"))))
			.isEqualTo("expected");
	}

	@Test
	void calculateValueFrom1MappedSource() {
		ValueSink<String> destination = Value.named("dest", String.class);
		ValueSource<Integer> source = Value.named("source", Integer.class);

		Calculation.Direct<Integer, String> calculation = Calculate.value(destination)
			.using(source)
			.by(it -> it.toString());

		assertThat(calculation.sources()).containsExactly(source);
		assertThat(calculation.destination()).isEqualTo(destination);
		assertThat(calculation.calculate(valueResolver(ImmutableMap.of(source, 1))))
			.isEqualTo("1");
	}

	@Test
	void calculateValueFrom2MappedSource() {
		ValueSink<String> destination = Value.named("dest", String.class);
		ValueSource<Integer> source_a = Value.named("a", Integer.class);
		ValueSource<Integer> source_b = Value.named("b", Integer.class);

		Calculation.Merge2<Integer, Integer, String> calculation = Calculate.value(destination)
			.using(source_a, source_b)
			.by((a, b) -> "a: " + a + ", b: " + b);

		assertThat(calculation.sources()).containsExactly(source_a, source_b);
		assertThat(calculation.destination()).isEqualTo(destination);
		assertThat(calculation.calculate(valueResolver(ImmutableMap.of(source_a, 1, source_b, 2))))
			.isEqualTo("a: 1, b: 2");
	}

	@Test
	void calculateValueFrom3MappedSource() {
		ValueSink<String> destination = Value.named("dest", String.class);
		ValueSource<Integer> source_a = Value.named("a", Integer.class);
		ValueSource<Integer> source_b = Value.named("b", Integer.class);
		ValueSource<Integer> source_c = Value.named("c", Integer.class);

		Calculation.Merge3<Integer, Integer, Integer, String> calculation = Calculate.value(destination)
			.using(source_a, source_b, source_c)
			.by((a, b, c) -> "a: " + a + ", b: " + b + ", c: " + c);

		assertThat(calculation.sources()).containsExactly(source_a, source_b, source_c);
		assertThat(calculation.destination()).isEqualTo(destination);
		assertThat(calculation.calculate(valueResolver(ImmutableMap.of(source_a, 1, source_b, 2, source_c, 3))))
			.isEqualTo("a: 1, b: 2, c: 3");
	}

	static Calculation.ValueLookup valueResolver(Map<Value<?>, ?> map) {
		return new Calculation.ValueLookup() {
			@Override
			public <T> T get(Value<T> id) {
				return (T) map.get(id);
			}
		};
	}

}