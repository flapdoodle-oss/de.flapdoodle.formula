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
import de.flapdoodle.formula.calculate.functions.FN0;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.Set;

@Value.Immutable(builder = false)
public abstract class Generated<X> implements Calculation<X>, HasHumanReadableLabel {

	@Value.Parameter
	protected abstract FN0<X> transformation();

	@Override
	@Value.Auxiliary
	public Set<ValueSource<?>> sources() {
		return Collections.emptySet();
	}

	@Override
	public X calculate(ValueLookup values) {
		return transformation().get();
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(transformation());
	}

	public static <X> Generated<X> with(
		ValueSink<X> destination,
		FN0<X> transformation
	) {
		return ImmutableGenerated.of(destination, transformation);
	}
}
