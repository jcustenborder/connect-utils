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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Validator is used to ensure that the setting is a file on the file system, is readable, and writable.
 */
public class ValidFileWritable extends ValidFile {
  private static final Logger log = LoggerFactory.getLogger(ValidFileWritable.class);

  private ValidFileWritable() {
    super(true);
  }

  @Override
  protected void ensureValid(String setting, Object input, File directoryPath) {
    super.ensureValid(setting, input, directoryPath);
  }

  public static ValidFileWritable of() {
    return new ValidFileWritable();
  }
}
