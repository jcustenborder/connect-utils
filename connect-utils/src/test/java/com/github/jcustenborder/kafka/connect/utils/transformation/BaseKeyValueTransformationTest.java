package com.github.jcustenborder.kafka.connect.utils.transformation;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class BaseKeyValueTransformationTest {
  static class Base<R extends ConnectRecord<R>> extends BaseKeyValueTransformation<R> {

    protected Base(boolean isKey) {
      super(isKey);
    }

    @Override
    public ConfigDef config() {
      return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
  }
  static class StringTransformation<R extends ConnectRecord<R>> extends Base<R> {
    public StringTransformation() {
      super(true);
    }

    @Override
    protected SchemaAndValue processString(R record, Schema inputSchema, String input) {
      return new SchemaAndValue(inputSchema, input);
    }
  }

  @Test
  public void test() {
    StringTransformation transformation = new StringTransformation();
    SinkRecord record = new SinkRecord(
        "testing",
        1,
        Schema.STRING_SCHEMA,
        "foo",
        null,
        null,
        123451L
    );
    transformation.apply(record);
  }

}
