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
package de.flapdoodle.formula.rules;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.types.Checks;
import org.immutables.value.Value.Check;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Immutable
public abstract class CalculationMap {
	public abstract List<Calculation<?>> all();

	@Check
	protected void check() {
		Set<Value<?>> collidingIds = Checks.collisions(all(), Calculation::destination);
		Preconditions.checkArgument(collidingIds.isEmpty(),"multiple entries with following ids: %s", collidingIds);
	}

	@Lazy
	protected Map<Value<?>, Calculation<?>> map() {
		return all().stream()
			.collect(ImmutableMap.toImmutableMap(Calculation::destination, Function.identity()));
	}

	@Lazy
	public Set<Value<?>> keys() {
		return map().keySet();
	}

	public <T> @Nullable Calculation<T> get(Value<T> key) {
		return (Calculation<T>) map().get(key);
	}
	public boolean contains(Value<?> it) {
		return map().containsKey(it);
	}

	public CalculationMap add(Calculation<?> calculation) {
		return ImmutableCalculationMap.builder().from(this)
			.addAll(calculation)
			.build();
	}

	public CalculationMap addAll(Iterable<? extends Calculation<?>> calculations) {
		return ImmutableCalculationMap.builder().from(this)
			.addAllAll(calculations)
			.build();
	}

	public CalculationMap merge(Iterable<? extends CalculationMap> calculationMaps) {
		ImmutableCalculationMap.Builder builder = ImmutableCalculationMap.builder().from(this);
		calculationMaps.forEach(it -> builder.addAllAll(it.all()));
		return builder.build();
	}

	public static CalculationMap empty() {
		return ImmutableCalculationMap.builder().build();
	}
}
