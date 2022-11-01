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
package de.flapdoodle.formula.solver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.flapdoodle.formula.Value;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ValueContainer {
	protected final Map<Value<?>, Object> values;
	protected final Set<Value<?>> nullValues;

	private ValueContainer(Map<Value<?>, Object> values, Set<Value<?>> nullValues) {
		this.values = values;
		this.nullValues = nullValues;
	}

	public <T> ValueContainer add(Value<T> id, T value) {
		Preconditions.checkArgument(!values.containsKey(id), "%s already set to %s", id, values.get(id));
		Preconditions.checkArgument(!nullValues.contains(id), "%s already set to null", id);

		return value != null
			? new ValueContainer(ImmutableMap.<Value<?>, Object>builder().putAll(values).put(id, value).build(), nullValues)
			: new ValueContainer(values, ImmutableSet.<Value<?>>builder().addAll(nullValues).add(id).build());
	}

	public <T> @Nullable T get(Value<T> id) {
		return !nullValues.contains(id)
			? (T) Preconditions.checkNotNull(values.get(id),"value %s not set", id)
			: null;
	}

	public Set<Value<?>> keys() {
		return Sets.union(values.keySet(), nullValues);
	}

	@Override public String toString() {
		return "ValueContainer{" +
			"values=" + values +
			", nullValues=" + nullValues +
			'}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ValueContainer that = (ValueContainer) o;
		return values.equals(that.values) && nullValues.equals(that.nullValues);
	}

	@Override
	public int hashCode() {
		return Objects.hash(values, nullValues);
	}
	
	public static ValueContainer empty() {
		return new ValueContainer(ImmutableMap.of(), ImmutableSet.of());
	}
}
