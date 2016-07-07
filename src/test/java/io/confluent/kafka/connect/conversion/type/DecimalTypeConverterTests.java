package io.confluent.kafka.connect.conversion.type;


import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DecimalTypeConverterTests {

  @Test
  public void testSchemaCache() {
    List<Schema> schemas = new ArrayList<>();
    for (int i = 1; i < 30; i++) {
      schemas.add(Decimal.schema(i));
    }

    DecimalTypeConverter typeConverter = new DecimalTypeConverter();

    for (int i = 0; i < 1000; i++) {
      for (Schema schema : schemas) {
        Object value = typeConverter.convert("0", schema);
      }
    }

    Assert.assertThat((int) typeConverter.schemaCache.size(), IsEqual.equalTo(schemas.size()));
  }

}
