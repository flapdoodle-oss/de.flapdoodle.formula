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

import de.flapdoodle.formula.AbstractHowToTest;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.*;
import de.flapdoodle.formula.calculate.calculations.Generated;
import de.flapdoodle.formula.calculate.calculations.Map1;
import de.flapdoodle.testdoc.Includes;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.*;

public class HowToCalculateTest extends AbstractHowToTest {

	@RegisterExtension
	public static Recording recording = Recorder.with("HowToCalculate.md", TabSize.spaces(2));

	@Test
	void fluentWay() {
		recording.begin("sample");
		ValueSource<Integer> valueA = Value.named("a", Integer.class);
		ValueSource<Integer> valueB = Value.named("b", Integer.class);
		ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

		Calculation<Integer> calculateSum = Calculate.value(valueSum)
			.using(valueA, valueB)
			.by((a, b) -> (a!=null && b!=null) ? a + b : null);

		Integer sum = calculateSum.calculate(valueLookup(
			MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
		));

		assertThat(sum).isEqualTo(3);
		recording.end();
	}

	@Test
	void basics() {
		recording.begin("destination");
		ValueSource<Integer> valueA = Value.named("a", Integer.class);
		ValueSink<Integer> valueResult = Value.named("result", Integer.class);

		Calculate.WithDestination<Integer> withDestination = Calculate.value(valueResult);
		recording.end();

		recording.begin("generating");
		Generated<Integer> generator = withDestination.by(() -> 2);
		assertThat(generator.calculate(valueLookup()))
				.isEqualTo(2);
		recording.end();

		recording.begin("sources");
		Map1<Integer, Integer> direct = withDestination.from(valueA);
		Calculate.WithMap1Nullable<Integer, Integer> usingA = withDestination.using(valueA);
		Calculate.WithMap1<Integer, Integer> requiringA = withDestination.requiring(valueA);
		recording.end();

		recording.begin("mapping");
		Calculation<Integer> calculation;

		calculation	= direct;
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1))))
			.isEqualTo(1);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null))))
			.isNull();

		calculation = usingA.by(a -> (a!=null) ? a + 1 : null);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1))))
			.isEqualTo(2);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null))))
			.isNull();

		calculation = usingA.ifAllSetBy(a -> a +1);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1))))
			.isEqualTo(2);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null))))
			.isNull();

		assertThat(requiringA.by(a -> a +1).calculate(valueLookup(MappedValue.of(valueA,1))))
			.isEqualTo(2);

		assertThatThrownBy(() -> {
			requiringA.by(a -> a + 1).calculate(valueLookup(MappedValue.of(valueA, null)));
		})
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining( "a(Integer) is null");
		recording.end();
	}

	@Test
	void customImplementation() {
		recording.include(SumCalculation.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.begin("sample");
		ValueSource<Integer> valueA = Value.named("a", Integer.class);
		ValueSource<Integer> valueB = Value.named("b", Integer.class);
		ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

		ImmutableSumCalculation calculateSum = SumCalculation.builder()
			.a(valueA)
			.b(valueB)
			.destination(valueSum)
			.build();

		Integer sum = calculateSum.calculate(valueLookup(
			MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
		));

		assertThat(sum).isEqualTo(3);
		recording.end();
	}

	@Test
	void fluentWithFunctionImplementation() {
		recording.include(SumFunction.class, Includes.WithoutPackage, Includes.WithoutImports, Includes.Trim);
		recording.begin("sample");
		ValueSource<Integer> valueA = Value.named("a", Integer.class);
		ValueSource<Integer> valueB = Value.named("b", Integer.class);
		ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

		Calculation<Integer> calculateSum = Calculate.value(valueSum)
			.using(valueA, valueB)
			.by(SumFunction.getInstance());

		Integer sum = calculateSum.calculate(valueLookup(
			MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
		));

		assertThat(sum).isEqualTo(3);
		recording.end();
	}

	@Test
	void nullChecks() {
		recording.begin("sample");
		ValueSource<Integer> valueA = Value.named("a", Integer.class);
		ValueSource<Integer> valueB = Value.named("b", Integer.class);
		ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

		Calculation<Integer> calculateSum = Calculate.value(valueSum)
			.requiring(valueA, valueB)
			.by((a, b) -> a + b, "a+b");

		Integer sum = calculateSum.calculate(valueLookup(
			MappedValue.of(valueA, 1), MappedValue.of(valueB, 2)
		));

		assertThatThrownBy(() -> calculateSum.calculate(valueLookup(
			MappedValue.of(valueA, null), MappedValue.of(valueB, 2)
		)))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("a+b: a(Integer) is null");
		
		assertThat(sum).isEqualTo(3);
		recording.end();
	}


	private static ValueLookup valueLookup(MappedValue<?> ... values) {
		return StrictValueLookup.of(values);
	}
}
