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

import com.google.common.base.Preconditions;
import de.flapdoodle.formula.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

// arguments are nullable
@FunctionalInterface
public interface FN0<R> extends Supplier<R> {
	@Override @Nullable R get();

	@Value.Immutable
	abstract class FN0WithLabel<R> implements FN0<R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN0<R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
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

	static <R> FN0WithLabel<R> withLabel(FN0<R> delegate, String label) {
		return ImmutableFN0WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN0checkNull<R> implements FN0<R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F0<R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R get() {
			return Preconditions.checkNotNull(delegate().get(), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <R> FN0checkNull<R> checkNull(F0<R> delegate) {
		return ImmutableFN0checkNull.of(delegate);
	}

}
