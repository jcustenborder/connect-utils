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

import com.google.common.base.Preconditions;

import java.io.File;

/**
 * Validator is used to ensure that the setting is a directory on the file system and that it is readable.
 */
public class ValidDirectory extends ValidFileSystem {

  private ValidDirectory() {
    this(false);
  }

  protected ValidDirectory(boolean ensureWritable) {
    super(ensureWritable);
  }


  @Override
  protected void ensureValid(String setting, Object input, File file) {
    Preconditions.checkState(file.isDirectory(), "'%s'(%s) must be a file.", setting, file);
  }

  public static ValidDirectory of() {
    return new ValidDirectory();
  }
}