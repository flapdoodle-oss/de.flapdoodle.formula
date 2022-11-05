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
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.types.Id;
import de.flapdoodle.formula.values.domain.CopyOnChangeValue;
import org.immutables.value.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value.Immutable
public abstract class CopyOnChangeProperty<O, T> implements IsReadable<O, T>, IsChangeable<O, T>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Value.Parameter
	protected abstract BiFunction<O, T, O> copyOnWrite();

	@Override
	public String toString() {
		return getClass().getSimpleName()+"{"+type().getSimpleName()+"."+name()+"}";
	}

	@Override public String asHumanReadable() {
		return type().getSimpleName()+"."+name()+"#rw";
	}
	
	@Override
	@Value.Auxiliary
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Override
	@Value.Auxiliary
	public O change(O instance, T value) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return copyOnWrite().apply(instance, value);
	}

	public CopyOnChangeValue<O, T> withId(Id<O> id) {
		return CopyOnChangeValue.of(id, this);
	}

	public static <O, T> ImmutableCopyOnChangeProperty<O,T> of(Class<O> type, String name, Function<O, T> getter, BiFunction<O, T, O> copyOnWrite) {
		return ImmutableCopyOnChangeProperty.of(type, name, getter, copyOnWrite);
	}
}
