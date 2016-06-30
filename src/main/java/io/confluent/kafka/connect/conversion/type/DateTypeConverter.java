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
package io.confluent.kafka.connect.conversion.type;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTypeConverter implements TypeConverter {
  final static Logger log = LoggerFactory.getLogger(DateTypeConverter.class);
  final SimpleDateFormat[] dateFormats;
  final TimeZone timeZone;

  public DateTypeConverter(TimeZone timeZone, SimpleDateFormat... dateFormats) {
    this.dateFormats = dateFormats;
    this.timeZone = timeZone;
  }

  public static DateTypeConverter createDefaultDateConverter() {
    return new DateTypeConverter(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd"));
  }

  public static DateTypeConverter createDefaultTimeConverter() {
    return new DateTypeConverter(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("HH:mm:ss"));
  }

  public static DateTypeConverter createDefaultTimestampConverter() {
    return new DateTypeConverter(TimeZone.getTimeZone("UTC"), new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
  }

  @Override
  public Object convert(String s) {
    Date date = null;
    for (SimpleDateFormat dateFormat : this.dateFormats) {
      try {
        date = dateFormat.parse(s);
        continue;
      } catch (ParseException e) {
        if (log.isDebugEnabled()) {
          log.debug("Could not parse '{}' to java.util.Date", s, date);
        }
      }
    }

    Preconditions.checkState(null != date, "Could not parse '%s' to java.util.Date", s);
    return date;
  }
}
