package io.confluent.kafka.connect.conversion.type;

public class Int16TypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Short.parseShort(s);
  }
}
