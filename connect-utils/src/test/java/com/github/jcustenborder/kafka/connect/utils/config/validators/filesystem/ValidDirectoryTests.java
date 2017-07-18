/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.config.validators.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidDirectoryTests extends FileSystemTests<ValidDirectory> {

  @BeforeEach
  public ValidDirectory createValidator() {
    return ValidDirectory.of();
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
