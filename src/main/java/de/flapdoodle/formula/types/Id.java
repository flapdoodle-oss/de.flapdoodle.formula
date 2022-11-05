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

import com.google.common.collect.Maps;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class Id<O> implements HasHumanReadableLabel {
	@Value.Parameter
	protected abstract Class<O> type();

	@Value.Parameter
	protected abstract int count();

	@Value.Auxiliary
	public Optional<O> asInstance(Object value) {
		return type().isInstance(value)
			? Optional.of((O) value)
			: Optional.empty();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"{type="+type().getSimpleName()+", count="+count()+"}";
	}

	@Override
	public String asHumanReadable() {
		return type().getSimpleName()+"#"+count();
	}
	
	private static TypeCounter typeCounter = new TypeCounter();
	private static ThreadLocal<TypeCounter> localTypeCounter = new InheritableThreadLocal<>();

	public static <O> Id<O> idFor(Class<O> type) {
		TypeCounter currentCounter = localTypeCounter.get();
		return ImmutableId.of(type, (currentCounter!=null ? currentCounter : typeCounter).count(type));
	}

	public static ClearTypeCounter with(TypeCounter typeCounter) {
		localTypeCounter.set(typeCounter);
		return () -> localTypeCounter.set(null);
	}

	public interface ClearTypeCounter extends AutoCloseable {
		@Override void close();
	}
}