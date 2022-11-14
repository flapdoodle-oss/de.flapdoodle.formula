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
package de.flapdoodle.formula.calculate.functions;

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface F4<A, B, C, D, R> {
	@Nonnull R apply(@Nonnull A a, @Nonnull B b, @Nonnull C c, @Nonnull D d);

	@Value.Immutable
	abstract class F4WithLabel<A, B, C, D, R> implements F4<A, B, C, D, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F4<A, B, C, D, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Override
		@Value.Auxiliary
		public R apply(A a, B b, C c, D d) {
			return delegate().apply(a, b, c, d);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <A, B, C, D, R> F4<A, B, C, D, R> withLabel(F4<A, B, C, D, R> delegate, String label) {
		return ImmutableF4WithLabel.of(delegate, label);
	}

}
