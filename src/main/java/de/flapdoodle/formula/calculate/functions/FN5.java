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
public interface FN5<A, B, C, D, E, R> {
	@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e);

	@Value.Immutable
	abstract class FN5WithLabel<A, B, C, D, E, R> implements FN5<A, B, C, D, E, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN5<A, B, C, D, E, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e) {
			return delegate().apply(a, b, c, d, e);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}

	}

	static <A, B, C, D, E, R> FN5<A, B, C, D, E, R> withLabel(FN5<A, B, C, D, E, R> delegate, String label) {
		return ImmutableFN5WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN5wrapF5<A, B, C, D, E, R> implements FN5<A, B, C, D, E, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F5<A, B, C, D, E, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e) {
			return (a!=null && b!=null && c!=null && d!=null && e!=null)
				? delegate().apply(a, b, c, d, e)
				: null;
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, D, E, R> FN5<A, B, C, D, E, R> mapOnlyIfNotNull(F5<A, B, C, D, E, R> delegate) {
		return ImmutableFN5wrapF5.of(delegate);
	}

	@Value.Immutable
	abstract class FN5checkNull<A, B, C, D, E, R> implements FN5<A, B, C, D, E, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F5<A, B, C, D, E, R> delegate();

		@Value.Parameter
		protected abstract String a();

		@Value.Parameter
		protected abstract String b();

		@Value.Parameter
		protected abstract String c();

		@Value.Parameter
		protected abstract String d();

		@Value.Parameter
		protected abstract String e();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e) {
			Preconditions.checkNotNull(a,"%s: %s is null", asHumanReadable(), a());
			Preconditions.checkNotNull(b,"%s: %s is null", asHumanReadable(), b());
			Preconditions.checkNotNull(c,"%s: %s is null", asHumanReadable(), c());
			Preconditions.checkNotNull(d,"%s: %s is null", asHumanReadable(), d());
			Preconditions.checkNotNull(e,"%s: %s is null", asHumanReadable(), e());
			return Preconditions.checkNotNull(delegate().apply(a, b, c, d, e), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, D, E, R> FN5<A, B, C, D, E, R> checkNull(F5<A, B, C, D, E, R> delegate, Object labelA, Object labelB, Object labelC, Object labelD, Object labelE) {
		return ImmutableFN5checkNull.of(delegate, asHumanReadable(labelA), asHumanReadable(labelB), asHumanReadable(labelC), asHumanReadable(labelD), asHumanReadable(labelE));
	}

}
