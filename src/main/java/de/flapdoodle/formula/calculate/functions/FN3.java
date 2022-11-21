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

import static de.flapdoodle.formula.types.HasHumanReadableLabel.asHumanReadable;

@FunctionalInterface
public interface FN3<A, B, C, R> {
	@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c);

	@Value.Immutable
	abstract class FN3WithLabel<A, B, C, R> implements FN3<A, B, C, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN3<A, B, C, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c) {
			return delegate().apply(a, b, c);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}

	}

	static <A, B, C, R> FN3<A, B, C, R> withLabel(FN3<A, B, C, R> delegate, String label) {
		return ImmutableFN3WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN3wrapF3<A, B, C, R> implements FN3<A, B, C, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F3<A, B, C, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c) {
			return (a!=null && b!=null && c!=null)
				? delegate().apply(a, b, c)
				: null;
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, R> FN3<A, B, C, R> mapOnlyIfNotNull(F3<A, B, C, R> delegate) {
		return ImmutableFN3wrapF3.of(delegate);
	}

	@Value.Immutable
	abstract class FN3checkNull<A, B, C, R> implements FN3<A, B, C, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F3<A, B, C, R> delegate();

		@Value.Parameter
		protected abstract String a();

		@Value.Parameter
		protected abstract String b();

		@Value.Parameter
		protected abstract String c();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c) {
			Preconditions.checkNotNull(a,"%s: %s is null", asHumanReadable(), a());
			Preconditions.checkNotNull(b,"%s: %s is null", asHumanReadable(), b());
			Preconditions.checkNotNull(c,"%s: %s is null", asHumanReadable(), c());
			return Preconditions.checkNotNull(delegate().apply(a, b, c), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, R> FN3<A, B, C, R> checkNull(F3<A, B, C, R> delegate, Object labelA, Object labelB, Object labelC) {
		return ImmutableFN3checkNull.of(delegate, asHumanReadable(labelA), asHumanReadable(labelB), asHumanReadable(labelC));
	}

}
