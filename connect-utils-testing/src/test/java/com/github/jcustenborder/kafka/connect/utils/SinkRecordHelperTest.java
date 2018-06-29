package com.github.jcustenborder.kafka.connect.utils;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static com.github.jcustenborder.kafka.connect.utils.SinkRecordHelper.delete;
import static com.github.jcustenborder.kafka.connect.utils.SinkRecordHelper.write;
import static com.github.jcustenborder.kafka.connect.utils.StructHelper.struct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SinkRecordHelperTest {

  @Test
  public void deleteStruct() {
    Struct key = struct("test", "id", Schema.Type.INT64, false, 1234L);
    SinkRecord actual = delete("topic", key);
    assertNotNull(actual, "actual should not be null.");
    assertEquals("topic", actual.topic(), "actual.topic does not match.");
    assertSchema(key.schema(), actual.keySchema(), "keySchema() does not match.");
    assertNull(actual.valueSchema(), "valueSchema() should be null.");
    assertNull(actual.value(), "valueSchema() should be null.");
  }

  @Test
  public void deleteNullKey() {
    assertThrows(DataException.class, () -> {
      SinkRecord actual = delete("topic", Schema.OPTIONAL_STRING_SCHEMA, null);
    });
  }

  @Test
  public void writeStruct() {
    Struct key = struct("key", "id", Schema.Type.INT64, false, 1234L);
    Struct value = struct("value", "id", Schema.Type.INT64, false, 1234L);
    SinkRecord actual = write("topic", key, value);

    assertNotNull(actual, "actual should not be null.");
    assertEquals("topic", actual.topic(), "actual.topic does not match.");
    assertSchema(key.schema(), actual.keySchema(), "keySchema() does not match.");
    assertSchema(value.schema(), actual.valueSchema(), "keySchema() does not match.");
  }

}
