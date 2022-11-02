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
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@org.immutables.value.Value.Immutable
public abstract class Solver {

	@org.immutables.value.Value.Default
	protected Function<ContextView, Calculation.ValueLookup> calculationLookupFactory() {
		return context -> context::getValidated;
	}

	@org.immutables.value.Value.Default
	protected Function<ContextView, Validation.ValueLookup> validationLookupFactory() {
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
		ContextHolder view=new ContextHolder(context);
		
		Calculation.ValueLookup valueLookup = calculationLookupFactory().apply(view);
		Validation.ValueLookup validationValueLookup = validationLookupFactory().apply(view);

		for (VerticesAndEdges<Value<?>, DefaultEdge> it : roots) {
			Preconditions.checkArgument(it.loops().isEmpty(), "loops found: %s", it.loops());

			for (Value<?> node : it.vertices()) {
				context = process(valueLookup, validationValueLookup, valueGraph, node, context);
				view.update(context);
			}
		}

		return context;
	}

	private static Context process(
		Calculation.ValueLookup valueLookup,
		Validation.ValueLookup validationValueLookup,
		ValueGraph valueGraph,
		Value<?> node,
		Context context
	) {
		if (node instanceof Unvalidated) {
			return processUnvalidated(valueLookup, (Unvalidated<?>) node, context);
		}
		return processProcessed(valueLookup, validationValueLookup, valueGraph, node, context);
	}

	private static <T> Context processProcessed(Calculation.ValueLookup valueLookup, Validation.ValueLookup validationValueLookup, ValueGraph valueGraph, Value<T> destination, Context context) {
		Calculation<T> calculation = valueGraph.calculationOrNull(destination);
		Validation<T> validation = valueGraph.validationOrNull(destination);

		if (calculation != null || validation != null) {
			T calculated = calculation != null
				? calculation.calculate(valueLookup)
				: valueLookup.get(destination);

			Either<T, List<ErrorMessage>> validated = validation != null
				? validate(validationValueLookup, validation, calculated)
				: Either.left(calculated);

			return context.addValidated(destination, validated);
		} else
			return context;
	}

	private static <T> Either<T, List<ErrorMessage>> validate(Validation.ValueLookup valueLookup, Validation<T> validation, T calculated) {
		List<ErrorMessage> errorMessages = validation.validate(validator(), Optional.ofNullable(calculated), valueLookup);
		return errorMessages.isEmpty()
			? Either.left(calculated)
			: Either.right(errorMessages);
	}

	private static Validator validator() {
		return new Validator() {
		};
	}

	private static Validation.ValueLookup validationLookup(ContextView context) {
		return new Validation.ValueLookup() {
			@Override
			public <T> ValidatedValue<T> get(ValueSource<T> id) {
				return id instanceof Unvalidated
					? ValidatedValue.builder(id)
					.value(Optional.ofNullable(context.getValue(id)))
					.build()
					: ValidatedValue.builder(id)
					.value(Optional.ofNullable(context.getValidated(id)))
					.invalidReferences(invalidReferences(context, id))
					.build();
			}
		};
	}

	private static <T> Iterable<? extends ValueSource<?>> invalidReferences(ContextView context, ValueSource<T> id) {
		return context.hasValidationErrors(id) ? ImmutableList.of(id) : ImmutableSet.of();
	}

	private static <T> Context processUnvalidated(Calculation.ValueLookup valueLookup, Unvalidated<T> node, Context context) {
		return context.addValue(node, valueLookup.get(node));
	}

	private static class ContextHolder implements ContextView {

		private ContextView delegate;

		public ContextHolder(ContextView delegate) {
			this.delegate = delegate;
		}

		void update(ContextView delegate) {
			this.delegate = delegate;
		}

		@Override
		public <T> @Nullable T getValue(Value<T> id) {
			return delegate.getValue(id);
		}
		@Override
		public <T> @Nullable T getValidated(Value<T> id) {
			return delegate.getValidated(id);
		}
		@Override
		public boolean hasValidationErrors(Value<?> id) {
			return delegate.hasValidationErrors(id);
		}
	}
}
