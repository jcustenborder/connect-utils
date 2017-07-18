package com.github.jcustenborder.kafka.connect.utils.config.validators.filesystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileSystemTests {
  Set<Path> cleanup;

  @BeforeEach
  public void beforeEach() {
    this.cleanup = new HashSet<>();
  }

  protected Path createTempFile() throws IOException {
    Path result = Files.createTempFile("test", "test");
    this.cleanup.add(result);
    return result;
  }

  protected Path createTempDirectory() throws IOException {
    Path result = Files.createTempDirectory("test");
    this.cleanup.add(result);
    return result;
  }

  @AfterEach
  public void afterEach() throws IOException {
    for (Path p : this.cleanup) {
      Files.deleteIfExists(p);
    }
  }
}
