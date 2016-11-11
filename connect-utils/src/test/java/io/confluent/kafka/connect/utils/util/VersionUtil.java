package io.confluent.kafka.connect.utils.util;

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
