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
package com.github.jcustenborder.kafka.connect.utils.util;

import com.google.common.base.Strings;

/**
 * Utility class to read the manifest of a jar and return the version. This must be added to your pom.
 * <p>
 * {@code             <plugin>
 * <groupId>org.apache.maven.plugins</groupId>
 * <artifactId>maven-jar-plugin</artifactId>
 * <configuration>
 * <archive>
 * <manifest>
 * <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
 * <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
 * </manifest>
 * </archive>
 * </configuration>
 * </plugin>}
 */
public class VersionUtil {
  public static String version(Class<?> cls) {
    final String FALLBACK_VERSION = "0.0.0.0";

    try {
      String version = cls.getPackage().getImplementationVersion();

      if (Strings.isNullOrEmpty(version)) {
        version = FALLBACK_VERSION;
      }

      return version;
    } catch (Exception ex) {
      return FALLBACK_VERSION;
    }
  }
}
