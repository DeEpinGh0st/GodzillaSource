package org.springframework.objenesis;

import org.springframework.objenesis.strategy.InstantiatorStrategy;
import org.springframework.objenesis.strategy.StdInstantiatorStrategy;






















public class ObjenesisStd
  extends ObjenesisBase
{
  public ObjenesisStd() {
    super((InstantiatorStrategy)new StdInstantiatorStrategy());
  }






  
  public ObjenesisStd(boolean useCache) {
    super((InstantiatorStrategy)new StdInstantiatorStrategy(), useCache);
  }
}
