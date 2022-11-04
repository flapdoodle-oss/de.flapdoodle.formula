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
package de.flapdoodle.formula.values.properties;

import com.google.common.base.Preconditions;
import org.immutables.value.Value;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Value.Immutable
public abstract class ModifiableProperty<O, T> implements IsReadable<O, T>, IsWritable<O, T> {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Value.Parameter
	protected abstract BiConsumer<O, T> setter();

	@Override
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Override
	public void set(O instance, T value) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		setter().accept(instance, value);
	}

	public static <O, T> ImmutableModifiableProperty<O,T> of(Class<O> type, String name, Function<O, T> getter, BiConsumer<O, T> setter) {
		return ImmutableModifiableProperty.of(type, name, getter, setter);
	}
}
