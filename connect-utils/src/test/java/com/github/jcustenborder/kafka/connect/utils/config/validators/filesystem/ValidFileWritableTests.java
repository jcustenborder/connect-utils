package com.github.jcustenborder.kafka.connect.utils.config.validators.filesystem;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidFileWritableTests extends FileSystemTests<ValidFileWritable> {
  @Override
  public ValidFileWritable createValidator() {
    return ValidFileWritable.of();
  }

  @Test
  public void hasReadWrite() throws IOException {
    final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(EnumSet.of(
        PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE
    ));
    final Path path = createTempFile(attr);
    this.validator.ensureValid("testing", path.toString());
  }

  @Test
  public void notWritable() throws IOException {
    final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(EnumSet.of(
        PosixFilePermission.OWNER_READ
    ));
    final Path path = createTempFile(attr);

    assertThrows(IllegalStateException.class, () -> {
      this.validator.ensureValid("testing", path.toString());
    });
  }
}
