package com.github.jcustenborder.kafka.connect.utils.config.validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidCharsetTest {
  @Test
  public void utf8() {
    ConfigDef.Validator validator = new ValidCharset();
    validator.ensureValid("testing", "utf8");
  }

  @Test
  public void unsupportedCharset() {
    ConfigException configException = assertThrows(ConfigException.class, () -> {
      ConfigDef.Validator validator = new ValidCharset();
      validator.ensureValid("testing", "utf9");
    });
  }

  @Test
  public void invalid() {
    ConfigException configException = assertThrows(ConfigException.class, () -> {
      ConfigDef.Validator validator = new ValidCharset();
      validator.ensureValid("testing", 1234);
    });
  }

  @Test
  public void list() {
    ConfigDef.Validator validator = new ValidCharset();
    validator.ensureValid("testing", Arrays.asList("UTF-8", "UTF-16"));
    System.out.println(validator);
  }

  @Test
  public void display() {
    ConfigDef.Validator validator = new ValidCharset("UTF-8");
    final String expected = "Valid values: 'UTF-8'";
    final String actual = validator.toString();
    System.out.println(actual);
    assertEquals(expected, actual);
  }
}
