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

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SourceRecordConcurrentLinkedDequeTest {
  SourceRecordConcurrentLinkedDeque sourceRecords;

  @BeforeEach
  public void before() {
    this.sourceRecords = new SourceRecordConcurrentLinkedDeque();
  }

  @Test
  public void drain() throws InterruptedException {
    List<SourceRecord> records = new ArrayList<>(256);
    assertFalse(this.sourceRecords.drain(records), "drain should return false");
    assertTrue(records.isEmpty(), "records should be empty");

    final int EXPECTED_COUNT = 5;
    for (int i = 0; i < EXPECTED_COUNT; i++) {
      SourceRecord record = new SourceRecord(null, null, null, null, null);
      this.sourceRecords.add(record);
    }

    assertEquals(EXPECTED_COUNT, this.sourceRecords.size(), "sourceRecords.size() should match.");
    assertTrue(this.sourceRecords.drain(records), "drain should return true");
    assertTrue(this.sourceRecords.isEmpty(), "drain should have emptied the deque.");
    assertEquals(EXPECTED_COUNT, records.size(), "records.size()");
  }

}
