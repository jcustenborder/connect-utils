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
package com.github.jcustenborder.kafka.connect.utils.template;

import com.google.common.base.Preconditions;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.InvalidReferenceException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class StructTemplate {
  final Configuration configuration;
  final StringTemplateLoader loader;

  public StructTemplate() {
    this.configuration = new Configuration(Configuration.getVersion());
    this.loader = new StringTemplateLoader();
    this.configuration.setTemplateLoader(this.loader);
    this.configuration.setDefaultEncoding("UTF-8");
    this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    this.configuration.setLogTemplateExceptions(false);
    this.configuration.setObjectWrapper(new ConnectObjectWrapper());
  }

  public void addTemplate(String templateName, String template) {
    this.loader.putTemplate(templateName, template);
  }

  private String executeInternal(String templateName, Object value) {
    Preconditions.checkNotNull(templateName, "templateName cannot be null.");
    Preconditions.checkNotNull(value, "values cannot be null.");
    Template template;

    try {
      template = this.configuration.getTemplate(templateName);
    } catch (IOException ex) {
      throw new DataException(
          String.format("Exception thrown while loading template '%s'", templateName),
          ex
      );
    }

    try (StringWriter writer = new StringWriter()) {
      template.process(value, writer);
      return writer.toString();
    } catch (IOException e) {
      throw new ConnectException("Exception while processing template", e);
    } catch (InvalidReferenceException e) {
      throw new DataException(
          String.format(
              "Exception thrown while processing template. Offending expression '%s'",
              e.getBlamedExpressionString()
          ),
          e);
    } catch (TemplateException e) {
      throw new ConnectException("Exception while processing template", e);
    }
  }

  public String execute(String templateName, Struct struct) {
    return executeInternal(templateName, struct);
  }

  public String execute(String templateName, Map<String, ?> values) {
    return executeInternal(templateName, values);
  }

  public String execute(String templateName, ConnectRecord record, Struct struct, Map<String, ?> additionalValues) {
    ConnectTemplateHashModel connectTemplateHashModel = new ConnectTemplateHashModel(record, struct);
    return executeInternal(templateName, connectTemplateHashModel);
  }
}
