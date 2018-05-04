package com.github.jcustenborder.kafka.connect.utils.config.recommenders;

import com.github.jcustenborder.kafka.connect.utils.config.TestEnum;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumRecommenderTest {
  @Test
  public void visible() {
    ConfigDef.Recommender recommender = Recommenders.enumValues(TestEnum.class);
    final List<Object> actual = recommender.validValues("asdf", ImmutableMap.of());
    final List<Object> expected = ImmutableList.copyOf(Arrays.stream(TestEnum.values()).map(Enum::toString).toArray());
    assertEquals(expected, actual);
  }
}
