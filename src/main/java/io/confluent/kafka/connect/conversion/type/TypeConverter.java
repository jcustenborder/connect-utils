package io.confluent.kafka.connect.conversion.type;

public interface TypeConverter {
  /**
   * Method is used to convert a String to an object representation of a Kafka Connect Type
   * @param s input string to convert
   * @return Object representation of the Kafka Connect Type
   */
  Object convert(String s);
}
