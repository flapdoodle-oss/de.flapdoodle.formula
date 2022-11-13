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
import de.flapdoodle.formula.validation.Validator;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

public interface V0<T> {
	List<ErrorMessage> validate(Validator validator, Optional<T> value);

	@Value.Immutable
	abstract class V0Explained<T> implements V0<T>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract V0<T> delegate();
		@Value.Parameter
		protected abstract String humanReadable();

		@Override
		@Value.Auxiliary
		public List<ErrorMessage> validate(Validator validator, Optional<T> value) {
			return delegate().validate(validator, value);
		}

		@Override
		public String asHumanReadable() {
			return humanReadable();
		}
	}

	static <A> V0<A> withLabel(V0<A> delegate, String label) {
		return ImmutableV0Explained.of(delegate, label);
	}
}
