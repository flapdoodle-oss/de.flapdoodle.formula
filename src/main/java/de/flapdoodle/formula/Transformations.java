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
package de.flapdoodle.formula;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Transformations {
	static <X> F1<X, X> identity() {
		return a -> a;
	}

	@FunctionalInterface
	interface F1<A, R> extends Function<A, R> {
		@Override @Nullable R apply(@Nullable A a);
	}

	@FunctionalInterface
	interface F2<A, B, R> extends BiFunction<A, B, R> {
		@Override @Nullable R apply(@Nullable A a,@Nullable B b);
	}

	@FunctionalInterface
	interface F3<A, B, C, R> {
		@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c);
	}
}
