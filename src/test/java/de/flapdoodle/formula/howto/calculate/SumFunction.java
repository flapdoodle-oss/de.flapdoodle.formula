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
package de.flapdoodle.formula.howto.calculate;

import de.flapdoodle.formula.calculate.functions.FN2;

import javax.annotation.Nullable;

public class SumFunction implements FN2<Integer, Integer, Integer> {
	private SumFunction() {
		// no instance
	}

	@Nullable @Override
	public Integer apply(@Nullable Integer a, @Nullable Integer b) {
		return (a != null && b != null)
			? a + b
			: null;
	}

	private final static SumFunction INSTANCE=new SumFunction();

	public static SumFunction getInstance() {
		return INSTANCE;
	}
}
