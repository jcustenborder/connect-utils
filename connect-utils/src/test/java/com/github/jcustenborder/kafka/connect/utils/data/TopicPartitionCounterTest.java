/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TopicPartitionCounterTest {
  TopicPartitionCounter counter;

  @BeforeEach
  public void before() {
    this.counter = new TopicPartitionCounter();
  }

  @Test
  public void increment() {
    final TopicPartition topicPartition = new TopicPartition("test", 1);
    final Map<TopicPartition, Long> expected = ImmutableMap.of(
        topicPartition, 1234L
    );
    this.counter.increment(topicPartition, 1234L);

    for (long i = 1; i <= 123L; i++) {
      this.counter.increment(topicPartition, i);
    }

    assertEquals(expected, this.counter.data());
  }


  @Test
  public void incrementByTopicPartition() {
    final TopicPartition topicPartition = new TopicPartition("test", 1);
    final Map<TopicPartition, Long> expected = ImmutableMap.of(
        topicPartition, 123L
    );

    for (long i = 1; i <= 123L; i++) {
      this.counter.increment(topicPartition, i);
    }

    assertEquals(expected, this.counter.data());
  }

  @Test
  public void incrementByTopicNamePartition() {
    final TopicPartition topicPartition = new TopicPartition("test", 1);
    final Map<TopicPartition, Long> expected = ImmutableMap.of(
        topicPartition, 123L
    );

    for (long i = 1; i <= 123L; i++) {
      this.counter.increment(topicPartition.topic(), topicPartition.partition(), i);
    }

    assertEquals(expected, this.counter.data());
  }

  @Test
  public void nullTopic() {
    assertThrows(IllegalStateException.class, () -> {
      this.counter.increment(null, 1, 123L);
    });
  }

  @Test
  public void emptyTopic() {
    assertThrows(IllegalStateException.class, () -> {
      this.counter.increment("", 1, 123L);
    });
  }

  @Test
  public void nullTopicPartition() {
    assertThrows(NullPointerException.class, () -> {
      this.counter.increment(null, 123L);
    });
  }
}
