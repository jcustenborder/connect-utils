package io.confluent.kafka.connect.conversion.type;

public class BooleanConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Boolean.parseBoolean(s);
  }
}
