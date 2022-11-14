package de.flapdoodle.formula.howto.calculate;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.*;
import de.flapdoodle.formula.calculate.calculations.Map1;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class HowToCalculateTest {

	@Test
	void basics() {
		ValueSource<Integer> valueA = Value.named("a", Integer.class);
		ValueSink<Integer> valueSum = Value.named("sum", Integer.class);

		Calculate.WithDestination<Integer> withDestination = Calculate.value(valueSum);

		Map1<Integer, Integer> direct = withDestination.from(valueA);
		Calculate.WithMap1Nullable<Integer, Integer> usingA = withDestination.using(valueA);
		Calculate.WithMap1<Integer, Integer> requiringA = withDestination.requiring(valueA);

		Calculation<Integer> calculation;

		calculation	= direct;
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1)))).isEqualTo(1);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null)))).isNull();

		calculation = usingA.by(a -> (a!=null) ? a + 1 : null);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1)))).isEqualTo(2);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null)))).isNull();

		calculation = usingA.ifAllSetBy(a -> a +1);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1)))).isEqualTo(2);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,null)))).isNull();

		calculation = requiringA.by(a -> a +1);
		assertThat(calculation.calculate(valueLookup(MappedValue.of(valueA,1)))).isEqualTo(2);
		try {
			calculation.calculate(valueLookup(MappedValue.of(valueA,null)));
			fail("should not reach this point");
		} catch (NullPointerException nx) {
			assertThat(nx.getLocalizedMessage()).contains("a is null");
		}
	}

	@Test
	void fluentWay() {
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
	}

	@Test
	void customImplementation() {
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
	}

	@Test
	void fluentWithFunctionImplementation() {
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
	}

	@Test
	void nullChecks() {
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
			.hasMessage("a+b: a is null");
		
		assertThat(sum).isEqualTo(3);
	}


	private static ValueLookup valueLookup(MappedValue<?> ... values) {
		return StrictValueLookup.of(values);
	}
}
