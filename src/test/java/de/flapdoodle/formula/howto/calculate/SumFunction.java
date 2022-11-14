package de.flapdoodle.formula.howto.calculate;

import de.flapdoodle.formula.calculate.functions.FN2;

import javax.annotation.Nullable;

public class SumFunction implements FN2<Integer, Integer, Integer> {
	private SumFunction() {
		// no instance
	}

	@Nullable @Override
	public Integer apply(@Nullable Integer a, @Nullable Integer b) {
		return (a != null && b != null)
			? a + b
			: null;
	}

	private final static SumFunction INSTANCE=new SumFunction();

	public static SumFunction getInstance() {
		return INSTANCE;
	}
}
