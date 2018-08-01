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

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidDirectoryWritableTest extends FileSystemTest<ValidDirectoryWritable> {
  @Override
  public ValidDirectoryWritable createValidator() {
    return ValidDirectoryWritable.of();
  }

  @Test
  public void hasReadWrite() throws IOException {
    final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(EnumSet.of(
        PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE
    ));
    final Path path = createTempDirectory(attr);
    this.validator.ensureValid("testing", path.toString());
  }

  @Test
  public void notWritable() throws IOException {
    final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(EnumSet.of(
        PosixFilePermission.OWNER_READ
    ));
    final Path path = createTempDirectory(attr);

    assertThrows(ConfigException.class, () -> {
      this.validator.ensureValid("testing", path.toString());
    });
  }
}