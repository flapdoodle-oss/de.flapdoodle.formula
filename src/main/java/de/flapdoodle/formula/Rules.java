package de.flapdoodle.formula;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable(builder = false)
public abstract class Rules {
	@Value.Parameter
	public abstract CalculationMap calculations();
	@Value.Parameter
	public abstract ValidationMap validations();

	public ImmutableRules add(Calculation<?> calculation) {
		return ImmutableRules.copyOf(this)
			.withCalculations(calculations().add(calculation));
	}

	public ImmutableRules addCalculations(List<Calculation<?>> calculations) {
		return ImmutableRules.copyOf(this)
			.withCalculations(calculations().addAll(calculations));
	}

	public ImmutableRules add(Validation<?> validation) {
		return ImmutableRules.copyOf(this)
			.withValidations(validations().add(validation));
	}

	public ImmutableRules addValidations(List<Validation<?>> validations) {
		return ImmutableRules.copyOf(this)
			.withValidations(validations().addAll(validations));
	}

	public static ImmutableRules empty() {
		return ImmutableRules.of(CalculationMap.empty(), ValidationMap.empty());
	}
}
