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
package de.flapdoodle.formula.validation;

import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSink;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.validation.validations.*;

public abstract class Validate {
	private Validate() {
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

		public Self<X> by(V0<X> validator) {
			return Self.with(destination, validator);
		}
		public Self<X> by(V0<X> validator, String description) {
			return Self.with(destination, V0.withLabel(validator, description));
		}

		public <A> WithRelationTo1<X, A> using(ValueSource<A> a) {
			return new WithRelationTo1<>(destination, a);
		}
		public <A, B> WithRelationTo2<X, A, B> using(ValueSource<A> a, ValueSource<B> b) {
			return new WithRelationTo2<>(destination, a, b);
		}
		public <A, B, C> WithRelationTo3<X, A, B, C> using(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			return new WithRelationTo3<>(destination, a, b, c);
		}
		public <A, B, C, D> WithRelationTo4<X, A, B, C, D> using(ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			return new WithRelationTo4<>(destination, a, b, c, d);
		}
	}

	public static class WithRelationTo1<X, A> {
		private Value<X> destination;
		private ValueSource<A> a;

		public WithRelationTo1(Value<X> destination, ValueSource<A> a) {
			this.destination = destination;
			this.a = a;
		}

		public RelatedTo1<X, A> by(V1<X, A> validator) {
			return RelatedTo1.with(destination, a, validator);
		}

		public RelatedTo1<X, A> by(V1<X, A> validator, String description) {
			return RelatedTo1.with(destination, a, V1.withLabel(validator, description));
		}
	}

	public static class WithRelationTo2<X, A, B> {
		private Value<X> destination;
		private ValueSource<A> a;
		private ValueSource<B> b;

		public WithRelationTo2(Value<X> destination, ValueSource<A> a, ValueSource<B> b) {
			this.destination = destination;
			this.a = a;
			this.b = b;
		}

		public RelatedTo2<X, A, B> by(V2<X, A, B> validator) {
			return RelatedTo2.with(destination, a, b, validator);
		}

		public RelatedTo2<X, A, B> by(V2<X, A, B> validator, String description) {
			return RelatedTo2.with(destination, a, b, V2.withLabel(validator, description));
		}
	}

	public static class WithRelationTo3<X, A, B, C> {
		private Value<X> destination;
		private ValueSource<A> a;
		private ValueSource<B> b;
		private ValueSource<C> c;

		public WithRelationTo3(Value<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c) {
			this.destination = destination;
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public RelatedTo3<X, A, B, C> by(V3<X, A, B, C> validator) {
			return RelatedTo3.with(destination, a, b, c,  validator);
		}

		public RelatedTo3<X, A, B, C> by(V3<X, A, B, C> validator, String description) {
			return RelatedTo3.with(destination, a, b, c, V3.withLabel(validator, description));
		}
	}

	public static class WithRelationTo4<X, A, B, C, D> {
		private Value<X> destination;
		private ValueSource<A> a;
		private ValueSource<B> b;
		private ValueSource<C> c;
		private ValueSource<D> d;

		public WithRelationTo4(Value<X> destination, ValueSource<A> a, ValueSource<B> b, ValueSource<C> c, ValueSource<D> d) {
			this.destination = destination;
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		public RelatedTo4<X, A, B, C, D> by(V4<X, A, B, C, D> validator) {
			return RelatedTo4.with(destination, a, b, c,d,   validator);
		}

		public RelatedTo4<X, A, B, C, D> by(V4<X, A, B, C, D> validator, String description) {
			return RelatedTo4.with(destination, a, b, c, d, V4.withLabel(validator, description));
		}
	}
}
