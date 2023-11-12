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
import java.util.function.Supplier;

// all arguments must be set
@FunctionalInterface
public interface F0<R> extends Supplier<R> {
	@Override @Nonnull R get();

	@Value.Immutable
	abstract class F0WithLabel<R> implements F0<R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F0<R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Override
		@Value.Auxiliary
		public R get() {
			return delegate().get();
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <R> F0WithLabel<R> withLabel(F0<R> delegate, String label) {
		return ImmutableF0WithLabel.of(delegate, label);
	}
}
