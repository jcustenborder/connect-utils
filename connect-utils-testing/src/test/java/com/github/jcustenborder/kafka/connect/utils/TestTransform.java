package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

@Description("This is a testing transformation.")
public class TestTransform implements Transformation<SourceRecord> {
  @Override
  public SourceRecord apply(SourceRecord sourceRecord) {
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
