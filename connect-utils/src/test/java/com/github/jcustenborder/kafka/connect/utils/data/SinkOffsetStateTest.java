package com.github.jcustenborder.kafka.connect.utils.data;

import com.github.jcustenborder.kafka.connect.utils.jackson.ObjectMapperFactory;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SinkOffsetStateTest {


  SinkOffsetState roundTrip(SinkOffsetState input) throws IOException {
    final byte[] buffer = ObjectMapperFactory.INSTANCE.writeValueAsBytes(input);
    return ObjectMapperFactory.INSTANCE.readValue(buffer, SinkOffsetState.class);
  }

  @Test
  public void roundTrip() throws IOException {
    final SinkOffsetState expected = SinkOffsetState.of("test", 1, 1234L);
    final SinkOffsetState actual = roundTrip(expected);
    assertNotNull(actual);
    assertEquals(expected.topic(), actual.topic());
    assertEquals(expected.partition(), actual.partition());
    assertEquals(expected.offset(), actual.offset());
    assertEquals(expected.topicPartition(), actual.topicPartition());
    assertEquals(expected, actual);
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void roundTripByTopicPartition() throws IOException {
    final SinkOffsetState expected = SinkOffsetState.of(new TopicPartition("test", 1), 1234L);
    final SinkOffsetState actual = roundTrip(expected);
    assertNotNull(actual);
    assertEquals(expected.topic(), actual.topic());
    assertEquals(expected.partition(), actual.partition());
    assertEquals(expected.offset(), actual.offset());
    assertEquals(expected.topicPartition(), actual.topicPartition());
    assertEquals(expected, actual);
  }

  @Test
  public void display() {
    final String expected = "SinkOffsetState{topic=test, partition=1, offset=1234}";
    final SinkOffsetState input = SinkOffsetState.of("test", 1, 1234L);
    assertEquals(expected, input.toString());
  }

  @Test
  public void notEquals() {
    final SinkOffsetState input = SinkOffsetState.of("test", 1, 1234L);
    assertFalse(input.equals("something"));
  }


}
