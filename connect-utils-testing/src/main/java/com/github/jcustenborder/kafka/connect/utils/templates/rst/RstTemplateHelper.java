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
package com.github.jcustenborder.kafka.connect.utils.templates.rst;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jcustenborder.kafka.connect.utils.templates.ConnectorTemplate;
import com.github.jcustenborder.kafka.connect.utils.templates.IntentedWriter;
import com.github.jcustenborder.kafka.connect.utils.templates.Table;
import com.github.jcustenborder.kafka.connect.utils.templates.TemplateHelper;
import com.google.common.base.Joiner;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class RstTemplateHelper extends TemplateHelper {

  List<Integer> emphasizeLines(String content) {
    List<Integer> result = new ArrayList<>();
    try (StringReader stringReader = new StringReader(content)) {
      try (LineNumberReader reader = new LineNumberReader(stringReader)) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.contains(REQUIRED_CONFIG)) {
            result.add(reader.getLineNumber());
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return result;
  }

  public String jsonExample(ConnectorTemplate template) {
    StringWriter writer = new StringWriter();

    final ObjectNode outputNode = createJsonNode(template);
    final String json;
    try {
      json = this.objectMapper.writeValueAsString(outputNode);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }

    writer.write(".. code-block:: json");
    writer.write('\n');
    writer.write("    :name: connector.json");
    writer.write('\n');
    List<Integer> emphasizeLines = emphasizeLines(json);
    if (!emphasizeLines.isEmpty()) {
      writer.write("    :emphasize-lines: ");
      writer.write(Joiner.on(',').join(emphasizeLines));
      writer.write('\n');
    }
    writer.write('\n');
    writer.write(indent(json));
    writer.write('\n');
    return writer.toString();
  }

  String indent(String result) {
    Writer writer = new StringWriter();

    try (StringReader stringReader = new StringReader(result)) {
      try (BufferedReader reader = new BufferedReader(stringReader)) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("#")) {
            continue;
          }
          writer.write("    ");
          writer.write(line);
          writer.write('\n');
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return writer.toString();
  }

  public String propertiesExample(ConnectorTemplate template) {
    StringWriter writer = new StringWriter();
    Properties properties = createProperties(template);

    try {
      properties.store(writer, "");
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    String result = writer.toString();
    result = indent(result);
    writer = new StringWriter();
    writer.write(".. code-block:: properties");
    writer.write('\n');
    writer.write("    :name: connector.properties");
    writer.write('\n');
    List<Integer> emphasizeLines = emphasizeLines(result);
    if (!emphasizeLines.isEmpty()) {
      writer.write("    :emphasize-lines: ");
      writer.write(Joiner.on(',').join(emphasizeLines));
      writer.write('\n');
    }
    writer.write('\n');
    writer.write(result);
    return writer.toString();
  }

  public String table(Table table) {
    try (StringWriter writer = new StringWriter()) {
      try (IntentedWriter printWriter = new IntentedWriter(writer)) {
        printWriter.write(String.format(".. csv-table:: %s\n", table.getTitle()));
        printWriter.increase();
        printWriter.write(String.format(":header: \"%s\"\n", Joiner.on("\", \"").join(table.getHeaders())));
        printWriter.write(":widths: auto\n");
        printWriter.println();

        try (CSVWriter csvWriter = new CSVWriter(printWriter)) {
          final List<String[]> rows = table.getRowData().stream()
              .map(strings -> strings.toArray(new String[strings.size()]))
              .collect(Collectors.toList());
          csvWriter.writeAll(rows);
        }
      }
      return writer.toString();
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
