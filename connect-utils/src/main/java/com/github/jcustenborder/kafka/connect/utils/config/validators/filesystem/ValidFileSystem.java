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
import com.google.common.base.Strings;
import org.apache.kafka.common.config.ConfigDef;

import java.io.File;

/**
 * Validator is used as a base for validators that check file system properties.
 */
public abstract class ValidFileSystem implements ConfigDef.Validator {

  protected ValidFileSystem() {

  }

  protected abstract void ensureValid(String setting, Object input, File file);

  @Override
  public void ensureValid(String setting, Object input) {
    Preconditions.checkState(input instanceof String, "'%s' must be a string", setting);
    final String value = input.toString();
    Preconditions.checkState(!Strings.isNullOrEmpty(value), "'%s' cannot be null or empty.", setting);
    final File file = new File(value);
    Preconditions.checkState(file.isAbsolute(), "'%s'(%s) is not an absolute path.", setting, file);
    ensureValid(setting, input, file);
  }
}