package com.github.jcustenborder.kafka.connect.utils.config.validators.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ValidFileTests extends FileSystemTests {
  ValidFile validator;

  @BeforeEach
  public void createValidator() {
    this.validator = ValidFile.of();
  }


  @Test
  public void ensureValid_nullSetting() {
    assertThrows(IllegalStateException.class, () -> {
      this.validator.ensureValid("test", null);
    });
  }

  @Test
  public void ensureValid_blankSetting() {
    assertThrows(IllegalStateException.class, () -> {
      this.validator.ensureValid("test", "");
    });
  }


  @Test
  public void fileIsDirectory() throws IOException {
    final Path input = createTempDirectory();
    assertThrows(IllegalStateException.class, () -> {
      this.validator.ensureValid("test", input.toString());
    });
  }

  @Test
  public void fileIsFile() throws IOException {
    final Path input = createTempFile();
    this.validator.ensureValid("test", input.toString());
  }


}
