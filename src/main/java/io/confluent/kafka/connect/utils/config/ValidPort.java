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
package io.confluent.kafka.connect.utils.config;

import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;


/**
 * Validator is used to validate that the config setting is in the proper range for a TCP or UDP port.
 */
public class ValidPort implements ConfigDef.Validator {
  final int start;
  final int end;

  ValidPort(int start, int end) {
    Preconditions.checkState(start > 0, "start must be greater than 0.");
    Preconditions.checkState(end > 0, "end must be greater than 0.");
    Preconditions.checkState(start <= 65535, "start must be less than or equal to 65535.");
    Preconditions.checkState(end <= 65535, "end must be less than or equal to 65535.");
    Preconditions.checkState(end > start, "end must be less than or equal to 65535.");

    this.start = start;
    this.end = end;
  }

  /**
   * Creates a default instance of the validator in the non privileged port range of 1025 through 65535.
   *
   * @return ConfigDef.Validator checking for ports in the range of 1025 through 65535.
   */
  public static ValidPort of() {
    return of(1025, 65535);
  }

  /**
   * Creates a validator in the port range specified.
   *
   * @param start The low port of the range.
   * @param end   The high port of the range.
   * @return ConfigDef.Validator Validator for the port range specified.
   * @throws IllegalStateException Throws if the start not a valid port number.
   * @throws IllegalStateException Throws if the end not a valid port number.
   * @throws IllegalStateException Throws if the start is greater than end.
   */
  public static ValidPort of(int start, int end) {
    return new ValidPort(start, end);
  }

  /**
   * Method is used to validate that the supplied port is within the valid range.
   *
   * @param setting name of the setting being tested.
   * @param value   value being tested.
   */
  @Override
  public void ensureValid(String setting, Object value) {
    Preconditions.checkState(value instanceof Integer, "%s must be an integer.", setting);
    Integer port = (Integer) value;
    Preconditions.checkState(port >= this.start && port <= this.end, "'%s'(%s) must be between %s and %s.", setting, port, this.start, this.end);
  }
}
