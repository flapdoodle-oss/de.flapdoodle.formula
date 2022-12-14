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
package de.flapdoodle.formula.explain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.calculate.MappedValue;
import de.flapdoodle.formula.calculate.StrictValueLookup;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.rules.Rules;
import de.flapdoodle.formula.solver.*;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.Validate;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.formula.values.Named;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RuleDependencyGraphTest {
	private Named<Integer> a = Value.named("a", Integer.class);
	private Named<Integer> b = Value.named("b", Integer.class);
	private Named<Integer> c = Value.named("c", Integer.class);
	private Named<Integer> x = Value.named("x", Integer.class);
	private Named<Integer> sum = Value.named("sum", Integer.class);
	private Named<Integer> mult = Value.named("mult", Integer.class);
	private Named<Integer> all = Value.named("all", Integer.class);

	@Test
	void dependencyGraphMustBeInRightOrder() {
		Rules rules = Rules.empty()
			.add(Calculate.value(all).using(x, mult).by((_x, _mult) -> (_x!=null && _mult!=null) ? _x*_mult : null, "x*mult"))
			.add(Calculate.value(sum).using(a, b).by((_a, _b) -> _a + _b,"a+b"))
			.add(Calculate.value(mult).using(a, c).by((_a,_c) -> _a * _c, "a*c"))
			.add(Validate.value(mult).using(sum).by((it, _sum) -> (it.get() < _sum.value())
				? Validation.error("fail")
				: Validation.noErrors(),"it<sum"));

		String dot = RuleDependencyGraph.explain(rules);
		System.out.println("---------------------");
		System.out.println(dot);
		System.out.println("---------------------");

//		RuleDependencyGraph.dependencyTreeFor(rules, mult);

		ValueGraph valueGraph = ValueDependencyGraphBuilder.build(rules);

		Result result = Solver.solve(valueGraph, StrictValueLookup.of(
			MappedValue.of(a,1),
			MappedValue.of(b,10),
			MappedValue.of(c,2),
			MappedValue.of(x,5)));

		Explanation explanation = valueGraph.explain(all);
		System.out.println(Explanation.render(explanation, value -> {
			return HasHumanReadableLabel.asHumanReadable(value) + " = " + result.valueOrError(value)
				.map(it -> ""+it, errors -> ""+errors.errorMessages().stream().map(errorMessage -> "error('"+errorMessage.key()+"')").collect(Collectors.joining(", ")));
		}));

		assertThat(result.get(sum)).isEqualTo(11);
		assertThat(result.get(mult)).isNull();
		assertThat(result.validationErrors().get(mult).errorMessages())
			.containsExactly(ErrorMessage.of("fail"));
	}
}