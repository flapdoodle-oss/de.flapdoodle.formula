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
package de.flapdoodle.formula.calculate.calculations;

import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.calculate.functions.FN1;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable(builder = false)
public abstract class Aggregated<S, X> implements Calculation<X>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract List<ValueSource<S>> sourceList();

	@Value.Parameter
	protected abstract FN1<List<S>, X> aggregation();

	@Override
	public List<ValueSource<S>> sources() {
		return sourceList();
	}

	@Override
	public X calculate(ValueLookup values) {
		List<S> sourceValues = sourceList().stream()
			.map(values::get)
			.collect(Collectors.toList());
		return aggregation().apply(sourceValues);
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(aggregation());
	}

	public static <S, X> Aggregated<S, X> with(
		List<? extends ValueSource<S>> sourceList,
		ValueSink<X> destination,
		FN1<List<S>, X> aggregation
	) {
		return ImmutableAggregated.of(destination, sourceList, aggregation);
	}
}
