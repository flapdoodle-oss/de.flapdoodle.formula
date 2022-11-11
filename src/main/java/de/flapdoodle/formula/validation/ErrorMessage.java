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

import de.flapdoodle.formula.ValueSource;
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

	static ErrorMessage of(String key, Object ... arg) {
		return ImmutableErrorMessage.builder()
			.key(key)
			.addArgs(arg)
			.build();
	}

	static ImmutableErrorMessage.Builder builder() {
		return ImmutableErrorMessage.builder();
	}
}
