package org.springframework.objenesis;

import org.springframework.objenesis.strategy.InstantiatorStrategy;
import org.springframework.objenesis.strategy.SerializingInstantiatorStrategy;






















public class ObjenesisSerializer
  extends ObjenesisBase
{
  public ObjenesisSerializer() {
    super((InstantiatorStrategy)new SerializingInstantiatorStrategy());
  }






  
  public ObjenesisSerializer(boolean useCache) {
    super((InstantiatorStrategy)new SerializingInstantiatorStrategy(), useCache);
  }
}
