package de.flapdoodle.formula.solver;

import de.flapdoodle.formula.ErrorMessage;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.types.Either;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@org.immutables.value.Value.Immutable
public abstract class Context {
	@org.immutables.value.Value.Default
	protected ValueContainer values() {
		return ValueContainer.empty();
	}
	@org.immutables.value.Value.Default
	protected ValueContainer validatedValues() {
		return ValueContainer.empty();
	}
	protected abstract Set<Value<?>> validatedValuesWithErrors();

	protected abstract Map<Value<?>, List<ErrorMessage>> errorMessages();

	public <T> Context addValue(Value<T> id, @Nullable T value) {
		return ImmutableContext.copyOf(this).withValues(values().add(id, value));
	}

	public <T> @Nullable T getValue(Value<T> id) {
		return values().get(id);
	}

	public <T> Context addValidated(Value<T> id, Either<T, List<ErrorMessage>> valueOrErrorMessages) {
		return valueOrErrorMessages.map(value -> ImmutableContext.copyOf(this)
			.withValidatedValues(validatedValues().add(id, value)), errors -> ImmutableContext.builder()
			.from(this)
			.validatedValues(validatedValues().add(id, null))
			.addValidatedValuesWithErrors(id)
			.putErrorMessages(id, errors)
			.build());
	}

	public <T> @Nullable T getValidated(Value<T> id) {
		return validatedValues().get(id);
	}

	public boolean hasValidationErrors(Value<?> id) {
		return validatedValuesWithErrors().contains(id);
	}

	public static Context empty() {
		return ImmutableContext.builder()
			.build();
	}
}
