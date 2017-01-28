package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.MoreObjects;
import org.apache.kafka.connect.data.Schema;

public class SchemaUtils {
  public static String toString(Schema schema) {
    return MoreObjects.toStringHelper(schema)
        .add("type", schema.type())
        .add("name", schema.name())
        .add("isOptional", schema.isOptional())
        .add("version", schema.version())
        .omitNullValues()
        .toString();
  }
}
