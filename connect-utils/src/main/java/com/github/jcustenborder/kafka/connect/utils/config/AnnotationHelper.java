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
package com.github.jcustenborder.kafka.connect.utils.config;

public class AnnotationHelper {

  public static String description(Class<?> c) {
    Description description = c.getAnnotation(Description.class);
    return (null != description) ? description.value() : null;
  }

  public static String title(Class<?> c) {
    Title annotation = c.getAnnotation(Title.class);
    return (null != annotation) ? annotation.value() : c.getSimpleName();
  }
  
  public static String danger(Class<?> c) {
    DocumentationDanger annotation = c.getAnnotation(DocumentationDanger.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String important(Class<?> c) {
    DocumentationImportant annotation = c.getAnnotation(DocumentationImportant.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String tip(Class<?> c) {
    DocumentationTip annotation = c.getAnnotation(DocumentationTip.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String note(Class<?> c) {
    DocumentationNote annotation = c.getAnnotation(DocumentationNote.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String warning(Class<?> c) {
    DocumentationWarning annotation = c.getAnnotation(DocumentationWarning.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String introduction(Package p) {
    Introduction annotation = p.getAnnotation(Introduction.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String danger(Package p) {
    DocumentationDanger annotation = p.getAnnotation(DocumentationDanger.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String important(Package p) {
    DocumentationImportant annotation = p.getAnnotation(DocumentationImportant.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String tip(Package p) {
    DocumentationTip annotation = p.getAnnotation(DocumentationTip.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String note(Package p) {
    DocumentationNote annotation = p.getAnnotation(DocumentationNote.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String warning(Package p) {
    DocumentationWarning annotation = p.getAnnotation(DocumentationWarning.class);
    return (null != annotation) ? annotation.value() : null;
  }
}
