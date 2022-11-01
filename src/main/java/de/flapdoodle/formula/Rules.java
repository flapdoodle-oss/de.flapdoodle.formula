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
