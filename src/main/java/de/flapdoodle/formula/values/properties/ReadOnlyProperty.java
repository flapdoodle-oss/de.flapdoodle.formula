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
import de.flapdoodle.formula.values.matcher.Matcher;
import de.flapdoodle.formula.values.matcher.ReadOnlyValue;
import org.immutables.value.Value;

import java.util.function.Function;

@Value.Immutable
public abstract class ReadOnlyProperty<O, T> implements IsReadable<O, T> {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Override public String toString() {
		return getClass().getSimpleName()+"{"+type().getSimpleName()+"."+name()+"}";
	}

	@Override
	@Value.Auxiliary
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Value.Auxiliary
	public ReadOnlyValue<O, T> matching(Matcher<O> matcher) {
		return ReadOnlyValue.of(matcher, this);
	}

	public static <O, T> ImmutableReadOnlyProperty<O,T> of(Class<O> type, String name, Function<O, T> getter) {
		return ImmutableReadOnlyProperty.of(type, name, getter);
	}
}
