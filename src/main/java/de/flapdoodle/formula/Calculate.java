package de.flapdoodle.formula;

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

		public Calculation.Direct<X, X> from(ValueSource<X> a) {
			return new WithDirect<>(destination, a).by(Transformations.identity());
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
	}

	public static class WithDirect<X, A> {
		private final ValueSink<X> destination;
		private final ValueSource<A> a;

		public WithDirect(ValueSink<X> destination, ValueSource<A> a) {
			this.destination = destination;
			this.a = a;
		}

		public Calculation.Direct<A, X> by(Transformations.F1<A, X> transformation) {
			return Calculation.Direct.with(a, destination, transformation);
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

		public Calculation.Merge2<A, B, X> by(Transformations.F2<A,B,X> transformation) {
			return Calculation.Merge2.with(a, b, destination, transformation);
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

		public Calculation.Merge3<A, B, C, X> by(Transformations.F3<A,B,C,X> transformation) {
			return Calculation.Merge3.with(a, b, c, destination, transformation);
		}
	}

}
