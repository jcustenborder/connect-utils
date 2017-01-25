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
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;

class ConnectTemplateHashModel extends WrappingTemplateModel implements TemplateHashModel {

  final ConnectRecord connectRecord;
  final Struct struct;

  ConnectTemplateHashModel(ConnectRecord connectRecord, Struct struct) {
    this.connectRecord = connectRecord;
    this.struct = struct;
  }

  @Override
  public TemplateModel get(String s) throws TemplateModelException {
    Object value = null;

    if ("topic".equals(s)) {
      value = this.connectRecord.topic();
    } else if ("partition".equals(s)) {
      value = this.connectRecord.kafkaPartition();
    } else if (null != (this.struct.schema().field(s))) {
      value = this.struct.get(s);
    }

    if (null == value) {
      return TemplateModel.NOTHING;
    }

    return wrap(value);
  }

  @Override
  public boolean isEmpty() throws TemplateModelException {
    return false;
  }
}
