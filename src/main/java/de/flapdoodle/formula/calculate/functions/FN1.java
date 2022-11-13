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

// arguments are nullable
@FunctionalInterface
public interface FN1<A, R> extends Function<A, R> {
	@Override @Nullable R apply(@Nullable A a);

	static <X> FN1<X, X> identity() {
		return ImmutableFN1Identity.of();
	}

	@Value.Immutable(singleton = true)
	abstract class FN1Identity<X> implements FN1<X,X> {
		@Nullable @Override public X apply(@Nullable X x) {
			return x;
		}
	}

	@Value.Immutable
	abstract class FN1WithLabel<A, R> implements FN1<A, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN1<A, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a) {
			return delegate().apply(a);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <A, R> FN1WithLabel<A, R> withLabel(FN1<A, R> delegate, String label) {
		return ImmutableFN1WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN1wrapF1<A, R> implements FN1<A, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F1<A, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a) {
			return a!=null
				? delegate().apply(a)
				: null;
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, R> FN1wrapF1<A, R> mapOnlyIfNotNull(F1<A, R> delegate) {
		return ImmutableFN1wrapF1.of(delegate);
	}

	@Value.Immutable
	abstract class FN1checkNull<A, R> implements FN1<A, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F1<A, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a) {
			Preconditions.checkNotNull(a,"%s: a is null", asHumanReadable());
			return Preconditions.checkNotNull(delegate().apply(a), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, R> FN1checkNull<A, R> checkNull(F1<A, R> delegate) {
		return ImmutableFN1checkNull.of(delegate);
	}

}
