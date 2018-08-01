package com.github.jcustenborder.kafka.connect.utils.config.recommenders;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharsetRecommenderTest {
  @Test
  public void validateDefault() {
    final ConfigDef.Recommender recommender = Recommenders.charset();
    final List<Object> expected = ImmutableList.copyOf(Charset.availableCharsets().keySet());
    final List<Object> actual = recommender.validValues("foo", ImmutableMap.of());
    assertEquals(expected, actual);
    assertTrue(recommender.visible("foo", ImmutableMap.of()));
  }

  @Test
  public void minimalDefault() {
    final ConfigDef.Recommender recommender = Recommenders.charset(VisibleCallback.ALWAYS_VISIBLE, Charsets.UTF_8.name());
    final List<Object> expected = ImmutableList.of(Charsets.UTF_8.name());
    final List<Object> actual = recommender.validValues("foo", ImmutableMap.of());
    assertEquals(expected, actual);
    assertTrue(recommender.visible("foo", ImmutableMap.of()));
  }
}
