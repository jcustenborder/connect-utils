/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.data.type;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import org.apache.kafka.connect.data.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTypeParser implements TypeParser {
  final static Logger log = LoggerFactory.getLogger(DateTypeParser.class);
  final SimpleDateFormat[] dateFormats;
  final TimeZone timeZone;

  public DateTypeParser(TimeZone timeZone, SimpleDateFormat... dateFormats) {
    this.dateFormats = dateFormats;
    this.timeZone = timeZone;
  }

  public static DateTypeParser createDefaultDateConverter() {
    return new DateTypeParser(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd"));
  }

  public static DateTypeParser createDefaultTimeConverter() {
    return new DateTypeParser(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("HH:mm:ss"));
  }

  public static DateTypeParser createDefaultTimestampConverter() {
    return new DateTypeParser(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
  }

  @Override
  public Object parseString(String s, final Schema schema) {
    Date date = null;
    for (SimpleDateFormat dateFormat : this.dateFormats) {
      try {
        date = dateFormat.parse(s);
        break;
      } catch (ParseException e) {
        if (log.isDebugEnabled()) {
          log.debug("Could not parse '{}' to java.util.Date", s, e);
        }
      }
    }

    Preconditions.checkState(null != date, "Could not parse '%s' to java.util.Date", s);
    return date;
  }

  @Override
  public Class<?> expectedClass() {
    return java.util.Date.class;
  }

  @Override
  public Object parseJsonNode(JsonNode input, Schema schema) {
    Preconditions.checkState(input.isLong() || input.isInt(), "'%s' is not a '%s'", input.textValue(), expectedClass().getSimpleName());
    return new java.util.Date(input.longValue());
  }
}
