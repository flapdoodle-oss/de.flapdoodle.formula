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
