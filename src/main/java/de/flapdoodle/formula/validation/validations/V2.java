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

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.ValidatedValue;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface V2<T, A, B> {
	List<ErrorMessage> validate(@Nonnull Optional<T> value, @Nonnull ValidatedValue<A> a, @Nonnull ValidatedValue<B> b);

	@Value.Immutable
	abstract class V2Explained<T, A, B> implements V2<T, A, B>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract V2<T, A, B> delegate();

		@Value.Parameter
		protected abstract String humanReadable();

		@Override
		@Value.Auxiliary
		@Nonnull public List<ErrorMessage> validate(@Nonnull Optional<T> value, @Nonnull ValidatedValue<A> a, @Nonnull ValidatedValue<B> b) {
			return delegate().validate(value, a, b);
		}

		@Override
		public String asHumanReadable() {
			return humanReadable();
		}
	}

	static <T, A, B> V2<T, A, B> withLabel(V2<T, A, B> delegate, String label) {
		return ImmutableV2Explained.of(delegate, label);
	}
}
