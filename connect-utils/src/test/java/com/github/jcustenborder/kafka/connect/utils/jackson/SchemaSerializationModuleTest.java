package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;


public class SchemaSerializationModuleTest {
  ObjectMapper objectMapper;

  @BeforeEach
  public void before() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new SchemaSerializationModule());
  }


}
