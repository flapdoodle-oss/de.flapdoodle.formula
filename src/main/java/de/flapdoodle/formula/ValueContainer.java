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
package de.flapdoodle.formula;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

@Immutable
public abstract class ValueContainer {
	protected abstract Map<Value<?>, Object> values();
	protected abstract Set<Value<?>> nullValues();

	@org.immutables.value.Value.Auxiliary
	public <T> ValueContainer add(Value<T> id, T value) {
		Preconditions.checkArgument(!values().containsKey(id), "%s already set to %s", id, values().get(id));
		Preconditions.checkArgument(!nullValues().contains(id), "%s already set to null", id);

		ImmutableValueContainer.Builder builder = ImmutableValueContainer.builder()
			.from(this);
		if (value==null) {
			builder.addNullValues(id);
		} else {
			builder.putValues(id, value);
		}
		return builder.build();
	}

	@org.immutables.value.Value.Auxiliary
	public <T> @Nullable T get(Value<T> id) {
		return !nullValues().contains(id)
			? (T) Preconditions.checkNotNull(values().get(id),"value %s not set", id)
			: null;
	}

	public Set<Value<?>> keys() {
		return Sets.union(values().keySet(), nullValues());
	}

	public static ValueContainer empty() {
		return ImmutableValueContainer.builder().build();
	}
}
