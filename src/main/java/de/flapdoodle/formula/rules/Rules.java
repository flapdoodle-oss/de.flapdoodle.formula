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
package de.flapdoodle.formula.rules;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.validation.Validation;
import org.immutables.value.Value;

@Value.Immutable(builder = false)
public abstract class Rules {
	@Value.Parameter
	public abstract CalculationMap calculations();
	@Value.Parameter
	public abstract ValidationMap validations();

	public ImmutableRules addCalculations(Iterable<? extends Calculation<?>> calculations) {
		return ImmutableRules.copyOf(this)
			.withCalculations(calculations().addAll(calculations));
	}

	public ImmutableRules add(Calculation<?> calculation) {
		return ImmutableRules.copyOf(this)
			.withCalculations(calculations().add(calculation));
	}

	public ImmutableRules add(Calculation<?> calculation, Calculation<?> ... other) {
		return addCalculations(Lists.asList(calculation, other));
	}

	public ImmutableRules addValidations(Iterable<? extends Validation<?>> validations) {
		return ImmutableRules.copyOf(this)
			.withValidations(validations().addAll(validations));
	}

	public ImmutableRules add(Validation<?> validation) {
		return ImmutableRules.copyOf(this)
			.withValidations(validations().add(validation));
	}

	public ImmutableRules add(Validation<?> validation, Validation<?> ... other) {
		return addValidations(Lists.asList(validation, other));
	}

	public ImmutableRules addRules(Iterable<? extends Rules> rules) {
		return ImmutableRules.of(
			calculations().merge(Iterables.transform(rules, Rules::calculations)),
			validations().merge(Iterables.transform(rules, Rules::validations))
		);
	}

	public ImmutableRules add(Rules first, Rules ... others) {
		return addRules(Lists.asList(first, others));
	}

	public static ImmutableRules empty() {
		return ImmutableRules.of(CalculationMap.empty(), ValidationMap.empty());
	}
}
