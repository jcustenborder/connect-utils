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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicPartitionCounter {
  private final Map<TopicPartition, Long> data = new ConcurrentHashMap<>(100);

  public void increment(TopicPartition topicPartition, long offset) {
    Preconditions.checkNotNull(topicPartition, "topicPartition cannot be null.");
    Preconditions.checkState(offset > 0, "offset must be greater than 0.");
    synchronized (this.data) {
      long current = data.getOrDefault(topicPartition, Long.MIN_VALUE);

      if (offset > current) {
        this.data.put(topicPartition, offset);
      }
    }
  }

  public void increment(String topic, int partition, long offset) {
    Preconditions.checkState(!Strings.isNullOrEmpty(topic), "topic cannot be null or empty.");
    increment(new TopicPartition(topic, partition), offset);
  }

  public Map<TopicPartition, Long> data() {
    return ImmutableMap.copyOf(this.data);
  }
}
