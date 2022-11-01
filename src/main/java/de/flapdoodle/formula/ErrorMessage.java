package de.flapdoodle.formula;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Value.Immutable
public interface ErrorMessage {
	String key();

	@Value.Default
	default List<Object> args() {
		return Collections.emptyList();
	}

	@Value.Default
	default Set<ValueSource<?>> invalidSources() {
		return Collections.emptySet();
	}

	static ErrorMessage of(String key, Object arg) {
		return ImmutableErrorMessage.builder()
			.key(key)
			.addArgs(arg)
			.build();
	}

	static ImmutableErrorMessage.Builder builder() {
		return ImmutableErrorMessage.builder();
	}
}
