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
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.ValidatedValueLookup;
import de.flapdoodle.formula.validation.Validation;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable(builder = false)
public abstract class RelatedTo3<X, A, B, C> implements Validation<X>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract ValueSource<A> a();

	@Value.Parameter
	protected abstract ValueSource<B> b();

	@Value.Parameter
	protected abstract ValueSource<C> c();

	@Value.Parameter
	protected abstract V3<X, A, B, C> validation();

	@Override
	public List<ValueSource<?>> sources() {
		return ImmutableList.of(a(), b(), c());
	}

	@Override
	public List<ErrorMessage> validate(Optional<X> unvalidatedValue, ValidatedValueLookup values) {
		return validation().validate(unvalidatedValue, values.get(a()), values.get(b()), values.get(c()));
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(validation());
	}

	public static <X, A, B, C> RelatedTo3<X, A, B, C> with(
		de.flapdoodle.formula.Value<X> destination,
		ValueSource<A> a,
		ValueSource<B> b,
		ValueSource<C> c,
		V3<X, A, B, C> validation
	) {
		return ImmutableRelatedTo3.of(destination, a, b, c, validation);
	}
}
