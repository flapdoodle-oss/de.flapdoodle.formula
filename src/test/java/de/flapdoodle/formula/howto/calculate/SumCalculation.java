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
package de.flapdoodle.formula.howto.calculate;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.calculate.ValueLookup;
import org.immutables.value.Value.Immutable;

import java.util.Arrays;
import java.util.List;

@Immutable
public abstract class SumCalculation implements Calculation<Integer> {
	public abstract ValueSource<Integer> a();
	public abstract ValueSource<Integer> b();

	@Override
	public abstract Value<Integer> destination();

	@Override
	@org.immutables.value.Value.Lazy
	public List<? extends ValueSource<?>> sources() {
		return Arrays.asList(a(), b());
	}

	@Override
	public Integer calculate(ValueLookup values) {
		Integer a = values.get(a());
		Integer b = values.get(b());
		return (a != null && b != null)
			? a + b
			: null;
	}

	public static ImmutableSumCalculation.Builder builder() {
		return ImmutableSumCalculation.builder();
	}
}
