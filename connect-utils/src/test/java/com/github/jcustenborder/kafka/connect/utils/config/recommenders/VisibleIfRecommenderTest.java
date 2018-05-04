package com.github.jcustenborder.kafka.connect.utils.config.recommenders;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisibleIfRecommenderTest {

  @Test
  public void visible() {
    ConfigDef.Recommender recommender = Recommenders.visibleIf("ssl.enabled", true);
    assertTrue(recommender.visible("somekey", ImmutableMap.of("ssl.enabled", true)));
    assertFalse(recommender.visible("somekey", ImmutableMap.of("ssl.enabled", false)));
  }

}
