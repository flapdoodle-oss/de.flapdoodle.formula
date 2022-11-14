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
package de.flapdoodle.formula.validation.validations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.*;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Value.Immutable(builder = false)
public abstract class RelatedTo2<X, A, B> implements Validation<X>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract ValueSource<A> a();

	@Value.Parameter
	protected abstract ValueSource<B> b();

	@Value.Parameter
	protected abstract V2<X, A, B> validation();

	@Override
	public Set<ValueSource<?>> sources() {
		return ImmutableSet.of(a(), b());
	}

	@Override
	public List<ErrorMessage> validate(Optional<X> unvalidatedValue, ValidatedValueLookup values) {
		return validation().validate(unvalidatedValue, values.get(a()), values.get(b()));
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(validation());
	}

	public static <X, A, B> RelatedTo2<X, A, B> with(
		de.flapdoodle.formula.Value<X> destination,
		ValueSource<A> a,
		ValueSource<B> b,
		V2<X, A, B> validation
	) {
		return ImmutableRelatedTo2.of(destination, a, b, validation);
	}
}
