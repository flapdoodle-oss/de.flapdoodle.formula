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
