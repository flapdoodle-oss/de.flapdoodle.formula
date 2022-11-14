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
import de.flapdoodle.formula.calculate.functions.FN3;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

@Value.Immutable(builder = false)
public abstract class Merge3<A, B, C, X> implements Calculation<X>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract ValueSource<A> a();

	@Value.Parameter
	protected abstract ValueSource<B> b();

	@Value.Parameter
	protected abstract ValueSource<C> c();

	@Value.Parameter
	protected abstract FN3<A, B, C, X> transformation();

	@Override
	public Set<ValueSource<?>> sources() {
		return ImmutableSet.of(a(), b(), c());
	}

	@Override
	public X calculate(ValueLookup values) {
		return transformation().apply(values.get(a()), values.get(b()), values.get(c()));
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(transformation());
	}

	public static <A, B, C, X> Merge3<A, B, C, X> with(
		ValueSource<A> a,
		ValueSource<B> b,
		ValueSource<C> c,
		ValueSink<X> destination,
		FN3<A, B, C, X> transformation
	) {
		return ImmutableMerge3.of(destination, a, b, c, transformation);
	}
}
