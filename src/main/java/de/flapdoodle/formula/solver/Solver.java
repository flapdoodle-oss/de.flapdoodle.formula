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
import com.google.common.collect.Sets;
import de.flapdoodle.formula.Unvalidated;
import de.flapdoodle.formula.Value;
import de.flapdoodle.formula.ValueSource;
import de.flapdoodle.formula.calculate.*;
import de.flapdoodle.formula.validation.*;
import de.flapdoodle.graph.Graphs;
import de.flapdoodle.graph.VerticesAndEdges;
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Solver {

	private Solver() {
		// no instance
	}

	public static Result solve(ValueGraph valueGraph, ValueLookup lookup) {
		return solve(Context.empty(), valueGraph, lookup).asResult();
	}

	static Context solve(Context context, ValueGraph valueGraph, ValueLookup lookup) {
		Collection<VerticesAndEdges<Value<?>, DefaultEdge>> roots = Graphs.rootsOf(valueGraph.graph());
		if (lookup instanceof HasSetOfKnownValues) {
			Set<Value<?>> providedValuesSet = ((HasSetOfKnownValues) lookup).keySet();

			Set<Value<?>> calculationDestinations = roots.stream()
				.flatMap(it -> it.vertices().stream())
				.map(valueGraph::calculationOrNull)
				.filter(Objects::nonNull)
				.map(Calculation::destination)
				.collect(Collectors.toSet());

			Set<Value<?>> shadowedValuesFromLookup = Sets.intersection(providedValuesSet, calculationDestinations);

			Preconditions.checkArgument(shadowedValuesFromLookup.isEmpty(),"value lookup values are shadowed by calculations: %s", shadowedValuesFromLookup);
		}

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
		return processValue(lookup, valueGraph, node, context);
	}

	private static <T> Context processValue(ValueLookup lookup, ValueGraph valueGraph, Value<T> destination, Context context) {
		Calculation<T> calculation = valueGraph.calculationOrNull(destination);

		T calculated;
		Context withCalculationSources;

		if (calculation != null) {
			List<MappedValue<?>> entries = calculation.sources().stream()
				.map(source -> mappedValue(context, lookup, source))
				.collect(Collectors.toList());

			StrictValueLookup calculationLookup = StrictValueLookup.of(entries);
			calculated = calculation.calculate(calculationLookup);
			withCalculationSources = context.addIfNotExist(entries);
		} else {
			calculated = value(context, lookup, destination);
			withCalculationSources = context;
		}

		Context withValidation;
		Validation<T> validation = valueGraph.validationOrNull(destination);
		if (validation != null) {
			List<ValidatedValue<?>> sources = validation.sources().stream()
				.map(source -> validatedValue(withCalculationSources, source))
				.collect(Collectors.toList());

			StrictValidatedValueLookup validationLookup = StrictValidatedValueLookup.with(sources);
			List<ErrorMessage> errorMessages = validate(validationLookup, validation, calculated);

			Context withValidationSources = withCalculationSources.addIfNotExist(sources.stream()
				.filter(ValidatedValue::isValid)
				.map(Solver::asMappedValue)
				.collect(Collectors.toList()));

			if (errorMessages.isEmpty()) {
				withValidation = withValidationSources.add(destination, calculated);
			} else {
				withValidation = withValidationSources.addInvalid(destination, ValidationError.of(errorMessages,
					sources.stream().filter(it -> !it.isValid()).map(ValidatedValue::source).collect(
						Collectors.toSet())));
			}
		} else {
			withValidation = withCalculationSources.add(destination, calculated);
		}

		return withValidation;
	}

	private static <T> MappedValue<T> asMappedValue(ValidatedValue<T> validated) {
		Preconditions.checkArgument(validated.isValid(),"%s is not valid", validated.source());
		return MappedValue.of(validated.source(), validated.value());
	}

	private static <T> MappedValue<T> mappedValue(Context context, ValueLookup lookup, Value<T> id) {
		return MappedValue.of(id, value(context, lookup, id));
	}

	private static <T> @Nullable T value(Context context, ValueLookup lookup, Value<T> id) {
		return context.isValid(id)
			? context.getValidated(id)
			: context.isInvalid(id)
			? null
			: lookup.get(id);
	}

	private static <T> ValidatedValue<T> validatedValue(Context context, ValueSource<T> id) {
		if (id instanceof Unvalidated) {
			return ValidatedValue.of(id, context.getUnvalidated(id));
		}
		return context.isValid(id)
			? ValidatedValue.of(id, context.getValidated(id))
			: ValidatedValue.of(id, context.validationError(id));
	}

	private static <T> List<ErrorMessage> validate(ValidatedValueLookup valueLookup, Validation<T> validation, T calculated) {
		return validation.validate(Optional.ofNullable(calculated), valueLookup);
	}

	private static <T> Context processUnvalidated(ValueLookup lookup, Unvalidated<T> node, Context context) {
		return context.addUnvalidated(node, lookup.get(node.wrapped()));
	}
}
