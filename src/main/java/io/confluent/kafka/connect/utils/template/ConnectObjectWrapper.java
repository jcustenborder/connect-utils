/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.template;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.Map;

class ConnectObjectWrapper extends DefaultObjectWrapper {

  @Override
  public TemplateModel wrap(Object obj) throws TemplateModelException {
    if (obj instanceof Map) {
      return new MissingValueHashWrapper((Map<String, ?>) obj);
    }

    return super.wrap(obj);
  }

  @Override
  protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
    if (obj instanceof ConnectObjectWrapper) {
      return (TemplateModel) obj;
    }

    return super.handleUnknownType(obj);
  }
}
