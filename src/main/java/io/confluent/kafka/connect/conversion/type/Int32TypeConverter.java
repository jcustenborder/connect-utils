package io.confluent.kafka.connect.conversion.type;

public class Int32TypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Integer.parseInt(s);
  }
}
