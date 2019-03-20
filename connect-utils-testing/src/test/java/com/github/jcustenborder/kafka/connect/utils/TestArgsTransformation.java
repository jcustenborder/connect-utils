package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationDanger;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationWarning;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

@Title("TestTransformation")
@Description("This transformation is used to rename fields in the key.")
@DocumentationNote("This is a note")
@DocumentationTip("This is a tip")
@DocumentationImportant("This is important")
@DocumentationDanger("This is a danger")
@DocumentationWarning("This is a warning")
public class TestArgsTransformation<R extends ConnectRecord<R>> implements Transformation<R> {
  public TestArgsTransformation(String a) {

  }

  @Override
  public R apply(R sourceRecord) {
    return null;
  }

  @Override
  public ConfigDef config() {
    return new ConfigDef()
        .define("important", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "This is an important value.");
  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
