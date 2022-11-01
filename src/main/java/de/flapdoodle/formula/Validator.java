package de.flapdoodle.formula;

import org.immutables.builder.Builder;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Validator {

	@Value.Immutable
	abstract class ValidatedValue<T> {
		@Builder.Parameter
		public abstract ValueSource<T> source();
		public abstract Optional<T> value();
		public abstract Set<ValueSource<?>> invalidReferences();

		public static <T> ImmutableValidatedValue.Builder<T> builder(ValueSource<T> source) {
			return ImmutableValidatedValue.builder(source);
		}
	}


	interface Self<T> {
		List<ErrorMessage> validate(Validator validator, Optional<T> value);
	}

	interface RelatedTo1<T, A> {
		List<ErrorMessage> validate(Validator validator,Optional<T> value, ValidatedValue<A> a);
	}

	interface RelatedTo2<T, A, B> {
		List<ErrorMessage> validate(Validator validator,Optional<T> value, ValidatedValue<A> a, ValidatedValue<B> b);
	}
}
