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
package de.flapdoodle.formula.values.matcher;

import de.flapdoodle.formula.types.Maybe;
import de.flapdoodle.formula.values.properties.ReadOnlyProperty;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class ReadOnlyValue<O, T> implements ReadableValue<O, T> {
	@Parameter
	protected abstract Matcher<O> matcher();
	@Parameter
	protected abstract ReadOnlyProperty<O, T> property();

	@Override
	public <X> Maybe<ReadOnlyValue<X, T>> matching(X instance) {
		return matcher().match(instance)
			? Maybe.some((ReadOnlyValue<X, T>) this)
			: Maybe.none();
	}

	@Override
	@Value.Auxiliary
	public T get(O instance) {
		return property().get(instance);
	}

	public static <O, T> ImmutableReadOnlyValue<O, T> of(Matcher<O> matcher, ReadOnlyProperty<O, T> property) {
		return ImmutableReadOnlyValue.of(matcher, property);
	}
}
