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
import java.util.function.BiFunction;

@FunctionalInterface
public interface F2<A, B, R> extends BiFunction<A, B, R> {
	@Override @Nonnull R apply(@Nonnull A a, @Nonnull B b);

	@Value.Immutable
	abstract class F2WithLabel<A, B, R> implements F2<A, B, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F2<A, B, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Override
		@Value.Auxiliary
		public R apply(A a, B b) {
			return delegate().apply(a, b);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <A, B, R> F2WithLabel<A, B, R> withLabel(F2<A, B, R> delegate, String label) {
		return ImmutableF2WithLabel.of(delegate, label);
	}

}
