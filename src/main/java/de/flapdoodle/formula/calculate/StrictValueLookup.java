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
package de.flapdoodle.formula.calculate;

import com.google.common.base.Preconditions;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.types.Checks;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Immutable
public abstract class StrictValueLookup implements ValueLookup, HasSetOfKnownValues {
	protected abstract List<MappedValue<?>> entries();

	@org.immutables.value.Value.Check
	protected void check() {
		Set<Value<?>> collidingIds = Checks.collisions(entries(), MappedValue::id);
		Preconditions.checkArgument(collidingIds.isEmpty(),"multiple entries with following ids: %s", collidingIds);
	}

	@org.immutables.value.Value.Lazy
	protected Map<Value<?>, Object> values() {
		return entries().stream()
			.filter(it -> it.value()!=null)
			.collect(Collectors.toMap(MappedValue::id, MappedValue::value));
	}

	@org.immutables.value.Value.Lazy
	protected Set<Value<?>> nullValues() {
		return entries().stream()
			.filter(it -> it.value()==null)
			.map(MappedValue::id)
			.collect(Collectors.toSet());
	}

	@Override
	@org.immutables.value.Value.Lazy
	public Set<Value<?>> keySet() {
		return entries().stream()
			.map(MappedValue::id)
			.collect(Collectors.toSet());
	}


	@Override
	@Auxiliary
	public <T> @Nullable T get(Value<T> id) {
		if (nullValues().contains(id)) return null;
		Preconditions.checkArgument(values().containsKey(id),"value not set: %s", id);
		return (T) values().get(id);
	}

	public static StrictValueLookup of(Collection<? extends MappedValue<?>> entries) {
		return ImmutableStrictValueLookup.builder()
			.addAllEntries(entries)
			.build();
	}

	public static StrictValueLookup of(MappedValue<?> ... entries) {
		return ImmutableStrictValueLookup.builder()
			.addEntries(entries)
			.build();
	}
}
