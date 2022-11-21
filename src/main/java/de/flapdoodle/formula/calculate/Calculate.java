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
import de.flapdoodle.formula.calculate.calculations.*;
import de.flapdoodle.formula.calculate.functions.*;

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

		public Map1<X, X> from(ValueSource<X> a) {
			return new WithMap1Nullable<>(destination, a).by(FN1.identity());
		}

		public <A> WithMap1<X, A> requiring(ValueSource<A> a) {
			return new WithMap1<>(destination, a);
		}
		public <A> WithMap1Nullable<X, A> using(ValueSource<A> a) {
			return new WithMap1Nullable<>(destination, a);
		}

		public <A, B> WithMerge2<X, A, B> requiring(ValueSource<A> a, ValueSource<B> b) {
			return new WithMerge2<>(destination, a, b);
		}
		public <A, B> WithMerge2Nullables<X, A, B> using(ValueSource<A> a, ValueSource<B> b) {
			return new WithMerge2Nullables<>(destination, a, b);
		}

		public <A, B, C> WithMerge3<X, A, B, C> requiring(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			return new WithMerge3<>(destination, a, b, c);
		}
		public <A, B, C> WithMerge3Nullables<X, A, B, C> using(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			return new WithMerge3Nullables<>(destination, a, b, c);
		}

		public <A, B, C, D> WithMerge4<X, A, B, C, D> requiring(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			return new WithMerge4<>(destination, a, b, c, d);
		}
		public <A, B, C, D> WithMerge4Nullables<X, A, B, C, D> using(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			return new WithMerge4Nullables<>(destination, a, b, c, d);
		}

		public <A, B, C, D, E> WithMerge5<X, A, B, C, D, E> requiring(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d, ValueSource<E> e) {
			return new WithMerge5<>(destination, a, b, c, d, e);
		}
		public <A, B, C, D, E> WithMerge5Nullables<X, A, B, C, D, E> using(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d,
			ValueSource<E> e) {
			return new WithMerge5Nullables<>(destination, a, b, c, d, e);
		}

		public <S> WithSourcesNullable<X, S> aggregating(List<? extends ValueSource<S>> sources) {
			return new WithSourcesNullable<>(destination, sources);
		}
	}

	protected abstract static class AbstractWithDirect<X, A> {
		protected final ValueSink<X> destination;
		protected final ValueSource<A> a;

		protected AbstractWithDirect(ValueSink<X> destination, ValueSource<A> a) {
			this.destination = destination;
			this.a = a;
		}
	}

	public static class WithMap1<X, A> extends AbstractWithDirect<X, A> {
		public WithMap1(ValueSink<X> destination, ValueSource<A> a) {
			super(destination, a);
		}

		public Map1<A, X> by(F1<A, X> transformation) {
			return Map1.with(a, destination, FN1.checkNull(transformation, a));
		}

		public Map1<A, X> by(F1<A, X> transformation, String description) {
			return Map1.with(a, destination, FN1.checkNull(F1.withLabel(transformation, description), a));
		}
	}

	public static class WithMap1Nullable<X, A> extends AbstractWithDirect<X, A> {
		public WithMap1Nullable(ValueSink<X> destination, ValueSource<A> a) {
			super(destination, a);
		}

		public Map1<A, X> by(FN1<A, X> transformation) {
			return Map1.with(a, destination, transformation);
		}

		public Map1<A, X> by(FN1<A, X> transformation, String description) {
			return Map1.with(a, destination, FN1.withLabel(transformation, description));
		}

		public Map1<A, X> ifAllSetBy(F1<A, X> transformation) {
			return Map1.with(a, destination, FN1.mapOnlyIfNotNull(transformation));
		}

		public Map1<A, X> ifAllSetBy(F1<A, X> transformation, String description) {
			return Map1.with(a, destination, FN1.mapOnlyIfNotNull(F1.withLabel(transformation, description)));
		}
	}

	protected static abstract class WithMerge2Abstract<X, A, B> {
		protected final ValueSink<X> destination;
		protected final ValueSource<A> a;
		protected final ValueSource<B> b;

		protected WithMerge2Abstract(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b) {
			this.destination = destination;
			this.a = a;
			this.b = b;
		}
	}

	public static class WithMerge2<X, A, B> extends WithMerge2Abstract<X, A, B> {
		public WithMerge2(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b) {
			super(destination, a, b);
		}

		public Merge2<A, B, X> by(F2<A, B, X> transformation) {
			return Merge2.with(a, b, destination, FN2.checkNull(transformation, a, b));
		}

		public Merge2<A, B, X> by(F2<A, B, X> transformation, String description) {
			return Merge2.with(a, b, destination, FN2.checkNull(F2.withLabel(transformation, description), a, b));
		}
	}

	public static class WithMerge2Nullables<X, A, B> extends WithMerge2Abstract<X, A, B> {
		public WithMerge2Nullables(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b) {
			super(destination, a, b);
		}

		public Merge2<A, B, X> by(FN2<A, B, X> transformation) {
			return Merge2.with(a, b, destination, transformation);
		}

		public Merge2<A, B, X> by(FN2<A, B, X> transformation, String description) {
			return Merge2.with(a, b, destination, FN2.withLabel(transformation, description));
		}

		public Merge2<A, B, X> ifAllSetBy(F2<A, B, X> transformation) {
			return Merge2.with(a, b, destination, FN2.mapOnlyIfNotNull(transformation));
		}

		public Merge2<A, B, X> ifAllSetBy(F2<A, B, X> transformation, String description) {
			return Merge2.with(a, b, destination, FN2.mapOnlyIfNotNull(F2.withLabel(transformation, description)));
		}

	}

	protected static abstract class WithMerge3Abstract<X, A, B, C> {
		protected final ValueSink<X> destination;
		protected final ValueSource<A> a;
		protected final ValueSource<B> b;
		protected final ValueSource<C> c;

		protected WithMerge3Abstract(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			this.destination = destination;
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	public static class WithMerge3<X, A, B, C> extends WithMerge3Abstract<X, A, B, C> {
		public WithMerge3(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			super(destination, a, b, c);
		}

		public Merge3<A, B, C, X> by(F3<A, B, C, X> transformation) {
			return Merge3.with(a, b, c, destination, FN3.checkNull(transformation, a, b, c));
		}

		public Merge3<A, B, C, X> by(F3<A, B, C, X> transformation, String description) {
			return Merge3.with(a, b, c, destination, FN3.checkNull(F3.withLabel(transformation, description), a, b, c));
		}
	}

	public static class WithMerge3Nullables<X, A, B, C> extends WithMerge3Abstract<X, A, B, C> {
		public WithMerge3Nullables(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			super(destination, a, b, c);
		}

		public Merge3<A, B, C, X> by(FN3<A, B, C, X> transformation) {
			return Merge3.with(a, b, c, destination, transformation);
		}

		public Merge3<A, B, C, X> by(FN3<A, B, C, X> transformation, String description) {
			return Merge3.with(a, b, c, destination, FN3.withLabel(transformation, description));
		}

		public Merge3<A, B, C, X> ifAllSetBy(F3<A, B, C, X> transformation) {
			return Merge3.with(a, b, c, destination, FN3.mapOnlyIfNotNull(transformation));
		}

		public Merge3<A, B, C, X> ifAllSetBy(F3<A, B, C, X> transformation, String description) {
			return Merge3.with(a, b, c, destination, FN3.mapOnlyIfNotNull(F3.withLabel(transformation, description)));
		}
	}

	protected static abstract class WithMerge4Abstract<X, A, B, C, D> {
		protected final ValueSink<X> destination;
		protected final ValueSource<A> a;
		protected final ValueSource<B> b;
		protected final ValueSource<C> c;
		protected final ValueSource<D> d;

		protected WithMerge4Abstract(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			this.destination = destination;
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}
	}

	public static class WithMerge4<X, A, B, C, D> extends WithMerge4Abstract<X, A, B, C, D> {
		public WithMerge4(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			super(destination, a, b, c, d);
		}

		public Merge4<A, B, C, D, X> by(F4<A, B, C, D, X> transformation) {
			return Merge4.with(a, b, c, d, destination, FN4.checkNull(transformation, a, b, c, d));
		}

		public Merge4<A, B, C, D, X> by(F4<A, B, C, D, X> transformation, String description) {
			return Merge4.with(a, b, c, d, destination, FN4.checkNull(F4.withLabel(transformation, description), a, b, c, d));
		}
	}

	public static class WithMerge4Nullables<X, A, B, C, D> extends WithMerge4Abstract<X, A, B, C, D> {
		public WithMerge4Nullables(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			super(destination, a, b, c, d);
		}

		public Merge4<A, B, C, D, X> by(FN4<A, B, C, D, X> transformation) {
			return Merge4.with(a, b, c, d, destination, transformation);
		}

		public Merge4<A, B, C, D, X> by(FN4<A, B, C, D, X> transformation, String description) {
			return Merge4.with(a, b, c, d, destination, FN4.withLabel(transformation, description));
		}

		public Merge4<A, B, C, D, X> ifAllSetBy(F4<A, B, C, D, X> transformation) {
			return Merge4.with(a, b, c, d, destination, FN4.mapOnlyIfNotNull(transformation));
		}

		public Merge4<A, B, C, D, X> ifAllSetBy(F4<A, B, C, D, X> transformation, String description) {
			return Merge4.with(a, b, c, d, destination, FN4.mapOnlyIfNotNull(F4.withLabel(transformation, description)));
		}
	}

	protected static abstract class WithMerge5Abstract<X, A, B, C, D, E> {
		protected final ValueSink<X> destination;
		protected final ValueSource<A> a;
		protected final ValueSource<B> b;
		protected final ValueSource<C> c;
		protected final ValueSource<D> d;
		protected final ValueSource<E> e;

		protected WithMerge5Abstract(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d, ValueSource<E> e) {
			this.destination = destination;
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
		}
	}

	public static class WithMerge5<X, A, B, C, D, E> extends WithMerge5Abstract<X, A, B, C, D, E> {
		public WithMerge5(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d, ValueSource<E> e) {
			super(destination, a, b, c, d, e);
		}

		public Merge5<A, B, C, D, E, X> by(F5<A, B, C, D, E, X> transformation) {
			return Merge5.with(a, b, c, d, e, destination, FN5.checkNull(transformation, a, b, c, d, e));
		}

		public Merge5<A, B, C, D, E, X> by(F5<A, B, C, D, E, X> transformation, String description) {
			return Merge5.with(a, b, c, d, e, destination, FN5.checkNull(F5.withLabel(transformation, description), a, b, c, d, e));
		}
	}

	public static class WithMerge5Nullables<X, A, B, C, D, E> extends WithMerge5Abstract<X, A, B, C, D, E> {
		public WithMerge5Nullables(ValueSink<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d, ValueSource<E> e) {
			super(destination, a, b, c, d, e);
		}

		public Merge5<A, B, C, D, E, X> by(FN5<A, B, C, D, E, X> transformation) {
			return Merge5.with(a, b, c, d, e, destination, transformation);
		}

		public Merge5<A, B, C, D, E, X> by(FN5<A, B, C, D, E, X> transformation, String description) {
			return Merge5.with(a, b, c, d, e, destination, FN5.withLabel(transformation, description));
		}

		public Merge5<A, B, C, D, E, X> ifAllSetBy(F5<A, B, C, D, E, X> transformation) {
			return Merge5.with(a, b, c, d, e, destination, FN5.mapOnlyIfNotNull(transformation));
		}

		public Merge5<A, B, C, D, E, X> ifAllSetBy(F5<A, B, C, D, E, X> transformation, String description) {
			return Merge5.with(a, b, c, d, e, destination, FN5.mapOnlyIfNotNull(F5.withLabel(transformation, description)));
		}
	}

	public static class WithSourcesNullable<X, S> {
		private final ValueSink<X> destination;
		private final List<? extends ValueSource<S>> sourceList;

		public WithSourcesNullable(ValueSink<X> destination, List<? extends ValueSource<S>> sourceList) {
			this.destination = destination;
			this.sourceList = sourceList;
		}

		public Aggregated<S, X> by(FN1<List<S>, X> aggregation) {
			return Aggregated.with(sourceList, destination, aggregation);
		}

		public Aggregated<S, X> by(FN1<List<S>, X> aggregation, String description) {
			return Aggregated.with(sourceList, destination, FN1.withLabel(aggregation, description));
		}
	}
}
