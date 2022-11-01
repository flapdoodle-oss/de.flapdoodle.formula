/*
 * Copyright (C) 2011
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
import de.flapdoodle.formula.*;
import de.flapdoodle.formula.types.Either;
import de.flapdoodle.graph.Graphs;
import de.flapdoodle.graph.VerticesAndEdges;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@org.immutables.value.Value.Immutable
public abstract class Solver {

	@org.immutables.value.Value.Default
	protected Function<Context, Calculation.ValueLookup> calculationLookupFactory() {
		return context -> context::getValidated;
	}

	@org.immutables.value.Value.Default
	protected Function<Context, Validation.ValueLookup> validationLookupFactory() {
		return context -> validationLookup(context);
	}

	public static ImmutableSolver.Builder builder() {
		return ImmutableSolver.builder();
	}

	public static Solver instance() {
		return builder().build();
	}

	public Context solve(ValueGraph valueGraph) {
		Collection<VerticesAndEdges<Value<?>, DefaultEdge>> roots = Graphs.rootsOf(valueGraph.graph());
		Context context = Context.empty();

		for (VerticesAndEdges<Value<?>, DefaultEdge> it : roots) {
			Preconditions.checkArgument(it.loops().isEmpty(), "loops found: %s", it.loops());

			for (Value<?> node : it.vertices()) {
				context = process(valueGraph, node, context);
			}
		}

		return context;
	}

	private Context process(ValueGraph valueGraph, Value<?> node, Context context) {
		if (node instanceof Unvalidated) return processUnvalidated((Unvalidated<?>) node, context);
		return processProcessed(valueGraph, node, context);
	}

	private <T> Context processProcessed(ValueGraph valueGraph, Value<T> destination, Context context) {
		Calculation<T> calculation = valueGraph.calculationOrNull(destination);
		Validation<T> validation = valueGraph.validationOrNull(destination);

		if (calculation!=null || validation != null) {
			Calculation.ValueLookup valueLookup = calculationLookupFactory().apply(context);

			T calculated = calculation != null
				? calculation.calculate(valueLookup)
				: valueLookup.get(destination);

			Either<T, List<ErrorMessage>> validated = validation != null
				? validate(context, validation, calculated)
				: Either.left(calculated);

			return context.addValidated(destination, validated);
		} else
			return context;
	}

	private <T> Either<T, List<ErrorMessage>> validate(Context context, Validation<T> validation, T calculated) {
		List<ErrorMessage> errorMessages = validation.validate(validator(), Optional.ofNullable(calculated), validationLookupFactory().apply(context));
		return errorMessages.isEmpty()
			? Either.left(calculated)
			: Either.right(errorMessages);
	}

	private static Validator validator() {
		return new Validator() {
		};
	}

	private static Validation.ValueLookup validationLookup(Context context) {
		return new Validation.ValueLookup() {
			@Override
			public <T> Validator.ValidatedValue<T> get(ValueSource<T> id) {
				return id instanceof Unvalidated
					? Validator.ValidatedValue.builder(id)
					.value(Optional.ofNullable(context.getValue(id)))
					.build()
					: Validator.ValidatedValue.builder(id)
					.value(Optional.ofNullable(context.getValidated(id)))
					.invalidReferences(invalidReferences(context, id))
					.build();
			}
		};
	}

	private static <T> Iterable<? extends ValueSource<?>> invalidReferences(Context context, ValueSource<T> id) {
		return context.hasValidationErrors(id) ? ImmutableList.of(id) : ImmutableSet.of();
	}

	private <T> Context processUnvalidated(Unvalidated<T> node, Context context) {
		T value = calculationLookupFactory().apply(context).get(node);
		return context.addValue(node, value);
	}
}
