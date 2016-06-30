package io.confluent.kafka.connect.conversion.type;

public class Float32TypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Float.parseFloat(s);
  }
}
