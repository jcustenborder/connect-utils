package io.confluent.kafka.connect.conversion.type;

public class Int8TypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Byte.parseByte(s);
  }
}
