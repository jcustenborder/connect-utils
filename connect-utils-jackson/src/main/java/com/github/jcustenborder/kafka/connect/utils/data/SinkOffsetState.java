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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import org.apache.kafka.common.TopicPartition;

/**
 * This class is a helper class for storing offsets for a topic partition in an external store.
 * Many connectors implement exactly once features against by storing the last offset for
 * each of the topic partitions they have processed.
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE)
public class SinkOffsetState implements Comparable<SinkOffsetState> {
  @JsonProperty("topic")
  private String topic;
  @JsonProperty("partition")
  private Integer partition;
  @JsonProperty("offset")
  private Long offset;

  @JsonCreator
  private SinkOffsetState() {

  }

  public String topic() {
    return this.topic;
  }

  public Integer partition() {
    return this.partition;
  }

  public Long offset() {
    return this.offset;
  }

  private SinkOffsetState(String topic, Integer partition, Long offset) {
    Preconditions.checkState(!Strings.isNullOrEmpty(topic), "topic cannot be null or empty.");
    Preconditions.checkNotNull(partition, "partition cannot be null.");
    Preconditions.checkNotNull(offset, "offset cannot be null.");
    this.topic = topic;
    this.partition = partition;
    this.offset = offset;
  }

  public TopicPartition topicPartition() {
    return new TopicPartition(this.topic, this.partition);
  }

  public static SinkOffsetState of(TopicPartition topicPartition, long offset) {
    Preconditions.checkNotNull(topicPartition, "topicPartition cannot be null.");
    return of(topicPartition.topic(), topicPartition.partition(), offset);
  }

  public static SinkOffsetState of(String topic, int partition, long offset) {
    return new SinkOffsetState(topic, partition, offset);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("topic", this.topic)
        .add("partition", this.partition)
        .add("offset", this.offset)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        this.topic,
        this.partition,
        this.offset
    );
  }

  @Override
  public boolean equals(Object obj) {
    if ((obj instanceof SinkOffsetState)) {
      return 0 == compareTo((SinkOffsetState) obj);
    } else {
      return false;
    }
  }

  @Override
  public int compareTo(SinkOffsetState that) {
    return ComparisonChain.start()
        .compare(this.topic, that.topic)
        .compare(this.partition, that.partition)
        .compare(this.offset, that.offset)
        .result();
  }
}
