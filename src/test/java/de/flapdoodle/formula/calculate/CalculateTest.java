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
package de.flapdoodle.formula.calculate;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.calculations.*;
import de.flapdoodle.formula.calculate.functions.*;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static de.flapdoodle.formula.Value.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculateTest {
	/**
	 * Generated Tests
	 */
	@Nested
	class GeneratedTests {
		ValueSink<String> destination = named("dest", String.class);

		@Test
		void valueUsingIfAllSetWithLabel() {
			Generated<String> testee = Calculate.value(destination).by(new StringGenerator(), "label");

			assertThat(testee.sources()).isEmpty();
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			List<? extends MappedValue<?>> mappedValues = mappedValues();

			assertThat(((Calculation<String>) testee)
					.calculate(valueLookup(mappedValues))).isEqualTo("boo");
		}

		class StringGenerator implements F0<String> {
			@Nonnull @Override public String get() {
				return "boo";
			}

			@Override
			public String toString() {
				return StringGenerator.class.getSimpleName();
			}
		}
	}

	/**
	 * Map1 Tests
	 */
	@Nested
	class Map1Tests {
		ValueSource<Integer> source = named("source", Integer.class);
		ValueSink<String> destination = named("dest", String.class);

		@Test
		void valueFrom() {
			Map1<String, String> testee = Calculate.value(destination).from(named("s", String.class));

			assertThat(testee.sources()).containsExactly(named("s", String.class));
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("FN1Identity{}");

			List<? extends MappedValue<?>> mappedValues = mappedValues(MappedValue.of(named("s", String.class), "expected"));
			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("expected");
			assertNullIfAnyValueIsNull(testee, mappedValues);
		}

		@Test
		void valueRequiring() {
			Map1<Integer, String> testee = Calculate.value(destination).requiring(source).by(new IntToString());

			assertThat(testee.sources()).containsExactly(source);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("IntToString");

			assertThat(testee.calculate(valueLookup(MappedValue.of(source, 1)))).isEqualTo("1");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "IntToString", MappedValue.of(source, 1));
		}

		@Test
		void valueRequiringWithLabel() {
			Map1<Integer, String> testee = Calculate.value(destination).requiring(source).by(new IntToString(), "label");

			assertThat(testee.sources()).containsExactly(source);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			assertThat(testee.calculate(valueLookup(MappedValue.of(source, 1)))).isEqualTo("1");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "label", MappedValue.of(source, 1));
		}

		@Test
		void valueUsing() {
			Map1<Integer, String> testee = Calculate.value(destination).using(source).by(new NullableIntToString());

			assertThat(testee.sources()).containsExactly(source);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("NullableIntToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingWithLabel() {
			Map1<Integer, String> testee = Calculate.value(destination).using(source).by(new NullableIntToString(), "identity");

			assertThat(testee.sources()).containsExactly(source);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSet() {
			Map1<Integer, String> testee = Calculate.value(destination).using(source).ifAllSetBy(new IntToString());

			assertThat(testee.sources()).containsExactly(source);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("IntToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSetWithLabel() {
			Map1<Integer, String> testee = Calculate.value(destination).using(source).ifAllSetBy(new IntToString(), "identity");

			assertThat(testee.sources()).containsExactly(source);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		void assertIntToStringCalledIfNotNull(Calculation<String> testee) {
			List<? extends MappedValue<?>> mappedValues = mappedValues(MappedValue.of(source, 1));

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("1");
			assertNullIfAnyValueIsNull(testee, mappedValues);
		}

		class IntToString implements F1<Integer, String> {
			@Nonnull @Override public String apply(@Nonnull Integer value) {
				return value.toString();
			}

			@Override
			public String toString() {
				return IntToString.class.getSimpleName();
			}
		}

		class NullableIntToString implements FN1<Integer, String> {

			@Nullable @Override public String apply(@Nullable Integer value) {
				return value != null ? value.toString() : null;
			}

			@Override
			public String toString() {
				return NullableIntToString.class.getSimpleName();
			}
		}
	}

	/**
	 * Merge2 Tests
	 */
	@Nested
	class Merge2Tests {
		ValueSource<Integer> a = named("a", Integer.class);
		ValueSource<Integer> b = named("b", Integer.class);
		ValueSink<String> destination = named("dest", String.class);

		List<? extends MappedValue<?>> mappedValues = mappedValues(
			MappedValue.of(a, 1),
			MappedValue.of(b, 2)
		);

		@Test
		void valueRequiring() {
			Merge2<Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b).by(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertThat(testee.calculate(valueLookup(mappedValues)))
				.isEqualTo("3");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "SumToString", mappedValues);
		}

		@Test
		void valueRequiringWithLabel() {
			Merge2<Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b).by(new SumToString(), "label");

			assertThat(testee.sources()).containsExactly(a, b);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			assertThat(testee.calculate(valueLookup(mappedValues)))
				.isEqualTo("3");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "label", mappedValues);
		}

		@Test
		void valueUsing() {
			Merge2<Integer, Integer, String> testee = Calculate.value(destination).using(a, b).by(new NullableSumToString());

			assertThat(testee.sources()).containsExactly(a, b);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("NullableSumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingWithLabel() {
			Merge2<Integer, Integer, String> testee = Calculate.value(destination).using(a, b).by(new NullableSumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSet() {
			Merge2<Integer, Integer, String> testee = Calculate.value(destination).using(a, b).ifAllSetBy(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSetWithLabel() {
			Merge2<Integer, Integer, String> testee = Calculate.value(destination).using(a, b).ifAllSetBy(new SumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		void assertIntToStringCalledIfNotNull(Calculation<String> testee) {
			List<? extends MappedValue<?>> mappedValues = mappedValues(MappedValue.of(a, 1), MappedValue.of(b, 2));

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("3");
			assertNullIfAnyValueIsNull(testee, mappedValues);
		}

		class SumToString implements F2<Integer, Integer, String> {
			@Nonnull @Override public String apply(@Nonnull Integer a, @Nonnull Integer b) {
				return "" + (a + b);
			}
			@Override
			public String toString() {
				return SumToString.class.getSimpleName();
			}
		}

		class NullableSumToString implements FN2<Integer, Integer, String> {
			@Nullable @Override public String apply(@Nullable Integer a, @Nullable Integer b) {
				return (a != null && b != null) ? "" + (a + b) : null;
			}

			@Override
			public String toString() {
				return NullableSumToString.class.getSimpleName();
			}
		}
	}

	/**
	 * Merge3 Tests
	 */
	@Nested
	class Merge3Tests {
		ValueSource<Integer> a = named("a", Integer.class);
		ValueSource<Integer> b = named("b", Integer.class);
		ValueSource<Integer> c = named("c", Integer.class);
		ValueSink<String> destination = named("dest", String.class);

		List<? extends MappedValue<?>> mappedValues = mappedValues(
			MappedValue.of(a, 1),
			MappedValue.of(b, 2),
			MappedValue.of(c, 3)
		);

		@Test
		void valueRequiring() {
			Merge3<Integer, Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b, c).by(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("6");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "SumToString",mappedValues);
		}

		@Test
		void valueRequiringWithLabel() {
			Merge3<Integer, Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b, c).by(new SumToString(), "label");

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("6");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "label",mappedValues);
		}

		@Test
		void valueUsing() {
			Merge3<Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c).by(new NullableSumToString());

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("NullableSumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingWithLabel() {
			Merge3<Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c).by(new NullableSumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSet() {
			Merge3<Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c).ifAllSetBy(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSetWithLabel() {
			Merge3<Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c).ifAllSetBy(new SumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		void assertIntToStringCalledIfNotNull(Calculation<String> testee) {
			List<? extends MappedValue<?>> mappedValues = mappedValues(MappedValue.of(a, 1), MappedValue.of(b, 2), MappedValue.of(c, 3));

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("6");
			assertNullIfAnyValueIsNull(testee, mappedValues);
		}

		class SumToString implements F3<Integer, Integer, Integer, String> {
			@Nonnull @Override public String apply(@Nonnull Integer a, @Nonnull Integer b, @Nonnull Integer c) {
				return "" + (a + b + c);
			}
			@Override
			public String toString() {
				return SumToString.class.getSimpleName();
			}
		}

		class NullableSumToString implements FN3<Integer, Integer, Integer, String> {
			@Nullable @Override public String apply(@Nullable Integer a, @Nullable Integer b, @Nullable Integer c) {
				return (a != null && b != null && c != null) ? "" + (a + b + c) : null;
			}

			@Override
			public String toString() {
				return NullableSumToString.class.getSimpleName();
			}
		}
	}

	/**
	 * Merge4 Tests
	 */
	@Nested
	class Merge4Tests {
		ValueSource<Integer> a = named("a", Integer.class);
		ValueSource<Integer> b = named("b", Integer.class);
		ValueSource<Integer> c = named("c", Integer.class);
		ValueSource<Integer> d = named("d", Integer.class);
		ValueSink<String> destination = named("dest", String.class);

		List<? extends MappedValue<?>> mappedValues = mappedValues(
			MappedValue.of(a, 1),
			MappedValue.of(b, 2),
			MappedValue.of(c, 3),
			MappedValue.of(d, 4)
		);

		@Test
		void valueRequiring() {
			Merge4<Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b, c, d).by(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c, d);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("10");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "SumToString", mappedValues);
		}

		@Test
		void valueRequiringWithLabel() {
			Merge4<Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b, c, d).by(new SumToString(), "label");

			assertThat(testee.sources()).containsExactly(a, b, c, d);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("10");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "label", mappedValues);
		}

		@Test
		void valueUsing() {
			Merge4<Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d).by(new NullableSumToString());

			assertThat(testee.sources()).containsExactly(a, b, c, d);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("NullableSumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingWithLabel() {
			Merge4<Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d).by(new NullableSumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b, c, d);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSet() {
			Merge4<Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d).ifAllSetBy(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c, d);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSetWithLabel() {
			Merge4<Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d).ifAllSetBy(new SumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b, c, d);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		void assertIntToStringCalledIfNotNull(Calculation<String> testee) {
			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("10");
			assertNullIfAnyValueIsNull(testee, mappedValues);
		}

		class SumToString implements F4<Integer, Integer, Integer, Integer, String> {
			@Nonnull @Override public String apply(@Nonnull Integer a, @Nonnull Integer b, @Nonnull Integer c, @Nonnull Integer d) {
				return "" + (a + b + c + d);
			}
			@Override
			public String toString() {
				return SumToString.class.getSimpleName();
			}
		}

		class NullableSumToString implements FN4<Integer, Integer, Integer, Integer, String> {
			@Nullable @Override public String apply(@Nullable Integer a, @Nullable Integer b, @Nullable Integer c, @Nullable Integer d) {
				return (a != null && b != null && c != null && d != null) ? "" + (a + b + c + d) : null;
			}

			@Override
			public String toString() {
				return NullableSumToString.class.getSimpleName();
			}
		}
	}

	/**
	 * Merge5 Tests
	 */
	@Nested
	class Merge5Tests {
		ValueSource<Integer> a = named("a", Integer.class);
		ValueSource<Integer> b = named("b", Integer.class);
		ValueSource<Integer> c = named("c", Integer.class);
		ValueSource<Integer> d = named("d", Integer.class);
		ValueSource<Integer> e = named("e", Integer.class);
		ValueSink<String> destination = named("dest", String.class);

		List<? extends MappedValue<?>> mappedValues = mappedValues(
			MappedValue.of(a, 1),
			MappedValue.of(b, 2),
			MappedValue.of(c, 3),
			MappedValue.of(d, 4),
			MappedValue.of(e, 5)
		);

		@Test
		void valueRequiring() {
			Merge5<Integer, Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b, c, d, e).by(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c, d, e);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("15");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "SumToString", mappedValues);
		}

		@Test
		void valueRequiringWithLabel() {
			Merge5<Integer, Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).requiring(a, b, c, d, e).by(new SumToString(), "label");

			assertThat(testee.sources()).containsExactly(a, b, c, d, e);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("15");
			assertNullPointerExceptionIfAnyValueIsNull(testee, "label", mappedValues);
		}

		@Test
		void valueUsing() {
			Merge5<Integer, Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d, e).by(new NullableSumToString());

			assertThat(testee.sources()).containsExactly(a, b, c, d, e);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("NullableSumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingWithLabel() {
			Merge5<Integer, Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d, e).by(new NullableSumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b, c, d, e);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSet() {
			Merge5<Integer, Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d, e).ifAllSetBy(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c, d, e);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertIntToStringCalledIfNotNull(testee);
		}

		@Test
		void valueUsingIfAllSetWithLabel() {
			Merge5<Integer, Integer, Integer, Integer, Integer, String> testee = Calculate.value(destination).using(a, b, c, d, e).ifAllSetBy(new SumToString(), "identity");

			assertThat(testee.sources()).containsExactly(a, b, c, d, e);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("identity");

			assertIntToStringCalledIfNotNull(testee);
		}

		void assertIntToStringCalledIfNotNull(Calculation<String> testee) {
			assertThat(testee.calculate(valueLookup(mappedValues))).isEqualTo("15");
			assertNullIfAnyValueIsNull(testee, mappedValues);
		}

		class SumToString implements F5<Integer, Integer, Integer, Integer, Integer, String> {
			@Nonnull @Override public String apply(@Nonnull Integer a, @Nonnull Integer b, @Nonnull Integer c, @Nonnull Integer d, @Nonnull Integer e) {
				return "" + (a + b + c + d +e );
			}
			@Override
			public String toString() {
				return SumToString.class.getSimpleName();
			}
		}

		class NullableSumToString implements FN5<Integer, Integer, Integer, Integer, Integer, String> {
			@Nullable @Override public String apply(@Nullable Integer a, @Nullable Integer b, @Nullable Integer c, @Nullable Integer d, @Nullable Integer e) {
				return (a != null && b != null && c != null && d != null && e != null) ? "" + (a + b + c + d + e) : null;
			}

			@Override
			public String toString() {
				return NullableSumToString.class.getSimpleName();
			}
		}
	}

	/**
	 * Aggregate Tests
	 */
	@Nested
	class AggregateTests {
		ValueSource<Integer> a = named("a", Integer.class);
		ValueSource<Integer> b = named("b", Integer.class);
		ValueSource<Integer> c = named("c", Integer.class);
		ValueSink<String> destination = named("dest", String.class);

		@Test
		void valueAggregating() {
			Aggregated<Integer, String> testee = Calculate.value(destination).aggregating(Arrays.asList(a, b, c))
				.by(new SumToString());

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("SumToString");

			assertThat(testee.calculate(valueLookup(MappedValue.of(a, 1), MappedValue.of(b, 2), MappedValue.of(c, 3))))
				.isEqualTo("3 entries: 6");
			assertThat(testee.calculate(valueLookup(MappedValue.of(a, 1), MappedValue.of(b, null), MappedValue.of(c, 3))))
				.isEqualTo("3 entries: 4");
		}

		@Test
		void valueAggregatingWithLabel() {
			Aggregated<Integer, String> testee = Calculate.value(destination).aggregating(Arrays.asList(a, b, c))
				.by(new SumToString(), "label");

			assertThat(testee.sources()).containsExactly(a, b, c);
			assertThat(testee.destination()).isEqualTo(destination);
			assertThat(testee.asHumanReadable()).isEqualTo("label");

			assertThat(testee.calculate(valueLookup(MappedValue.of(a, 1), MappedValue.of(b, 2), MappedValue.of(c, 3))))
				.isEqualTo("3 entries: 6");
			assertThat(testee.calculate(valueLookup(MappedValue.of(a, 1), MappedValue.of(b, null), MappedValue.of(c, 3))))
				.isEqualTo("3 entries: 4");
		}

		class SumToString implements FN1<List<Integer>, String> {

			@Nullable @Override public String apply(@Nullable List<Integer> values) {
				return (values != null) ? values.size() + " entries: " + values.stream()
					.filter(Objects::nonNull)
					.mapToInt(it -> it)
					.sum() : null;
			}

			@Override public String toString() {
				return SumToString.class.getSimpleName();
			}
		}

	}

	static ValueLookup valueLookup(MappedValue<?>... values) {
		return StrictValueLookup.of(values);
	}

	static ValueLookup valueLookup(Collection<? extends MappedValue<?>> values) {
		return StrictValueLookup.of(values);
	}

	static List<? extends MappedValue<?>> mappedValues(MappedValue<?>... mappedValues) {
		return Arrays.asList(mappedValues);
	}

	private void assertNullPointerExceptionIfAnyValueIsNull(Calculation<?> testee, String label, MappedValue<?> ... nonNullValues) {
		assertNullPointerExceptionIfAnyValueIsNull(testee, label, ImmutableList.copyOf(nonNullValues));
	}

	private void assertNullPointerExceptionIfAnyValueIsNull(Calculation<?> testee, String label, Collection<? extends MappedValue<?>> nonNullValues) {
		assertThat(testee.calculate(valueLookup(nonNullValues))).isNotNull();

		nonNullValues.forEach(setToNull -> {
			List<MappedValue<?>> unchanged = nonNullValues.stream().filter(it -> it != setToNull).collect(Collectors.toList());

			ValueLookup valueLookup = valueLookup(ImmutableList.<MappedValue<?>>builder()
				.addAll(unchanged)
				.add(MappedValue.of(setToNull.id(), null))
				.build());

			assertThatThrownBy(() -> testee.calculate(valueLookup))
				.isInstanceOf(NullPointerException.class)
				.hasMessage(label + ": " + HasHumanReadableLabel.asHumanReadable(setToNull.id()) + " is null");
		});
	}

	private void assertNullIfAnyValueIsNull(Calculation<?> testee, Collection<? extends MappedValue<?>> nonNullValues) {
		assertThat(testee.calculate(valueLookup(nonNullValues))).isNotNull();

		for (MappedValue<?> setToNull : nonNullValues) {
			List<MappedValue<?>> unchanged = nonNullValues.stream().filter(it -> it != setToNull).collect(Collectors.toList());

			ValueLookup valueLookup = valueLookup(ImmutableList.<MappedValue<?>>builder()
				.addAll(unchanged)
				.add(MappedValue.of(setToNull.id(), null))
				.build());

			assertThat(testee.calculate(valueLookup)).isNull();
		}
	}
}