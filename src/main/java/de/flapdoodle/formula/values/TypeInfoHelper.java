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
package de.flapdoodle.formula.values;

import de.flapdoodle.formula.types.HasHumanReadableLabel;
import de.flapdoodle.reflection.ClassTypeInfo;
import de.flapdoodle.reflection.ListTypeInfo;
import de.flapdoodle.reflection.TypeInfo;
import de.flapdoodle.types.Pair;

public abstract class TypeInfoHelper {

	public static String asHumanReadable(TypeInfo<?> typeInfo) {
		if (typeInfo instanceof ClassTypeInfo) {
			return ((ClassTypeInfo<?>) typeInfo).type().getSimpleName();
		}
		if (typeInfo instanceof Pair.PairTypeInfo) {
			Pair.PairTypeInfo<?, ?> pair = (Pair.PairTypeInfo<?, ?>) typeInfo;
			return "Pair("+asHumanReadable(pair.first())+", "+asHumanReadable(pair.second())+")";
		}
		if (typeInfo instanceof ListTypeInfo) {
			ListTypeInfo<?> list = (ListTypeInfo<?>) typeInfo;
			return "List("+asHumanReadable(list.elements())+")";
		}
		if (typeInfo instanceof HasHumanReadableLabel) {
			return ((HasHumanReadableLabel) typeInfo).asHumanReadable();
		}
		return typeInfo.toString();
	}
}
