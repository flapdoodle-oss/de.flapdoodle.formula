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
package de.flapdoodle.formula.howto.calculate;

import de.flapdoodle.formula.AbstractHowToTest;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.MappedValue;
import de.flapdoodle.formula.calculate.StrictValueLookup;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.domain.*;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import de.flapdoodle.formula.values.properties.ModifiableProperty;
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import de.flapdoodle.reflection.TypeInfo;
import de.flapdoodle.testdoc.Includes;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nullable;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

public class WhatIsAValueTest extends AbstractHowToTest {

	@RegisterExtension
	public static Recording recording = Recorder.with("WhatIsAValue.md", TabSize.spaces(2));

	@Test
	void values() {
		recording.begin("types");
		Value<Integer> value=new Value<Integer>() {};
		Value<Integer> valueSink=new ValueSink<Integer>() {};
		Value<Integer> valueSource=new ValueSource<Integer>() {};
		recording.end();

		recording.begin("named");
		Value<Double> a = Value.named("a", Double.class);
		Value<Double> otherA = Value.named("a", Double.class);
		Value<Double> b = Value.named("b", Double.class);

		assertThat(a).isInstanceOf(ValueSource.class);
		assertThat(a).isInstanceOf(ValueSink.class);

		assertThat(a).isNotEqualTo(b);
		assertThat(a).isEqualTo(otherA);
		recording.end();

	}

	@Test
	void valuesRelated() {
		recording.begin("relatedTo");
		Value<Double> a = Value.named("a", Double.class);

		Value<Double> aRelatedToX = a.relatedTo("X");
		Value<Double> aRelatedToY = a.relatedTo("Y");
		assertThat(aRelatedToX).isNotEqualTo(aRelatedToY);
		recording.end();

		recording.begin("idFactory");
		Id<Double> firstId = Id.idFor(TypeInfo.of(Double.class));
		Id<Double> secondId = Id.idFor(TypeInfo.of(Double.class));
		assertThat(firstId).isNotEqualTo(secondId);

		Value<Double> relatedToFirstId = a.relatedTo(firstId);
		Value<Double> relatedToSecondId = a.relatedTo(secondId);
		assertThat(relatedToFirstId).isNotEqualTo(relatedToSecondId);
		recording.end();
	}

	@Test
	void valueLookup() {
		recording.begin("sample");
		Value<Double> a = Value.named("a", Double.class);
		Value<Integer> b = Value.named("b", Integer.class);

		ValueLookup valueLookup=new ValueLookup() {
			@Override public <T> @Nullable T get(Value<T> value) {
				if (value == a) {
					return (T) Double.valueOf(1.0);
				}
				throw new IllegalArgumentException("not found: "+value);
			}
		};

		assertThat(valueLookup.get(a)).isEqualTo(1.0);
		assertThatThrownBy(() -> valueLookup.get(b))
			.isInstanceOf(IllegalArgumentException.class);
		recording.end();

		recording.begin("strict");
		StrictValueLookup strictValueLookup = StrictValueLookup.of(
			MappedValue.of(a, 2.0)
		);

		assertThat(strictValueLookup.get(a)).isEqualTo(2.0);
		assertThatThrownBy(() -> strictValueLookup.get(b))
			.isInstanceOf(IllegalArgumentException.class);
		recording.end();
	}

	@Test
	void properties() {
		recording.include(SampleBean.class, Includes.WithoutImports, Includes.WithoutPackage, Includes.Trim);
		recording.begin("facts");
		Function<SampleBean, Double> getterAsFunction = SampleBean::getAmount;
		Function<SampleBean, Double> secondInstance = SampleBean::getAmount;
		assertThat(getterAsFunction).isNotEqualTo(secondInstance);
		recording.end();

		recording.begin("readOnly");
		ReadOnlyProperty<SampleBean, Double> property = ReadOnlyProperty.of(SampleBean.class, "amount", SampleBean::getAmount);

		SampleBean bean = new SampleBean();
		bean.setAmount(123.0);

		assertThat(property.get(bean)).isEqualTo(123.0);
		recording.end();

		recording.begin("readOnly.asValue");
		ReadOnlyValue<SampleBean, Double> amountValue = property.withId(bean.getId());
		assertThat(amountValue.get(bean)).isEqualTo(123.0);
		assertThat(amountValue.id()).isEqualTo(bean.getId());
		recording.end();

		recording.begin("modifiable");
		ModifiableProperty<SampleBean, Integer> modifiable = ModifiableProperty.of(SampleBean.class, "number",
			SampleBean::getNumber, SampleBean::setNumber);

		assertThat(modifiable.get(bean)).isNull();
		modifiable.set(bean,42);
		assertThat(modifiable.get(bean)).isEqualTo(42);
		recording.end();

		recording.begin("modifiable.asValue");
		ModifyInstanceValue<SampleBean, Integer> numberValue = modifiable.withId(bean.getId());
		assertThat(numberValue.get(bean)).isEqualTo(42);
		assertThat(numberValue.id()).isEqualTo(bean.getId());
		numberValue.set(bean,13);
		assertThat(numberValue.get(bean)).isEqualTo(13);
		recording.end();
	}

