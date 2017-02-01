package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.common.utils.Time;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeSerializationModuleTest {
  private static final Logger log = LoggerFactory.getLogger(TimeSerializationModuleTest.class);

  @Test
  public void roundtrip() throws IOException {
    final Time expected = mock(Time.class);
    when(expected.milliseconds()).thenReturn(1485910473123L);
    when(expected.nanoseconds()).thenReturn(1485910473123123L);
    final String temp = ObjectMapperFactory.INSTANCE.writeValueAsString(expected);
    log.trace(temp);
    final Time actual = ObjectMapperFactory.INSTANCE.readValue(temp, Time.class);
    assertNotNull(actual);
    assertEquals(expected.milliseconds(), actual.milliseconds(), "milliseconds() does not match.");
    assertEquals(expected.nanoseconds(), actual.nanoseconds(), "nanoseconds() does not match.");
  }

}
