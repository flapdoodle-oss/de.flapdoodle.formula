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
package de.flapdoodle.formula.solver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.Calculation;
import de.flapdoodle.formula.types.Either;
import de.flapdoodle.formula.validation.ErrorMessage;
import de.flapdoodle.formula.validation.ValidatedValue;
import de.flapdoodle.formula.validation.Validation;
import de.flapdoodle.formula.validation.Validator;
import de.flapdoodle.graph.Graphs;
import de.flapdoodle.graph.VerticesAndEdges;
import org.immutables.value.Value.Immutable;
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class Solver {

	private Solver() {
		// no instance
	}

	public interface ValueLookup {
		<T> @Nullable T get(Value<T> id);
	}

	public static Context solve(ValueGraph valueGraph, ValueLookup lookup) {
		Collection<VerticesAndEdges<Value<?>, DefaultEdge>> roots = Graphs.rootsOf(valueGraph.graph());

		Context context = Context.empty();

		for (VerticesAndEdges<Value<?>, DefaultEdge> it : roots) {
			Preconditions.checkArgument(it.loops().isEmpty(), "loops found: %s", it.loops());

			for (Value<?> node : it.vertices()) {
				context = process(lookup, valueGraph, node, context);
			}
		}

		return context;
	}

	private static Context process(
		ValueLookup lookup,
		ValueGraph valueGraph,
		Value<?> node,
		Context context
	) {
		if (node instanceof Unvalidated) {
			return processUnvalidated(lookup, (Unvalidated<?>) node, context);
		}
		return processProcessed(lookup, valueGraph, node, context);
	}

	private static <T> Context processProcessed(ValueLookup lookup, ValueGraph valueGraph, Value<T> destination, Context context) {
		Calculation<T> calculation = valueGraph.calculationOrNull(destination);
		Validation<T> validation = valueGraph.validationOrNull(destination);

		Calculation.ValueLookup calculationLookup = calculationLookup(context, lookup);
		Validation.ValueLookup validationLookup = validationValueLookup(context, lookup);

		if (calculation != null || validation != null) {
			T calculated = calculation != null
				? calculation.calculate(calculationLookup)
				: calculationLookup.get(destination);

			Either<T, List<ErrorMessage>> validated = validation != null
				? validate(validationLookup, validation, calculated)
				: Either.left(calculated);

			return context.addValidated(destination, validated);
		} else
			return context;
	}

	private static Calculation.ValueLookup calculationLookup(Context context, ValueLookup lookup) {
		return new Calculation.ValueLookup() {
			@Override public <T> @Nullable T get(Value<T> id) {
				return value(context,lookup,id);
			}
		};
	}

	private static <T> @Nullable T value(Context context, ValueLookup lookup, Value<T> id) {
		return context.hasValidated(id)
			? context.getValidated(id)
			: lookup.get(id);
	}

	private static Validation.ValueLookup validationValueLookup(Context context, ValueLookup lookup) {
		return new Validation.ValueLookup() {
			@Override public <T> ValidatedValue<T> get(ValueSource<T> id) {
				return id instanceof Unvalidated
					? ValidatedValue.builder(id)
					.value(Optional.ofNullable(context.getValue(id)))
					.build()
					: ValidatedValue.builder(id)
					.value(Optional.ofNullable(value(context, lookup, id)))
					.invalidReferences(invalidReferences(context, id))
					.build();
			}
		};
	}

	private static <T> Either<T, List<ErrorMessage>> validate(Validation.ValueLookup valueLookup, Validation<T> validation, T calculated) {
		List<ErrorMessage> errorMessages = validation.validate(validator(), Optional.ofNullable(calculated), valueLookup);
		return errorMessages.isEmpty()
			? Either.left(calculated)
			: Either.right(errorMessages);
	}

	private static <T> Iterable<? extends ValueSource<?>> invalidReferences(Context context, ValueSource<T> id) {
		return context.hasValidationErrors(id) ? ImmutableList.of(id) : ImmutableSet.of();
	}

	private static <T> Context processUnvalidated(ValueLookup lookup, Unvalidated<T> node, Context context) {
		return context.addValue(node, lookup.get(node.wrapped()));
	}

	private static Validator validator() {
		return new Validator() {
		};
	}
}
