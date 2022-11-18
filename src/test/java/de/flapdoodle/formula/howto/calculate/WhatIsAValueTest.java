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
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.Named;
import de.flapdoodle.formula.values.Related;
import de.flapdoodle.formula.values.domain.CopyOnChangeValue;
import de.flapdoodle.formula.values.domain.ModifyInstanceValue;
import de.flapdoodle.formula.values.domain.ReadOnlyValue;
import de.flapdoodle.formula.values.properties.*;
import de.flapdoodle.testdoc.Includes;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

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
		Id<Double> firstId = Id.idFor(Double.class);
		Id<Double> secondId = Id.idFor(Double.class);
		assertThat(firstId).isNotEqualTo(secondId);

		Value<Double> relatedToFirstId = a.relatedTo(firstId);
		Value<Double> relatedToSecondId = a.relatedTo(secondId);
		assertThat(relatedToFirstId).isNotEqualTo(relatedToSecondId);
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
		recording.end();
	}

	@Test
	void immutables() {
		recording.include(Sample.class, Includes.WithoutImports, Includes.WithoutPackage, Includes.Trim);

		recording.begin("readOnly");
		ReadOnlyProperty<Sample, Double> property = ReadOnlyProperty.of(Sample.class, "amount", Sample::getAmount);

		Sample bean = Sample.builder()
						.amount(123.0)
						.build();

		assertThat(property.get(bean)).isEqualTo(123.0);
		recording.end();

		recording.begin("readOnly.asValue");
		ReadOnlyValue<Sample, Double> amountValue = property.withId(bean.getId());
		assertThat(amountValue.get(bean)).isEqualTo(123.0);
		assertThat(amountValue.id()).isEqualTo(bean.getId());
		recording.end();

		recording.begin("copyOnChange");
		CopyOnChangeProperty<Sample, Integer> modifiable = CopyOnChangeProperty.of(Sample.class, "number",
						Sample::getNumber, Sample::withNumber);

		assertThat(modifiable.get(bean)).isNull();
		assertThat(modifiable.change(bean,42).getNumber()).isEqualTo(42);
		assertThat(bean.getNumber()).isEqualTo(42);
		recording.end();

		recording.begin("modifiable.asValue");
		CopyOnChangeValue<Sample, Integer> numberValue = modifiable.withId(bean.getId());
		assertThat(numberValue.get(bean)).isEqualTo(42);
		assertThat(numberValue.id()).isEqualTo(bean.getId());
		recording.end();
	}
}
