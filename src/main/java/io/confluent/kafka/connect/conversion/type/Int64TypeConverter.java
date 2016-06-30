package io.confluent.kafka.connect.conversion.type;

public class Int64TypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Long.parseLong(s);
  }
}
