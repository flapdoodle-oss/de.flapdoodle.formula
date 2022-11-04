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
package de.flapdoodle.formula.calculate;

import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;

import java.util.List;

public abstract class Calculate {
	private Calculate() {
		// no instance
	}

	public static <X> WithDestination<X> value(ValueSink<X> destination) {
		return new WithDestination<>(destination);
	}

	public static class WithDestination<X> {
		private final ValueSink<X> destination;

		private WithDestination(ValueSink<X> destination) {
			this.destination = destination;
		}

		public Calculations.Direct<X, X> from(ValueSource<X> a) {
			return new WithDirect<>(destination, a).by(Calculations.identity());
		}

		public <A> WithDirect<X, A> using(ValueSource<A> a) {
			return new WithDirect<>(destination, a);
		}

		public <A, B> WithMerge2<X, A, B> using(ValueSource<A> a, ValueSource<B> b) {
			return new WithMerge2<>(destination, a, b);
		}

		public <A, B, C> WithMerge3<X, A, B, C> using(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			return new WithMerge3<>(destination, a, b, c);
		}

		public <S> WithSources<X, S> aggregating(List<? extends ValueSource<S>> sources) {
			return new WithSources<>(destination, sources);
		}
	}

	public static class WithDirect<X, A> {
		private final ValueSink<X> destination;
		private final ValueSource<A> a;

		public WithDirect(ValueSink<X> destination, ValueSource<A> a) {
			this.destination = destination;
			this.a = a;
		}

		public Calculations.Direct<A, X> by(Calculations.F1<A, X> transformation) {
			return Calculations.Direct.with(a, destination, transformation);
		}
	}

	public static class WithMerge2<X , A, B> {
		private final ValueSink<X> destination;
		private final ValueSource<A> a;
		private final ValueSource<B> b;

		public WithMerge2(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b) {
			this.destination = destination;
			this.a = a;
			this.b = b;
		}

		public Calculations.Merge2<A, B, X> by(Calculations.F2<A,B,X> transformation) {
			return Calculations.Merge2.with(a, b, destination, transformation);
		}
	}

	public static class WithMerge3<X , A, B, C> {
		private final ValueSink<X> destination;
		private final ValueSource<A> a;
		private final ValueSource<B> b;
		private final ValueSource<C> c;

		public WithMerge3(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			this.destination = destination;
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public Calculations.Merge3<A, B, C, X> by(Calculations.F3<A,B,C,X> transformation) {
			return Calculations.Merge3.with(a, b, c, destination, transformation);
		}
	}

	public static class WithSources<X , S> {
		private final ValueSink<X> destination;
		private final List<? extends ValueSource<S>> sourceList;

		public WithSources(ValueSink<X> destination, List<? extends ValueSource<S>> sourceList) {
			this.destination = destination;
			this.sourceList = sourceList;
		}

		public Calculations.Aggregated<S, X> by(Calculations.F1<List<S>,X> aggregation) {
			return Calculations.Aggregated.with(sourceList, destination, aggregation);
		}
	}
}
