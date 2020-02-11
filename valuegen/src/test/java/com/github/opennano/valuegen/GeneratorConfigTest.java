package com.github.opennano.valuegen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.github.opennano.testutils.JavaBeanTestUtil;
import com.github.opennano.valuegen.generator.strategies.CycleStrategy;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

public class GeneratorConfigTest {

  @Test
  public void isJavaBean() {
    JavaBeanTestUtil.assertValidJavaBeans(GeneratorConfig.class);
  }

  @Test
  public void defaultCycleStrategyIsReuseAncestor() {
    assertThat(new GeneratorConfig().getCycleStrategy(), is(CycleStrategy.REUSE_ANCESTOR_VALUE));
  }

  @Test
  public void defaultMapKeyStrategyIsAnyKey() {
    assertThat(new GeneratorConfig().getMapKeyStrategy(), is(MapKeyStrategy.ANY_KEY_TYPE));
  }

  @Test
  public void defaultSubtypeStrategyIsUnique() {
    assertThat(new GeneratorConfig().getSubTypeStrategy(), is(SubtypeStrategy.UNIQUE_SUBTYPE));
  }
}
