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

import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.domain.*;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import de.flapdoodle.types.Maybe;
import org.immutables.value.Value;

import javax.annotation.Nullable;

import static de.flapdoodle.formula.values.properties.Properties.copyOnChange;
import static de.flapdoodle.formula.values.properties.Properties.readOnly;

@Value.Immutable
public abstract class ChangeableSample implements ChangeableInstance<ChangeableSample> {
	public static ReadOnlyProperty<ChangeableSample, String> name =
		readOnly(ChangeableSample.class, "name", ChangeableSample::name);
	public static ReadOnlyProperty<ChangeableSample, Double> amount =
		readOnly(ChangeableSample.class, "amount", ChangeableSample::amount);
	public static CopyOnChangeProperty<ChangeableSample, Integer> number =
		copyOnChange(ChangeableSample.class, "number", ChangeableSample::number, ChangeableSample::withNumber);

	@Value.Default
	public Id<ChangeableSample> id() {
		return Id.idFor(ChangeableSample.class);
	}

	public abstract @Nullable String name();

	public abstract @Nullable Integer number();

	public abstract @Nullable Double amount();

	public abstract ChangeableSample withNumber(Integer number);

	@Override
	public <T> ChangeableSample change(ChangeableValue<?, T> id, T value) {
		if (id.id().equals(id())) {
			return ((ChangeableValue<ChangeableSample, T>) id).change(this, value);
		}
		return this;
	}

	@Override
	public <T> Maybe<T> findValue(ReadableValue<?, T> id) {
		if (id.id().equals(id())) {
			return Maybe.some(((ReadableValue<ChangeableSample, T>) id).get(this));
		}
		return Maybe.none();
	}

	public static ImmutableChangeableSample.Builder builder() {
		return ImmutableChangeableSample.builder();
	}
}
