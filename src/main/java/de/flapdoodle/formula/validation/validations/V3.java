package de.flapdoodle.formula.validation.validations;

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.ValidatedValue;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface V3<T, A, B, C> {
	List<ErrorMessage> validate(@Nonnull Optional<T> value, @Nonnull ValidatedValue<A> a, @Nonnull ValidatedValue<B> b, @Nonnull ValidatedValue<C> c);

	@Value.Immutable
	abstract class V3Explained<T, A, B, C> implements V3<T, A, B, C>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract V3<T, A, B, C> delegate();

		@Value.Parameter
		protected abstract String humanReadable();

		@Override
		@Value.Auxiliary
		@Nonnull public List<ErrorMessage> validate(@Nonnull Optional<T> value, @Nonnull ValidatedValue<A> a, @Nonnull ValidatedValue<B> b, @Nonnull ValidatedValue<C> c) {
			return delegate().validate(value, a, b, c);
		}

		@Override
		public String asHumanReadable() {
			return humanReadable();
		}
	}

	static <T, A, B, C> V3<T, A, B, C> withLabel(V3<T, A, B, C> delegate, String label) {
		return ImmutableV3Explained.of(delegate, label);
	}
}
