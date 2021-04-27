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
package com.github.jcustenborder.kafka.connect.utils.templates;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

public interface Notes {
  @JsonProperty("warning")
  @Nullable
  String getWarning();

  @JsonProperty("tip")
  @Nullable
  String getTip();

  @JsonProperty("important")
  @Nullable
  String getImportant();

  @JsonProperty("danger")
  @Nullable
  String getDanger();

  @JsonProperty("note")
  @Nullable
  String getNote();

  @JsonProperty(value = "title")
  @Nullable
  String getTitle();

  @JsonProperty("description")
  @Nullable
  String getDescription();

  @JsonProperty("icon")
  @Nullable
  String getIcon();

  @JsonProperty(value = "introduction")
  @Nullable
  String getIntroduction();

  @Nullable
  List<Section> getSections();

  @Value.Immutable
  interface Section {
    String getTitle();
    String getText();
  }
}
