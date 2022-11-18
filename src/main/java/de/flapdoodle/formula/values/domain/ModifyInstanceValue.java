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
package de.flapdoodle.formula.values.domain;

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.properties.CopyOnChangeProperty;
import de.flapdoodle.formula.values.properties.ModifiableProperty;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class ModifyInstanceValue<O, T> implements ModifiableValue<O, T>, HasHumanReadableLabel {
	@Parameter
	public abstract Id<O> id();
	@Parameter
	protected abstract ModifiableProperty<O, T> property();

	@Override
	public String asHumanReadable() {
		return property().asHumanReadable()+" {"+id().asHumanReadable()+"}";
	}
	
	@Override
	public T get(O instance) {
		return property().get(instance);
	}
	
	@Override
	public void set(O instance, T value) {
		property().set(instance, value);
	}

	public static <O, T> ImmutableModifyInstanceValue<O, T> of(Id<O> id, ModifiableProperty<O, T> property) {
		return ImmutableModifyInstanceValue.of(id, property);
	}
}
