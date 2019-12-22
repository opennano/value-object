package com.github.opennano.valuegen;

import static com.github.opennano.valuegen.generator.strategies.CycleStrategy.REUSE_ANCESTOR_VALUE;
import static com.github.opennano.valuegen.generator.strategies.MapKeyStrategy.ANY_KEY_TYPE;
import static com.github.opennano.valuegen.generator.strategies.SubtypeStrategy.UNIQUE_SUBTYPE;

import com.github.opennano.valuegen.generator.strategies.CycleStrategy;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

public class GeneratorConfig {

  private CycleStrategy cycleStrategy = REUSE_ANCESTOR_VALUE;
  private MapKeyStrategy mapKeyStrategy = ANY_KEY_TYPE;
  private SubtypeStrategy subTypeStrategy = UNIQUE_SUBTYPE;

  public CycleStrategy getCycleStrategy() {
    return cycleStrategy;
  }

  public void setCycleStrategy(CycleStrategy cycleStrategy) {
    this.cycleStrategy = cycleStrategy;
  }

  public MapKeyStrategy getMapKeyStrategy() {
    return mapKeyStrategy;
  }

  public void setMapKeyStrategy(MapKeyStrategy mapKeyStrategy) {
    this.mapKeyStrategy = mapKeyStrategy;
  }

  public SubtypeStrategy getSubTypeStrategy() {
    return subTypeStrategy;
  }

  public void setSubTypeStrategy(SubtypeStrategy subTypeStrategy) {
    this.subTypeStrategy = subTypeStrategy;
  }
}
