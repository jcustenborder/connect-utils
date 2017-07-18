package com.github.jcustenborder.kafka.connect.utils.config.validators.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidDirectoryTests extends FileSystemTests {
  ValidDirectory validator;

  @BeforeEach
  public void createValidator() {
    this.validator = ValidDirectory.of();
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
    this.validator.ensureValid("test", input.toString());
  }

  @Test
  public void fileIsFile() throws IOException {
    final Path input = createTempFile();
    assertThrows(IllegalStateException.class, () -> {
      this.validator.ensureValid("test", input.toString());
    });
  }
}
