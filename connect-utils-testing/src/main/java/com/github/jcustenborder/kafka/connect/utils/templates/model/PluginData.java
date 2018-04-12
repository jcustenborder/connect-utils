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
package com.github.jcustenborder.kafka.connect.utils.templates.model;

import com.github.jcustenborder.kafka.connect.utils.config.AnnotationHelper;
import com.github.jcustenborder.kafka.connect.utils.docs.Example;
import com.google.common.collect.ImmutableList;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PluginData {
  final List<SourceConnectorData> sourceConnectors;
  final List<SinkConnectorData> sinkConnectors;
  final List<TransformationData> transformations;

  private final String warning;
  private final String tip;
  private final String important;
  private final String danger;
  private final String note;
  private final String introduction;

  private PluginData(Package p, List<SourceConnectorData> sourceConnectors, List<SinkConnectorData> sinkConnectors, List<TransformationData> transformations) {
    this.sourceConnectors = ImmutableList.copyOf(sourceConnectors);
    this.sinkConnectors = ImmutableList.copyOf(sinkConnectors);
    this.transformations = ImmutableList.copyOf(transformations);

    this.warning = AnnotationHelper.warning(p);
    this.tip = AnnotationHelper.tip(p);
    this.important = AnnotationHelper.important(p);
    this.danger = AnnotationHelper.danger(p);
    this.note = AnnotationHelper.note(p);
    this.introduction = AnnotationHelper.introduction(p);
  }

  public static PluginData from(
      Package pkg,
      Set<String> resources,
      List<Class<? extends SourceConnector>> sourceConnectorClasses,
      List<Class<? extends SinkConnector>> sinkConnectorClasses,
      List<Class<? extends Transformation>> transformationClasses) {
    final List<SourceConnectorData> sourceConnectors = sourceConnectorClasses.stream()
        .map(SourceConnectorData::new)
        .collect(Collectors.toList());
    for (Configurable template : sourceConnectors) {
      final List<Example> examples = resources.stream()
          .filter(p -> p.startsWith(template.getResourceFilter()))
          .map(p -> Example.load(template.getCls(), "/" + p))
          .collect(Collectors.toList());
      template.setExamples(examples);
    }

    final List<SinkConnectorData> sinkConnectors = sinkConnectorClasses.stream()
        .map(SinkConnectorData::new)
        .collect(Collectors.toList());
    for (Configurable template : sinkConnectors) {
      final List<Example> examples = resources.stream()
          .filter(p -> p.startsWith(template.getResourceFilter()))
          .map(p -> Example.load(template.getCls(), "/" + p))
          .collect(Collectors.toList());
      template.setExamples(examples);
    }

    final List<TransformationData> transformations = transformationClasses.stream()
        .map(TransformationData::new)
        .collect(Collectors.toList());
    for (Configurable template : transformations) {
      final List<Example> examples = resources.stream()
          .filter(p -> p.startsWith(template.getResourceFilter()))
          .map(p -> Example.load(template.getCls(), "/" + p))
          .collect(Collectors.toList());
      template.setExamples(examples);
    }
    return new PluginData(pkg, sourceConnectors, sinkConnectors, transformations);
  }

  public List<Configurable> all() {
    List<Configurable> all = new ArrayList<>();
    all.addAll(this.sourceConnectors);
    all.addAll(this.sinkConnectors);
    all.addAll(this.transformations);
    return ImmutableList.copyOf(all);
  }

  public List<SourceConnectorData> getSourceConnectors() {
    return sourceConnectors;
  }

  public List<SinkConnectorData> getSinkConnectors() {
    return sinkConnectors;
  }

  public List<TransformationData> getTransformations() {
    return transformations;
  }

  public String getWarning() {
    return warning;
  }

  public String getTip() {
    return tip;
  }

  public String getImportant() {
    return important;
  }

  public String getDanger() {
    return danger;
  }

  public String getNote() {
    return note;
  }

  public String getIntroduction() {
    return introduction;
  }
}
