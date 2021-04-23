package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationDanger;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationWarning;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.storage.Converter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Description("The test source connector is used to simulate the usage fromConnector an actual connector that we would generate " +
    "documentation from.")
@Title("Test Source Connector")
@DocumentationNote("This is a note")
@DocumentationTip("This is a tip")
@DocumentationImportant("This is important")
@DocumentationDanger("This is a danger")
@DocumentationWarning("This is a warning")
public class TestConfigProvider implements ConfigProvider {

  @Override
  public ConfigData get(String s) {
    return null;
  }

  @Override
  public ConfigData get(String s, Set<String> set) {
    return null;
  }

  @Override
  public void close() throws IOException {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
