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
package com.github.jcustenborder.kafka.connect.utils.templates;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationDanger;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationSections;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationWarning;
import com.github.jcustenborder.kafka.connect.utils.config.Icon;
import com.github.jcustenborder.kafka.connect.utils.config.Introduction;
import com.github.jcustenborder.kafka.connect.utils.config.PluginName;
import com.github.jcustenborder.kafka.connect.utils.config.PluginOwner;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import com.google.common.base.CaseFormat;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AnnotationHelper {

  private static String description(Class<?> c) {
    Description description = c.getAnnotation(Description.class);
    return (null != description) ? description.value() : null;
  }

  private static String title(Class<?> c) {
    Title annotation = c.getAnnotation(Title.class);
    return (null != annotation) ? annotation.value() : c.getSimpleName();
  }

  private static String danger(Class<?> c) {
    DocumentationDanger annotation = c.getAnnotation(DocumentationDanger.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String important(Class<?> c) {
    DocumentationImportant annotation = c.getAnnotation(DocumentationImportant.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String tip(Class<?> c) {
    DocumentationTip annotation = c.getAnnotation(DocumentationTip.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String note(Class<?> c) {
    DocumentationNote annotation = c.getAnnotation(DocumentationNote.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String warning(Class<?> c) {
    DocumentationWarning annotation = c.getAnnotation(DocumentationWarning.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String introduction(Package p) {
    Introduction annotation = p.getAnnotation(Introduction.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String danger(Package p) {
    DocumentationDanger annotation = p.getAnnotation(DocumentationDanger.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String important(Package p) {
    DocumentationImportant annotation = p.getAnnotation(DocumentationImportant.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String tip(Package p) {
    DocumentationTip annotation = p.getAnnotation(DocumentationTip.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String note(Package p) {
    DocumentationNote annotation = p.getAnnotation(DocumentationNote.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String warning(Package p) {
    DocumentationWarning annotation = p.getAnnotation(DocumentationWarning.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String title(Package p) {
    Title annotation = p.getAnnotation(Title.class);
    return (null != annotation) ? annotation.value() : CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, p.getName());
  }

  public static String pluginOwner(Package p) {
    PluginOwner annotation = p.getAnnotation(PluginOwner.class);
    return (null != annotation) ? annotation.value() : null;
  }

  public static String pluginName(Package p) {
    PluginName annotation = p.getAnnotation(PluginName.class);
    return (null != annotation) ? annotation.value() : null;
  }

  private static String icon(Package p) {
    Icon annotation = p.getAnnotation(Icon.class);
    return (null != annotation) ? annotation.path() : null;
  }

  private static String icon(Class<?> c) {
    Icon annotation = c.getAnnotation(Icon.class);
    return (null != annotation) ? annotation.path() : null;
  }

  public static Notes notes(Class<?> c) {
    return new AnnotatedNotes(c);
  }

  public static Notes notes(Package p) {
    return new AnnotatedNotes(p);
  }

  static abstract class AnnotationReader {
    final AnnotatedElement annotatedElement;

    protected AnnotationReader(AnnotatedElement annotatedElement) {
      this.annotatedElement = annotatedElement;
    }

    protected <T extends Annotation, U> U getValue(Class<T> cls, Function<T, U> function) {
      T annotation = this.annotatedElement.getAnnotation(cls);
      U result;
      if (null != annotation) {
        result = function.apply(annotation);
      } else {
        result = null;
      }
      return result;
    }
  }

  static class AnnotatedNotes extends AnnotationReader implements Notes {
    protected AnnotatedNotes(AnnotatedElement annotatedElement) {
      super(annotatedElement);
    }

    @Nullable
    @Override
    public String getWarning() {
      return getValue(DocumentationWarning.class, DocumentationWarning::value);
    }

    @Nullable
    @Override
    public String getTip() {
      return getValue(DocumentationTip.class, DocumentationTip::value);
    }

    @Nullable
    @Override
    public String getImportant() {
      return getValue(DocumentationImportant.class, DocumentationImportant::value);
    }

    @Nullable
    @Override
    public String getDanger() {
      return getValue(DocumentationDanger.class, DocumentationDanger::value);
    }

    @Nullable
    @Override
    public String getNote() {
      return getValue(DocumentationNote.class, DocumentationNote::value);
    }

    @Nullable
    @Override
    public String getTitle() {
      if (this.annotatedElement instanceof Class<?>) {
        Class<?> o = (Class<?>) this.annotatedElement;
        return title(o);
      } else if (this.annotatedElement instanceof Package) {
        Package o = (Package) this.annotatedElement;
        return title(o);
      } else {
        throw new UnsupportedOperationException();
      }
    }

    @Nullable
    @Override
    public String getDescription() {
      return getValue(Description.class, Description::value);
    }

    @Nullable
    @Override
    public String getIcon() {
      return getValue(Icon.class, Icon::path);
    }

    @Nullable
    @Override
    public String getIntroduction() {
      return getValue(Introduction.class, Introduction::value);
    }

    @Override
    public List<Section> getSections() {
      List<Section> sections = getValue(DocumentationSections.class, documentationSections -> Stream.of(documentationSections.sections())
          .map(s -> ImmutableSection.builder()
              .title(s.title())
              .text(s.text())
              .build()
          )
          .collect(Collectors.toList()));
      if (null == sections) {
        sections = new ArrayList<>();
      }
      return sections;
    }
  }


}
