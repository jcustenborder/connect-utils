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
package com.github.jcustenborder.kafka.connect.utils.templates.markdown;

import com.github.jcustenborder.kafka.connect.utils.templates.Table;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkdownTemplateHelper {
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


  public String table(Table table) {
    StringBuilder builder = new StringBuilder();
    List<Integer> lengths = new ArrayList<>();
    lengths(lengths, Arrays.asList(table.getHeaders()));
    lengths(lengths, table.getRowData());

    int index = 0;
    List<String> rowData = new ArrayList<>(lengths.size());
    for (String columnHeader : table.getHeaders()) {
      final int length = lengths.get(index);
      String text = Strings.padEnd(columnHeader, length, ' ');
      rowData.add(text);
      index++;
    }
    builder.append("| ");
    builder.append(Joiner.on(" | ").join(rowData));
    builder.append("|\n");

    rowData.clear();
    for (Integer length : lengths) {
      rowData.add(Strings.repeat("-", length));
    }
    builder.append("| ");
    builder.append(Joiner.on(" | ").join(rowData));
    builder.append("|\n");

    for (List<String> row : table.getRowData()) {
      index = 0;
      rowData.clear();

      for (String columnData : row) {
        final int length = lengths.get(index);
        String text = Strings.padEnd(columnData, length, ' ');
        rowData.add(text);
        index++;
      }
      builder.append("| ");
      builder.append(Joiner.on(" | ").join(rowData));
      builder.append("|\n");
      index++;
    }

    return builder.toString();
  }
}
