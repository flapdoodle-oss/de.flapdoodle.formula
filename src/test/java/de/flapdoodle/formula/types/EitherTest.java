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
package de.flapdoodle.formula.types;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class EitherTest {
	@Test
	void left() {
		Either<String, Integer> testee = Either.left("left");

		assertThat(testee.isLeft()).isTrue();
		assertThat(testee.left()).isEqualTo("left");
		assertThatThrownBy(testee::right)
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void leftMap() {
		Either<String, Integer> testee = Either.left("left");
		Either<String, Integer> mapLeft = testee.mapLeft(s -> "[" + s + "]");
		Either<String, Integer> mapRight = testee.mapRight(s -> fail("must not be called"));

		assertThat(mapLeft.left()).isEqualTo("[left]");
		assertThatThrownBy(mapLeft::right)
			.isInstanceOf(NoSuchElementException.class);

		assertThat(mapRight.left()).isEqualTo("left");
		assertThatThrownBy(mapRight::right)
			.isInstanceOf(NoSuchElementException.class);

		String mapBoth = testee.map(l -> "<" + l + ">", s -> fail("must not be called"));
		assertThat(mapBoth).isEqualTo("<left>");
	}

	@Test
	void right() {
		Either<String, Integer> testee = Either.right(2);

		assertThat(testee.isLeft()).isFalse();
		assertThat(testee.right()).isEqualTo(2);
		assertThatThrownBy(testee::left)
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void rightMap() {
		Either<String, Integer> testee = Either.right(2);
		Either<String, Integer> mapLeft = testee.mapLeft(s -> fail("must not be called"));
		Either<String, Integer> mapRight = testee.mapRight(s -> s + 2);

		assertThatThrownBy(mapLeft::left)
			.isInstanceOf(NoSuchElementException.class);
		assertThat(mapLeft.right()).isEqualTo(2);

		assertThatThrownBy(mapRight::left)
			.isInstanceOf(NoSuchElementException.class);
		assertThat(mapRight.right()).isEqualTo(4);

		Integer mapBoth = testee.map(l -> fail("must not be called"), s -> s + 3);
		assertThat(mapBoth).isEqualTo(5);
	}
}