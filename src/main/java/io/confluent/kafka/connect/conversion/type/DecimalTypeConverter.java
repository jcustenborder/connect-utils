package io.confluent.kafka.connect.conversion.type;

import java.math.BigDecimal;

public class DecimalTypeConverter implements TypeConverter {
  @Override
  public Object convert(String s) {
    return new BigDecimal(s);
  }
}