	@Test
	void immutables() {
		recording.include(Sample.class, Includes.WithoutImports, Includes.WithoutPackage, Includes.Trim);

		recording.begin("readOnly");
		ReadOnlyProperty<Sample, Double> property = ReadOnlyProperty.of(Sample.class, "amount", Sample::getAmount);

		Sample instance = Sample.builder()
						.amount(123.0)
						.build();

		assertThat(property.get(instance)).isEqualTo(123.0);
		recording.end();

		recording.begin("readOnly.asValue");
		ReadOnlyValue<Sample, Double> amountValue = property.withId(instance.getId());
		assertThat(amountValue.get(instance)).isEqualTo(123.0);
		assertThat(amountValue.id()).isEqualTo(instance.getId());
		recording.end();

		recording.begin("copyOnChange");
		CopyOnChangeProperty<Sample, Integer> changeable = CopyOnChangeProperty.of(Sample.class, "number",
						Sample::getNumber, Sample::withNumber);

		assertThat(changeable.get(instance)).isNull();
		Sample changedInstance = changeable.change(instance, 42);
		assertThat(changedInstance.getNumber()).isEqualTo(42);
		assertThat(instance.getNumber()).isNull();
		recording.end();

		recording.begin("copyOnChange.asValue");
		CopyOnChangeValue<Sample, Integer> numberValue = changeable.withId(instance.getId());
		assertThat(numberValue.get(changedInstance)).isEqualTo(42);
		assertThat(numberValue.id()).isEqualTo(instance.getId());
		assertThat(numberValue.id()).isEqualTo(changedInstance.getId());
		recording.end();
	}

	@Test
	void valueLookupWithProperties() {
		recording.begin("sample");
		Sample instance = Sample.builder()
			.amount(123.0)
			.build();

		ReadOnlyValue<Sample, Double> amountValue = ReadOnlyProperty
			.of(Sample.class, "amount", Sample::getAmount)
			.withId(instance.getId());
		CopyOnChangeValue<Sample, Integer> numberValue = CopyOnChangeProperty
			.of(Sample.class, "number", Sample::getNumber, Sample::withNumber)
			.withId(instance.getId());

		ValueLookup delegatingValueLookup = new ValueLookup() {
			@Override public <T> @Nullable T get(Value<T> value) {
				if (value instanceof ReadableValue) {
					ReadableValue<?, T> readableValue = (ReadableValue<?, T>) value;
					if (readableValue.id().equals(instance.getId())) {
						ReadableValue<Sample, T> readFromSample = (ReadableValue<Sample, T>) readableValue;
						return readFromSample.get(instance);
					}
				}
				throw new IllegalArgumentException("not found: "+value);
			}
		};

		assertThat(delegatingValueLookup.get(amountValue)).isEqualTo(123.0);
		assertThat(delegatingValueLookup.get(numberValue)).isNull();
		recording.end();
	}

	@Test
	void changeableInstance() {
		recording.include(ChangeableSample.class, Includes.WithoutImports, Includes.WithoutPackage, Includes.Trim);
		recording.begin("sample");
		ChangeableSample instance = ChangeableSample.builder()
			.name("name")
			.number(42)
			.amount(123.0)
			.build();

		ValueLookup valueLookup = ChangeableInstanceValueLookup.of(
			instance, ValueLookup.failOnEachValue()
		);

		assertThat(valueLookup.get(ChangeableSample.name.withId(instance.id())))
			.isEqualTo("name");
		assertThat(valueLookup.get(ChangeableSample.amount.withId(instance.id())))
			.isEqualTo(123.0);
		recording.end();
	}
}
