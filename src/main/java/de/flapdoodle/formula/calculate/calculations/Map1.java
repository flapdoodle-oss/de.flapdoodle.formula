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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.calculate.ValueLookup;
import de.flapdoodle.formula.calculate.functions.FN1;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

@Value.Immutable(builder = false)
public abstract class Map1<A, X> implements Calculation<X>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract ValueSource<A> source();

	@Value.Parameter
	protected abstract FN1<A, X> transformation();

	@Override
	public Set<ValueSource<?>> sources() {
		return ImmutableSet.of(source());
	}

	@Override
	public X calculate(ValueLookup values) {
		return transformation().apply(values.get(source()));
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(transformation());
	}

	public static <A, X> Map1<A, X> with(
		ValueSource<A> source,
		ValueSink<X> destination,
		FN1<A, X> transformation
	) {
		return ImmutableMap1.of(destination, source, transformation);
	}
}
