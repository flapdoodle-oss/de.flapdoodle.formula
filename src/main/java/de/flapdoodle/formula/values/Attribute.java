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
package de.flapdoodle.formula.values;

import de.flapdoodle.formula.SinkAndSource;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.reflection.TypeInfo;
import org.immutables.value.Value.Immutable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Immutable
public abstract class Attribute<O, T> implements SinkAndSource<T> {
	protected abstract TypeInfo<O> objectType();

	protected abstract String name();

	protected abstract Function<O, T> getter();

	protected abstract BiConsumer<O, T> setter();

	public boolean isMatchingInstance(Object instance) {
		return objectType().isInstance(instance);
	}

	public T get(O domainObject) {
		return getter().apply(domainObject);
	}

	public void set(O domainObject, T value) {
		setter().accept(domainObject, value);
	}

	@Override
	public String toString() {
		return "Attribute{" + objectType() + "." + name() + '}';
	}

	public static <T, O> Attribute<O, T> of(Class<O> objectType, String name, Function<O, T> getter, BiConsumer<O, T> setter) {
		return ImmutableAttribute.<O, T>builder()
			.objectType(TypeInfo.of(objectType))
			.name(name)
			.getter(getter)
			.setter(setter)
			.build();
	}

	public static <T, O> Attribute<O, T> of(TypeInfo<O> objectType, String name, Function<O, T> getter, BiConsumer<O, T> setter) {
		return ImmutableAttribute.<O, T>builder()
			.objectType(objectType)
			.name(name)
			.getter(getter)
			.setter(setter)
			.build();
	}
}
