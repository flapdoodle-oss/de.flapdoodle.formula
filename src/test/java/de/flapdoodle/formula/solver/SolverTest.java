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
import de.flapdoodle.formula.Rules;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.Validate;
import de.flapdoodle.formula.values.Attribute;
import de.flapdoodle.formula.values.Named;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class SolverTest {
	private final Attribute<DomainObject, Integer> sumProperty = DomainObject.property("sum", DomainObject::getSum, DomainObject::setSum);
	private final Attribute<DomainObject, Integer> aProperty = DomainObject.property("a", DomainObject::getA, DomainObject::setA);
	private final Attribute<DomainObject, Integer> bProperty = DomainObject.property("b", DomainObject::getB, DomainObject::setB);

	private final Named<Integer> sumValue = Value.named("sum", Integer.class);

	@Test
	void calculateSumAndSetResultInDomainObject() {
		DomainObject domainObject = new DomainObject();
		domainObject.setA(1);
		domainObject.setB(2);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> a + b),
				Calculate.value(sumProperty)
					.from(sumValue))
		);

		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph());
		System.out.println("------------------");
		System.out.println(dot);
		System.out.println("------------------");

		Context context = Solver.solve(Context.empty(), valueGraph, valueLookup(domainObject));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue);

		applyValidatedValues(domainObject, context);
		assertThat(domainObject.getSum()).isEqualTo(3);
	}

	@Test
	void validateBaseValue() {
		DomainObject domainObject = new DomainObject();
		domainObject.setA(1);
		domainObject.setB(2);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
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

		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph());
		System.out.println("------------------");
		System.out.println(dot);
		System.out.println("------------------");

		Context context = Solver.solve(Context.empty(), valueGraph, valueLookup(domainObject));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue, aProperty);

		applyValidatedValues(domainObject, context);
		assertThat(domainObject.getSum()).isEqualTo(3);
	}

	@Test
	void useUnvalidated() {
		DomainObject domainObject = new DomainObject();
		domainObject.setA(1);
		domainObject.setB(2);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
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
					.by(((validator, value, a) -> ImmutableList.of()))
			)
		);

		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph());
		System.out.println("------------------");
		System.out.println(dot);
		System.out.println("------------------");

		Context context = Solver.solve(Context.empty(), valueGraph, valueLookup(domainObject));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue, aProperty, bProperty);

		applyValidatedValues(domainObject, context);
		assertThat(domainObject.getSum()).isEqualTo(3);
	}

	@Test
	void ifValidationFailResultShouldNotSetInDomainObject() {
		DomainObject domainObject = new DomainObject();
		domainObject.setA(7);
		domainObject.setB(4);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
			.add(
				Calculate.value(sumValue)
					.using(aProperty, bProperty)
					.by((a, b) -> a + b),
				Calculate.value(sumProperty)
					.from(sumValue))
			.add(Validate.value(sumValue).by((validator, value) -> value.map(it -> (it > 10)
					? ImmutableList.of(ErrorMessage.of("to-big", it))
					: ImmutableList.<ErrorMessage>of())
				.orElse(ImmutableList.of(ErrorMessage.of("not-set")))))
		);

		Context context = Solver.solve(Context.empty(), valueGraph, valueLookup(domainObject));

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue);

		applyValidatedValues(domainObject, context);

		assertThat(context.hasValidationErrors(sumValue)).isTrue();
		assertThat(context.validationErrors(sumValue)).isNotNull()
			.containsExactly(ErrorMessage.of("to-big", 11));
		assertThat(context.hasValidationErrors(sumProperty)).isFalse();
		assertThat(domainObject.getSum()).isNull();
	}

	private static void applyValidatedValues(DomainObject domainObject, Context context) {
		context.validatedValues().keys().forEach(id -> {
			if (id instanceof Attribute) {
				Attribute<?, ?> maybeProperty = (Attribute<?, ?>) id;
				if (maybeProperty.isMatchingInstance(domainObject)) {
					Attribute<DomainObject, Object> objectProperty = (Attribute<DomainObject, Object>) maybeProperty;
					Object value = context.validatedValues().get(id);
					objectProperty.set(domainObject, value);
				}
			}
		});
	}

	private Solver.ValueLookup valueLookup(DomainObject domainObject) {
		return new Solver.ValueLookup() {
			@Override
			public <T> @Nullable T get(Value<T> id) {
				if (id instanceof Attribute) {
					Attribute<?, ?> maybeProperty = (Attribute<?, ?>) id;
					if (maybeProperty.isMatchingInstance(domainObject)) {
						Attribute<DomainObject, T> objectProperty = (Attribute<DomainObject, T>) maybeProperty;
						return objectProperty.get(domainObject);
					}
				}
				throw new IllegalArgumentException("could not get " + id);
			}
		};
	}

	static class DomainObject {
		private Integer a;
		private Integer b;
		private Integer sum;

		public Integer getA() {
			return a;
		}
		public void setA(Integer a) {
			this.a = a;
		}
		public Integer getB() {
			return b;
		}
		public void setB(Integer b) {
			this.b = b;
		}

		public Integer getSum() {
			return sum;
		}

		public void setSum(Integer sum) {
			this.sum = sum;
		}

		static <T> Attribute<DomainObject, T> property(String name, Function<DomainObject, T> getter,
			BiConsumer<DomainObject, T> setter) {
			return Attribute.of(DomainObject.class, name, getter, setter);
		}
	}
}