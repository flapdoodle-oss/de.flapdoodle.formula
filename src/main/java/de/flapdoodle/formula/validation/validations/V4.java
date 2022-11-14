package de.flapdoodle.formula.validation.validations;

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.ValidatedValue;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface V4<T, A, B, C, D> {
	List<ErrorMessage> validate(@Nonnull Optional<T> value, @Nonnull ValidatedValue<A> a, @Nonnull ValidatedValue<B> b, @Nonnull ValidatedValue<C> c, @Nonnull ValidatedValue<D> d);

	@Value.Immutable
	abstract class V4Explained<T, A, B, C, D> implements V4<T, A, B, C, D>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract V4<T, A, B, C, D> delegate();

		@Value.Parameter
		protected abstract String humanReadable();

		@Override
		@Value.Auxiliary
		@Nonnull public List<ErrorMessage> validate(@Nonnull Optional<T> value, @Nonnull ValidatedValue<A> a, @Nonnull ValidatedValue<B> b, @Nonnull ValidatedValue<C> c, @Nonnull ValidatedValue<D> d) {
			return delegate().validate(value, a, b, c, d);
		}

		@Override
		public String asHumanReadable() {
			return humanReadable();
		}
	}

	static <T, A, B, C, D> V4<T, A, B, C, D> withLabel(V4<T, A, B, C, D> delegate, String label) {
		return ImmutableV4Explained.of(delegate, label);
	}
}
