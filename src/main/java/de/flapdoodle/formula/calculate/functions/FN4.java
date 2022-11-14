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

@FunctionalInterface
public interface FN4<A, B, C, D, R> {
	@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d);

	@Value.Immutable
	abstract class FN4WithLabel<A, B, C, D, R> implements FN4<A, B, C, D, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN4<A, B, C, D, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d) {
			return delegate().apply(a, b, c, d);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}

	}

	static <A, B, C, D, R> FN4<A, B, C, D, R> withLabel(FN4<A, B, C, D, R> delegate, String label) {
		return ImmutableFN4WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN4wrapF4<A, B, C, D, R> implements FN4<A, B, C, D, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F4<A, B, C, D, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d) {
			return (a!=null && b!=null && c!=null && d!=null)
				? delegate().apply(a, b, c, d)
				: null;
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, D, R> FN4<A, B, C, D, R> mapOnlyIfNotNull(F4<A, B, C, D, R> delegate) {
		return ImmutableFN4wrapF4.of(delegate);
	}

	@Value.Immutable
	abstract class FN4checkNull<A, B, C, D, R> implements FN4<A, B, C, D, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F4<A, B, C, D, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d) {
			Preconditions.checkNotNull(a,"%s: a is null", asHumanReadable());
			Preconditions.checkNotNull(b,"%s: b is null", asHumanReadable());
			Preconditions.checkNotNull(c,"%s: c is null", asHumanReadable());
			Preconditions.checkNotNull(d,"%s: d is null", asHumanReadable());
			return Preconditions.checkNotNull(delegate().apply(a, b, c, d), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, D, R> FN4<A, B, C, D, R> checkNull(F4<A, B, C, D, R> delegate) {
		return ImmutableFN4checkNull.of(delegate);
	}

}
