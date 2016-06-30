package io.confluent.kafka.connect.conversion.type;

public class Float64TypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return Double.parseDouble(s);
  }
}
