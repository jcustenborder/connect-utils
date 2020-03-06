package com.github.jcustenborder.kafka.connect.utils.nodoc;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.storage.Converter;

import java.util.Map;

public class NoDocTestConverter implements Converter {
  @Override
  public void configure(Map<String, ?> map, boolean b) {

  }

  @Override
  public byte[] fromConnectData(String s, Schema schema, Object o) {
    return new byte[0];
  }

  @Override
  public SchemaAndValue toConnectData(String s, byte[] bytes) {
    return null;
  }
}
