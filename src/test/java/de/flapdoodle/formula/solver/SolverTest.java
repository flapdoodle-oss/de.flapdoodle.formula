package de.flapdoodle.formula.solver;

import de.flapdoodle.formula.*;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class SolverTest {

	@Test
	void sample() {
		DomainObject domainObject=new DomainObject();
		domainObject.setA(1);
		domainObject.setB(2);

		ObjectProperty<DomainObject, Integer> sumProperty = DomainObject.property("sum", DomainObject::getSum, DomainObject::setSum);
		ObjectProperty<DomainObject, Integer> aProperty = DomainObject.property("a", DomainObject::getA, DomainObject::setA);
		ObjectProperty<DomainObject, Integer> bProperty = DomainObject.property("b", DomainObject::getB, DomainObject::setB);

		ValueGraph valueGraph = GraphBuilder.build(Rules.empty()
			.add(Calculate.value(Value.named("sum", Integer.class))
				.using(aProperty, bProperty)
				.by((a,b) -> a+b))
			.add(Calculate.value(sumProperty)
				.from(Value.named("sum", Integer.class)))
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
			.containsExactlyInAnyOrder(sumProperty, Value.named("sum", Integer.class));

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

		public Class<D> objectType() {
			return objectType;
		}

		public Function<D, T> getter() {
			return getter;
		}

		public BiConsumer<D, T> setter() {
			return setter;
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