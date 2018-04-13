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
package com.github.jcustenborder.kafka.connect.utils.templates.markdown;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jcustenborder.kafka.connect.utils.templates.model.Configurable;
import com.github.jcustenborder.kafka.connect.utils.templates.TemplateHelper;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

public class MarkdownTemplateHelper extends TemplateHelper {


  static void lengths(List<Integer> lengths, List<List<String>> rows) {
    if (lengths.isEmpty()) {
      for (int i = 0; i < rows.get(0).size(); i++) {
        lengths.add(0);
      }
    }
    for (List<String> row : rows) {
      for (int i = 0; i < row.size(); i++) {
        int previous = lengths.get(i);
        int current;
        if (Strings.isNullOrEmpty(row.get(i))) {
          current = 0;
        } else {
          current = row.get(i).length();
        }
        int value = Math.max(current, previous);
        lengths.set(i, value);
      }
    }

  }


  public String jsonExample(Configurable template) {
    ObjectNode outputNode = createJsonNode(template);

    StringWriter writer = new StringWriter();
    writer.write("```json");
    writer.write('\n');
    try {
      this.objectMapper.writeValue(writer, outputNode);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    writer.write('\n');
    writer.write("```");

    return writer.toString();
  }


  public String propertiesExample(Configurable template) {
    StringWriter writer = new StringWriter();
    writer.write("```properties");
    writer.write('\n');

    Properties properties = createProperties(template);

    try {
      properties.store(writer, "");
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    writer.write("```");

    String result = writer.toString();
    result = result.replaceAll("#.*\\n", "");
    return result.trim();

  }
}
