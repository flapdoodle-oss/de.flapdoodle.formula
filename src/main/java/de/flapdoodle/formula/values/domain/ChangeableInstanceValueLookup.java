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

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.types.Maybe;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import javax.annotation.Nullable;

@Immutable
public abstract class ChangeableInstanceValueLookup<O extends ChangeableInstance<O>> implements ValueLookup {

	@Parameter
	protected abstract O instance();

	@Parameter
	protected abstract ValueLookup fallback();

	@Override
	public <T> @Nullable T get(Value<T> id) {
		if (id instanceof ReadableValue) {
			Maybe<? extends T> value = instance().findValue((ReadableValue<?, ? extends T>) id);
			if (value.hasSome()) return value.get();
		}
		return fallback().get(id);
	}

	public static <O extends ChangeableInstance<O>> ChangeableInstanceValueLookup<O> of(O instance, ValueLookup fallback) {
		return ImmutableChangeableInstanceValueLookup.of(instance, fallback);
	}
}
