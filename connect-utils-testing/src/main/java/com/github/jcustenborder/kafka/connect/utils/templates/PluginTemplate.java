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

import com.google.common.collect.ImmutableList;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.List;
import java.util.stream.Collectors;

public class PluginTemplate {
  final List<SourceConnectorTemplate> sourceConnectors;
  final List<SinkConnectorTemplate> sinkConnectors;
  final List<TransformationTemplate> transformations;

  private PluginTemplate(List<SourceConnectorTemplate> sourceConnectors, List<SinkConnectorTemplate> sinkConnectors, List<TransformationTemplate> transformations) {
    this.sourceConnectors = ImmutableList.copyOf(sourceConnectors);
    this.sinkConnectors = ImmutableList.copyOf(sinkConnectors);
    this.transformations = ImmutableList.copyOf(transformations);
  }

  public List<SourceConnectorTemplate> getSourceConnectors() {
    return sourceConnectors;
  }

  public List<SinkConnectorTemplate> getSinkConnectors() {
    return sinkConnectors;
  }

  public List<TransformationTemplate> getTransformations() {
    return transformations;
  }

  public static PluginTemplate from(List<Class<? extends SourceConnector>> sourceConnectorClasses, List<Class<? extends SinkConnector>> sinkConnectorClasses, List<Class<? extends Transformation>> transformationClasses) {
    final List<SourceConnectorTemplate> sourceConnectors = sourceConnectorClasses.stream()
        .map(aClass -> new SourceConnectorTemplate(aClass))
        .collect(Collectors.toList());
    final List<SinkConnectorTemplate> sinkConnectors = sinkConnectorClasses.stream()
        .map(aClass -> new SinkConnectorTemplate(aClass))
        .collect(Collectors.toList());
    final List<TransformationTemplate> transformations = transformationClasses.stream()
        .map(aClass -> new TransformationTemplate(aClass))
        .collect(Collectors.toList());

    return new PluginTemplate(sourceConnectors, sinkConnectors, transformations);
  }
}
