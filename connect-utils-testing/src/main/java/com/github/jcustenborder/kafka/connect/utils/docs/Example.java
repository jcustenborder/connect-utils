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
package com.github.jcustenborder.kafka.connect.utils.docs;

import java.util.Map;

public class Example {
  String name;
  String description;
  Map<String, String> properties;

  public String name() {
    return this.name;
  }

  public void name(String name) {
    this.name = name;
  }

  public String description() {
    return this.description;
  }

  public void description(String description) {
    this.description = description;
  }

  public Map<String, String> properties() {
    return this.properties;
  }

  public void properties(Map<String, String> properties) {
    this.properties = properties;
  }
}
