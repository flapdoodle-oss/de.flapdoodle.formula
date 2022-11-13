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
public interface F3<A, B, C, R> {
	@Nonnull R apply(@Nonnull A a, @Nonnull B b, @Nonnull C c);

	@Value.Immutable
	abstract class F3WithLabel<A, B, C, R> implements F3<A, B, C, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F3<A, B, C, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Override
		@Value.Auxiliary
		public R apply(A a, B b, C c) {
			return delegate().apply(a, b, c);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <A, B, C, R> F3<A, B, C, R> withLabel(F3<A, B, C, R> delegate, String label) {
		return ImmutableF3WithLabel.of(delegate, label);
	}

}
