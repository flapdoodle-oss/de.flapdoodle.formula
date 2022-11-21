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

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class IdTest {

	@Test
	void idAsString() {
		try(Id.ClearTypeCounter __ = Id.with(new TypeCounter())) {
			Id<String> stringId = Id.idFor(String.class);

			assertThat(stringId.toString()).isEqualTo("ImmutableId{type=String, count=0}");
			assertThat(stringId.asHumanReadable()).isEqualTo("String#0");
		}
	}

	@Test
	void idForTypeMustUseTypeCounter() {
		TypeCounter localTypeCounter = new TypeCounter();

		try(Id.ClearTypeCounter __ = Id.with(localTypeCounter)) {
			Id<String> stringId = Id.idFor(String.class);
			assertThat(stringId.type()).isEqualTo(String.class);
			assertThat(stringId.count()).isEqualTo(0);
			assertThat(stringId.asInstance("foo")).contains("foo");
			assertThat(stringId.asInstance(1)).isEmpty();

			Id<Integer> intId = Id.idFor(Integer.class);
			assertThat(intId.type()).isEqualTo(Integer.class);
			assertThat(intId.count()).isEqualTo(0);
			assertThat(intId.asInstance("foo")).isEmpty();
			assertThat(intId.asInstance(1)).contains(1);
		}

		assertThat(localTypeCounter.count(String.class)).isEqualTo(1);
		assertThat(localTypeCounter.count(Integer.class)).isEqualTo(1);
	}

	@Test
	void notSameIdConcurrentAccess() throws InterruptedException {
		int numberOfThreads=10;
		List<Id<String>> collectedIds= new CopyOnWriteArrayList<>();
		List<Thread> threads=new ArrayList<>();

		for (int i=0;i<numberOfThreads;i++) {
			Thread thread = new Thread(() -> collectedIds.add(Id.idFor(String.class)));
			thread.start();
			threads.add(thread);
		}

		for (Thread thread : threads) {
			thread.join();
		}

		HashSet<Id<String>> idsAsSet = Sets.newHashSet(collectedIds);
		assertThat(idsAsSet).hasSize(numberOfThreads);
	}
}