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
package com.github.jcustenborder.kafka.connect.utils;

import com.google.common.collect.LinkedHashMultimap;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;
import static com.github.jcustenborder.kafka.connect.utils.GenericAssertions.assertMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertConnectRecord {

  public static void assertValue(Object expected, Object actual, String message) {
    final String prefix = null != message ? message + ": " : "";

    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
      return;
    }
    assertNotNull(actual, prefix + "actual should be null.");

    if (expected instanceof Struct) {
      assertTrue(actual instanceof Struct, prefix + "actual should be a Struct.");
      Struct expectedStruct = (Struct) expected;
      Struct actualStruct = (Struct) actual;
      assertStruct(expectedStruct, actualStruct, message);
    } else if (expected instanceof Map) {
      assertTrue(actual instanceof Map, prefix + "actual should be a Map.");
      Map expectedMap = (Map) expected;
      Map actualMap = (Map) actual;
      assertMap(expectedMap, actualMap, message);
    } else if (expected instanceof byte[]) {
      assertTrue(actual instanceof byte[], prefix + "actual should be a byte[].");
      byte[] expectedByteArray = (byte[]) expected;
      byte[] actualByteArray = (byte[]) actual;
      assertArrayEquals(expectedByteArray, actualByteArray, prefix + "actual does not match");
    } else {
      assertEquals(expected, actual, message);
    }
  }

  public static void assertRecord(ConnectRecord expected, ConnectRecord actual) {
    assertRecord(expected, actual, null);
  }

  public static void assertRecord(ConnectRecord expected, ConnectRecord actual, String message) {
    final String prefix = null != message ? message + ": " : "";
    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
      return;
    }

    assertNotNull(actual, prefix + "actual should not be null.");
    assertEquals(expected.kafkaPartition(), actual.kafkaPartition(), prefix + "kafkaPartition() does not match.");
    assertEquals(expected.topic(), actual.topic(), prefix + "topic() does not match.");
    assertEquals(expected.timestamp(), actual.timestamp(), prefix + "timestamp() does not match.");
    assertSchema(expected.keySchema(), actual.keySchema(), prefix + "keySchema() does not match");
    assertValue(expected.key(), actual.key(), prefix + "key() does not match.");
    assertSchema(expected.valueSchema(), actual.valueSchema(), prefix + "valueSchema() does not match");
    assertValue(expected.value(), actual.value(), prefix + "value() does not match.");
    assertHeaders(expected.headers(), actual.headers(), prefix + "headers() does not match.");
  }

  public static void assertSourceRecord(SourceRecord expected, SourceRecord actual) {
    assertSourceRecord(expected, actual, null);
  }

  public static void assertSourceRecord(SourceRecord expected, SourceRecord actual, String message) {
    final String prefix = null != message ? message + ": " : "";
    assertRecord(expected, actual, message);
    assertMap(expected.sourceOffset(), actual.sourceOffset(), prefix + "sourceOffset() does not match.");
    assertMap(expected.sourcePartition(), actual.sourcePartition(), prefix + "sourcePartition() does not match.");
  }

  public static void assertHeader(Header expected, Header actual) {
    assertHeader(expected, actual, null);
  }

  public static void assertHeader(Header expected, Header actual, String message) {
    final String prefix = null != message ? message + ": " : "";
    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
    }
    assertNotNull(actual, prefix + "actual should not be null.");
    assertEquals(expected.key(), expected.key(), prefix + "key() does not match.");
    assertValue(expected.value(), actual.value(), prefix + "value() does not match.");
    assertSchema(expected.schema(), actual.schema(), prefix + "schema() does not match.");
  }

  public static void assertHeaders(Headers expected, Headers actual) {
    assertHeaders(expected, actual, null);
  }

  public static void assertHeaders(Headers expected, Headers actual, String message) {
    final String prefix = null != message ? message + ": " : "";
    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
    }
    assertNotNull(actual, prefix + "actual should not be null.");

    assertEquals(expected.size(), actual.size(), prefix + "size() should match.");

    LinkedHashMultimap<String, Header> expectedHeaders = LinkedHashMultimap.create();
    for (Header header : expected) {
      expectedHeaders.put(header.key(), header);
    }
    LinkedHashMultimap<String, Header> actualHeaders = LinkedHashMultimap.create();
    for (Header header : actual) {
      actualHeaders.put(header.key(), header);
    }

    final Set<String> expectedKeys = StreamSupport.stream(expected.spliterator(), false)
        .map(Header::key)
        .collect(Collectors.toSet());

    for (String key : expectedKeys) {
      Header[] expectedHeaderSet = expectedHeaders.get(key).toArray(new Header[expectedHeaders.size()]);
      Header[] actualHeaderSet = actualHeaders.get(key).toArray(new Header[actualHeaders.size()]);

      if (null == expectedHeaderSet) {
        assertNull(actualHeaderSet, prefix + "header \"" + key + "\" should be null.");
      }
      assertNotNull(actualHeaderSet, prefix + "header \"" + key + "\" should be null.");
      assertEquals(expectedHeaderSet.length, actualHeaderSet.length, "length does not match.");
      for (int i = 0; i < expectedHeaderSet.length; i++) {
        assertHeader(expectedHeaderSet[i], actualHeaderSet[i],
            String.format("%sheader for key '%s' does not match at index %s", prefix, key, i)
        );
      }
    }
  }


}
