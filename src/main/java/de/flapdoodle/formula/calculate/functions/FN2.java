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
import java.util.function.BiFunction;

@FunctionalInterface
public interface FN2<A, B, R> extends BiFunction<A, B, R> {
	@Override @Nullable R apply(@Nullable A a, @Nullable B b);

	@Value.Immutable
	abstract class FN2WithLabel<A, B, R> implements FN2<A, B, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN2<A, B, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b) {
			return delegate().apply(a, b);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}

	}

	static <A, B, R> FN2<A, B, R> withLabel(FN2<A, B, R> delegate, String label) {
		return ImmutableFN2WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN2wrapF2<A, B, R> implements FN2<A, B, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F2<A, B, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b) {
			return (a!=null && b!=null)
				? delegate().apply(a, b)
				: null;
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, R> FN2<A, B, R> mapOnlyIfNotNull(F2<A, B, R> delegate) {
		return ImmutableFN2wrapF2.of(delegate);
	}

	@Value.Immutable
	abstract class FN2checkNull<A, B, R> implements FN2<A, B, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F2<A, B, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b) {
			Preconditions.checkNotNull(a,"%s: a is null", asHumanReadable());
			Preconditions.checkNotNull(b,"%s: b is null", asHumanReadable());
			return Preconditions.checkNotNull(delegate().apply(a, b), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, R> FN2<A, B, R> checkNull(F2<A, B, R> delegate) {
		return ImmutableFN2checkNull.of(delegate);
	}


}
