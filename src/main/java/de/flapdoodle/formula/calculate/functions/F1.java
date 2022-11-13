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
import java.util.function.Function;

// all arguments must be set
@FunctionalInterface
public interface F1<A, R> extends Function<A, R> {
	@Override @Nonnull R apply(@Nonnull A a);

	static <X> F1<X, X> identity() {
		return ImmutableF1Identity.of();
	}

	@Value.Immutable(singleton = true)
	abstract class F1Identity<X> implements F1<X,X> {
		@Override public X apply(X x) {
			return x;
		}
	}

	@Value.Immutable
	abstract class F1WithLabel<A, R> implements F1<A, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F1<A, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Override
		@Value.Auxiliary
		public R apply(A a) {
			return delegate().apply(a);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <A, R> F1WithLabel<A, R> withLabel(F1<A, R> delegate, String label) {
		return ImmutableF1WithLabel.of(delegate, label);
	}
}
