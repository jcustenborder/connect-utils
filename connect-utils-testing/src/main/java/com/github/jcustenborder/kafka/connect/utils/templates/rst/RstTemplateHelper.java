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

import com.github.jcustenborder.kafka.connect.utils.templates.IntentedWriter;
import com.github.jcustenborder.kafka.connect.utils.templates.Table;
import com.google.common.base.Joiner;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

public class RstTemplateHelper {
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
