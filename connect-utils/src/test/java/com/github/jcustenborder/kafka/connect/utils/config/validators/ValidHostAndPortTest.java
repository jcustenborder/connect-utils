package com.github.jcustenborder.kafka.connect.utils.config.validators;

import com.google.common.base.Strings;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ValidHostAndPortTest {

  @TestFactory
  public Stream<DynamicTest> invalid() {
    return Arrays.asList(
        (Object) Byte.MAX_VALUE,
        (Object) true,
        (Object) Integer.MAX_VALUE,
        (Object) Short.MAX_VALUE
    ).stream().map(input -> dynamicTest(input.getClass().getSimpleName(), () -> {
      assertThrows(ConfigException.class, () -> {
        ValidHostAndPort.of().ensureValid("foo", input);
      });
    }));
  }

  @TestFactory
  public Stream<DynamicTest> invalidString() {
    return Arrays.asList(
        null,
        "127.0.0.1",
        "::1:9021",
        ":9021"
    ).stream().map(input -> dynamicTest(Strings.isNullOrEmpty(input) ? "null" : input, () -> {
      assertThrows(ConfigException.class, () -> {
        ValidHostAndPort.of().ensureValid("foo", input);
      });
    }));
  }

  @TestFactory
  public Stream<DynamicTest> invalidList() {
    return Arrays.asList(
        Arrays.asList("127.0.0.1", "::1:9021", ":9021")
    ).stream().map(input -> dynamicTest(String.format("%s", input), () -> {
      assertThrows(ConfigException.class, () -> {
        ValidHostAndPort.of().ensureValid("foo", input);
      });
    }));
  }

  @TestFactory
  public Stream<DynamicTest> validString() {
    return Arrays.asList(
        "localhost:9021",
        "127.0.0.1:9021",
        "[::1]:9021"
    ).stream().map(input -> dynamicTest(input, () -> {
      ValidHostAndPort.of().ensureValid("foo", input);
    }));
  }

  @TestFactory
  public Stream<DynamicTest> validList() {
    return Arrays.asList(
        Arrays.asList("localhost:9021", "127.0.0.1:9021", "[::1]:9021")
    ).stream().map(input -> dynamicTest(String.format("%s", input), () -> {
      ValidHostAndPort.of().ensureValid("foo", input);
    }));
  }
}
