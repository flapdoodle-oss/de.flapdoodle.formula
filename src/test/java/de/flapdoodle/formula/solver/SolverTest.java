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
package de.flapdoodle.formula.solver;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.calculate.MappedValue;
import de.flapdoodle.formula.calculate.StrictValueLookup;
import de.flapdoodle.formula.rules.Rules;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.Validate;
import de.flapdoodle.formula.values.Named;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SolverTest {
	private final Named<Integer> sumProperty = Value.named("x.sum", Integer.class);
	private final Named<Integer> aProperty = Value.named("x.a", Integer.class);
	private final Named<Integer> bProperty = Value.named("x.b", Integer.class);
	private final Named<Integer> cProperty = Value.named("x.c", Integer.class);

	private final Named<Integer> sumValue = Value.named("sum", Integer.class);

	@Test
	void calculateSumAndSetResultInDomainObject() {
		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> a + b),
				Calculate.value(sumProperty)
					.from(sumValue))
		);

		Context context = Solver.solve(Context.empty(), valueGraph, StrictValueLookup.of(
			MappedValue.of(aProperty, 1),
			MappedValue.of(bProperty, 2)
		));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(aProperty, bProperty, sumProperty, sumValue);
		assertThat(context.getValidated(sumProperty)).isEqualTo(3);
	}

	@Test
	void validateBaseValue() {
		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> a + b),
				Calculate.value(sumProperty)
					.from(sumValue))
			.add(
				Validate.value(aProperty)
					.using(bProperty)
					.by((validator, value, b) -> ImmutableList.of())
			)
		);

		Context context = Solver.solve(Context.empty(), valueGraph, StrictValueLookup.of(
			MappedValue.of(aProperty, 1),
			MappedValue.of(bProperty, 2)
		));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue, aProperty, bProperty);
		assertThat(context.getValidated(sumProperty)).isEqualTo(3);
	}

	@Test
	void useUnvalidated() {
		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> a + b),
				Calculate.value(sumProperty)
					.from(sumValue))
			.add(
				Validate.value(aProperty)
					.using(bProperty)
					.by((validator, value, b) -> ImmutableList.of()),
				Validate.value(bProperty)
					.using(Value.unvalidated(aProperty))
					.by(((validator, value, a) -> validator.noErrors()))
			)
		);

		Context context = Solver.solve(Context.empty(), valueGraph, StrictValueLookup.of(
			MappedValue.of(aProperty, 1),
			MappedValue.of(bProperty, 2)
		));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue, aProperty, bProperty);
		assertThat(context.getValidated(sumProperty)).isEqualTo(3);
	}

	@Test
	void ifValidationFailResultShouldNotSetInDomainObject() {
		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> a + b),
				Calculate.value(sumProperty)
					.from(sumValue))
			.add(Validate.value(sumValue).by((validator, value) -> value.map(it -> (it > 10)
					? validator.error("to-big", it)
					: validator.noErrors())
				.orElse(validator.error("not-set"))))
		);

		Context context = Solver.solve(Context.empty(), valueGraph, StrictValueLookup.of(
			MappedValue.of(aProperty, 7),
			MappedValue.of(bProperty, 4)
		));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(aProperty, bProperty, sumProperty);
		assertThat(context.isInvalid(sumValue)).isTrue();
		assertThat(context.validationError(sumValue).errorMessages()).isNotNull()
			.containsExactly(ErrorMessage.of("to-big", 11));
		assertThat(context.isInvalid(sumProperty)).isFalse();
		assertThat(context.getValidated(sumProperty)).isNull();
	}

	@Test
	void detectInvalidSourceValueInValidation() {
		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> (a!=null && b!=null) ? a + b : null),
				Calculate.value(sumProperty)
					.from(sumValue))
			.add(Validate.value(aProperty).by((validator, value) -> validator.error("wrong")))
			.add(Validate.value(sumValue)
				.using(aProperty, cProperty)
				.by((validator, value, a, b) -> a.isValid()
					? validator.noErrors()
					: validator.error("source-invalid")))
		);

		Context context = Solver.solve(Context.empty(), valueGraph, StrictValueLookup.of(
			MappedValue.of(aProperty, 7),
			MappedValue.of(bProperty, 4),
			MappedValue.of(cProperty, null)
		));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(bProperty, cProperty, sumProperty);
		assertThat(context.isInvalid(sumValue)).isTrue();
		assertThat(context.validationError(sumValue).errorMessages()).isNotNull()
			.containsExactly(ErrorMessage.of("source-invalid"));
		assertThat(context.isInvalid(sumProperty)).isFalse();
		assertThat(context.getValidated(sumProperty)).isNull();
	}
}