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

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.WrappingTemplateModel;

import java.util.Map;

class MissingValueHashWrapper extends WrappingTemplateModel implements TemplateHashModel {
  final Map<String, ?> values;

  MissingValueHashWrapper(Map<String, ?> values) {
    this.values = values;
  }

  @Override
  public TemplateModel get(String s) throws TemplateModelException {
    Object value = values.get(s);

    if (null == value) {
      return TemplateModel.NOTHING;
    } else {
      return wrap(value);
    }
  }

  @Override
  public boolean isEmpty() throws TemplateModelException {
    return false;
  }
}
