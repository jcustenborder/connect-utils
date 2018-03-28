/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SourceRecordDequeTest {
  Time time;
  SourceRecordDequeBuilder builder;

  @BeforeEach
  public void before() {
    this.time = mock(Time.class);
    this.builder = SourceRecordDequeBuilder.of();
    this.builder.time = this.time;
  }

  SourceRecord newRecord() {
    return new SourceRecord(
        ImmutableMap.of(),
        ImmutableMap.of(),
        "foo",
        null,
        null,
        null
    );
  }


  @Test
  public void standard() {
    SourceRecordDeque deque = this.builder.build();
    assertNotNull(deque);
    final int count = 10;
    for (int i = 0; i < count; i++) {
      deque.add(newRecord());
    }
    assertEquals(count, deque.size());
    List<SourceRecord> records = new ArrayList<>(count);
    assertEquals(0, records.size());
    assertEquals(count, deque.size());
    deque.drain(records);
    assertEquals(count, records.size());
    assertEquals(0, deque.size());
  }

  @Test
  public void timeout() {
    when(this.time.milliseconds()).thenReturn(1000L, 3000L, 5000L, 100000L);
    SourceRecordDeque deque = this.builder
        .maximumCapacity(5)
        .build();
    final int count = 10;
    assertNotNull(deque);
    assertThrows(TimeoutException.class, () -> {
      for (int i = 0; i < count; i++) {
        deque.add(newRecord());
      }
    });
  }
}
