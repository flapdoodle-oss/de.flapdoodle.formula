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
import de.flapdoodle.formula.*;
import de.flapdoodle.formula.calculate.Calculate;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.validation.Validate;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class SolverTest {
	private final ObjectProperty<DomainObject, Integer> sumProperty = DomainObject.property("sum", DomainObject::getSum, DomainObject::setSum);
	private final ObjectProperty<DomainObject, Integer> aProperty = DomainObject.property("a", DomainObject::getA, DomainObject::setA);
	private final ObjectProperty<DomainObject, Integer> bProperty = DomainObject.property("b", DomainObject::getB, DomainObject::setB);

	private final Value.Named<Integer> sumValue = Value.named("sum", Integer.class);

	@Test
	void calculateSumAndSetResultInDomainObject() {
		DomainObject domainObject=new DomainObject();
		domainObject.setA(1);
		domainObject.setB(2);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
			.add(Calculate.value(sumValue)
				.using(aProperty, bProperty)
				.by((a,b) -> a+b))
			.add(Calculate.value(sumProperty)
				.from(sumValue))
		);

		String dot = GraphRenderer.renderGraphAsDot(valueGraph.graph(), Object::toString);
		System.out.println("------------------");
		System.out.println(dot);
		System.out.println("------------------");

		Solver testee = Solver.builder()
			.calculationLookupFactory(context -> calculationLookup(context, domainObject))
			.build();

		Context context = testee.solve(valueGraph);

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue);

		context.validatedValues().keys().forEach(id -> {
			if (id instanceof ObjectProperty) {
				ObjectProperty<?,?> maybeProperty = (ObjectProperty<?, ?>) id;
				if (maybeProperty.objectType.isInstance(domainObject)) {
					ObjectProperty<DomainObject, Object> objectProperty = (ObjectProperty<DomainObject, Object>) maybeProperty;
					Object value = context.validatedValues().get(id);
					objectProperty.setter.accept(domainObject, value);
				}
			}
		});

		assertThat(domainObject.getSum()).isEqualTo(3);
	}

	@Test
	void ifValidationFailResultShouldNotSetInDomainObject() {
		DomainObject domainObject=new DomainObject();
		domainObject.setA(7);
		domainObject.setB(4);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
			.add(Calculate.value(sumValue)
				.using(aProperty, bProperty)
				.by((a,b) -> a+b))
			.add(Calculate.value(sumProperty)
				.from(sumValue))
			.add(Validate.value(sumValue).by((validator, value) -> value.map(it -> (it > 10)
				? ImmutableList.of(ErrorMessage.of("to-big", it))
				: ImmutableList.<ErrorMessage>of())
				.orElse(ImmutableList.of(ErrorMessage.of("not-set")))))
		);

		Solver testee = Solver.builder()
			.calculationLookupFactory(context -> calculationLookup(context, domainObject))
			.build();

		Context context = testee.solve(valueGraph);

		assertThat(context.validatedValues().keys())
			.containsExactlyInAnyOrder(sumProperty, sumValue);

		context.validatedValues().keys().forEach(id -> {
			if (id instanceof ObjectProperty) {
				ObjectProperty<?,?> maybeProperty = (ObjectProperty<?, ?>) id;
				if (maybeProperty.objectType.isInstance(domainObject)) {
					ObjectProperty<DomainObject, Object> objectProperty = (ObjectProperty<DomainObject, Object>) maybeProperty;
					Object value = context.validatedValues().get(id);
					objectProperty.setter.accept(domainObject, value);
				}
			}
		});

		assertThat(context.hasValidationErrors(sumValue)).isTrue();
		assertThat(context.validationErrors(sumValue)).isNotNull()
			.containsExactly(ErrorMessage.of("to-big",11));
		assertThat(context.hasValidationErrors(sumProperty)).isFalse();
		assertThat(domainObject.getSum()).isNull();
	}

	private Calculation.ValueLookup calculationLookup(Context context, DomainObject domainObject) {
		return new Calculation.ValueLookup() {
			@Override public <T> T get(Value<T> id) {
				if (id instanceof ObjectProperty) {
					ObjectProperty<?, ?> maybeProperty = (ObjectProperty<?, ?>) id;
					if (maybeProperty.objectType.isInstance(domainObject)) {
						ObjectProperty<DomainObject, T> objectProperty = (ObjectProperty<DomainObject, T>) maybeProperty;
						return objectProperty.getter.apply(domainObject);
					}
				}
				return context.getValidated(id);
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

		static <T> ObjectProperty<DomainObject, T> property(String name, Function<DomainObject, T> getter,
			BiConsumer<DomainObject, T> setter) {
			return SolverTest.property(DomainObject.class, name, getter, setter);
		}
	}

	static <D,T> ObjectProperty<D, T> property(Class<D> domainObjectType, String name, Function<D, T> getter,
		BiConsumer<D, T> setter) {
		return new ObjectProperty<>(domainObjectType, name, getter, setter);
	}

	static class ObjectProperty<D, T> implements Value<T>, ValueSource<T>, ValueSink<T> {
		private final Class<D> objectType;
		private final String name;
		private final Function<D, T> getter;
		private final BiConsumer<D, T> setter;

		public ObjectProperty(
			Class<D> objectType,
			String name,
			Function<D, T> getter,
			BiConsumer<D, T> setter) {
			this.objectType = objectType;
			this.name = name;
			this.getter = getter;
			this.setter = setter;
		}

		@Override
		public String toString() {
			return "ObjectProperty{" + objectType + "." + name+ '}';
		}

		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ObjectProperty<?, ?> that = (ObjectProperty<?, ?>) o;
			return objectType.equals(that.objectType) && getter.equals(that.getter) && setter.equals(that.setter);
		}
		@Override public int hashCode() {
			return Objects.hash(objectType, getter, setter);
		}
	}
}