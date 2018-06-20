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

import java.io.File;

/**
 * Validator is used to ensure that the setting is a file on the file system and that it is readable.
 */
public class ValidFile extends ValidFileSystem {

  private ValidFile() {
    this(false);
  }

  protected ValidFile(boolean ensureWritable) {
    super(ensureWritable);
  }

  @Override
  protected void ensureValid(String setting, Object input, File file) {
    if (!file.isFile()) {
      throw new ConfigException(
          setting,
          String.format("'%s' must be a file.", file)
      );
    }
  }

  public static ValidFile of() {
    return new ValidFile();
  }

  @Override
  public String toString() {
    return "Absolute path to a file that exists.";
  }
}
