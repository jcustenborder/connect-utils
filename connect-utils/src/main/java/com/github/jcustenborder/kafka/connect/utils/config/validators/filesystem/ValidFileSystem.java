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

import com.google.common.base.Strings;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Validator is used as a base for validators that check file system properties.
 */
public abstract class ValidFileSystem implements ConfigDef.Validator {
  private static final Logger log = LoggerFactory.getLogger(ValidFileSystem.class);

  public final boolean ensureWritable;

  protected ValidFileSystem(boolean ensureWritable) {
    this.ensureWritable = ensureWritable;
  }

  protected abstract void ensureValid(String setting, Object input, File file);

  @Override
  public void ensureValid(String setting, Object input) {
    log.trace("ensureValid('{}', '{}')", setting, input);
    if (!(input instanceof String)) {
      throw new ConfigException(setting, "Input must be a string.");
    }
    final String value = input.toString();
    if (Strings.isNullOrEmpty(value)) {
      throw new ConfigException(setting, "Cannot be null or empty.");
    }
    final File file = new File(value);
    if (!file.isAbsolute()) {
      throw new ConfigException(
          setting,
          String.format("File '%s' is not an absolute path.", file)
      );
    }
    ensureValid(setting, input, file);

    if (this.ensureWritable) {
      if (!file.canWrite()) {
        throw new ConfigException(
            setting,
            String.format("File '%s' should be writable.", file)
        );
      }
    }

    if (!file.canRead()) {
      throw new ConfigException(
          setting,
          String.format("File '%s' should be readable.", file)
      );
    }
  }
}