/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.config.validators.filesystem;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class FileSystemTests<T extends ValidFileSystem> {
  private static final Logger log = LoggerFactory.getLogger(FileSystemTests.class);
  protected T validator;
  Set<Path> cleanup;

  public abstract T createValidator();

  @BeforeEach
  public void beforeEach() {
    this.cleanup = new HashSet<>();
    this.validator = createValidator();
  }

  protected Path createTempFile(FileAttribute<?>... attrs) throws IOException {
    Path result = Files.createTempFile("test", "test", attrs);
    this.cleanup.add(result);
    return result;
  }

  protected Path createTempDirectory(FileAttribute<?>... attrs) throws IOException {
    Path result = Files.createTempDirectory("test", attrs);
    this.cleanup.add(result);
    return result;
  }

  @Test
  public void ensureValid_nullSetting() {
    assertThrows(ConfigException.class, () -> {
      this.validator.ensureValid("test", null);
    });
  }

  @Test
  public void ensureValid_blankSetting() {
    assertThrows(ConfigException.class, () -> {
      this.validator.ensureValid("test", "");
    });
  }

  @Test
  public void ensureValid_notString() {
    assertThrows(ConfigException.class, () -> {
      this.validator.ensureValid("test", 1);
    });
  }

  @AfterEach
  public void afterEach() throws IOException {
    for (Path p : this.cleanup) {
      Files.deleteIfExists(p);
    }
  }
}
