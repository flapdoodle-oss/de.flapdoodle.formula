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
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class ReadOnlyValue<O, T> implements ReadableValue<O, T>, HasHumanReadableLabel {
	@Parameter
	public abstract Id<O> id();

	@Parameter
	protected abstract ReadOnlyProperty<O, T> property();

	@Override
	@Value.Auxiliary
	public T get(O instance) {
		return property().get(instance);
	}

	@Override
	public String asHumanReadable() {
		return property().asHumanReadable()+" {"+id().asHumanReadable()+"}";
	}

	public static <O, T> ImmutableReadOnlyValue<O, T> of(Id<O> id, ReadOnlyProperty<O, T> property) {
		return ImmutableReadOnlyValue.of(id, property);
	}
}
