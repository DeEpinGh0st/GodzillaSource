package org.springframework.objenesis;

import org.springframework.core.SpringProperties;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.strategy.InstantiatorStrategy;
import org.springframework.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.util.ConcurrentReferenceHashMap;




































public class SpringObjenesis
  implements Objenesis
{
  public static final String IGNORE_OBJENESIS_PROPERTY_NAME = "spring.objenesis.ignore";
  private final InstantiatorStrategy strategy;
  private final ConcurrentReferenceHashMap<Class<?>, ObjectInstantiator<?>> cache = new ConcurrentReferenceHashMap();



  
  private volatile Boolean worthTrying;



  
  public SpringObjenesis() {
    this(null);
  }





  
  public SpringObjenesis(InstantiatorStrategy strategy) {
    this.strategy = (strategy != null) ? strategy : (InstantiatorStrategy)new StdInstantiatorStrategy();

    
    if (SpringProperties.getFlag("spring.objenesis.ignore")) {
      this.worthTrying = Boolean.FALSE;
    }
  }








  
  public boolean isWorthTrying() {
    return (this.worthTrying != Boolean.FALSE);
  }









  
  public <T> T newInstance(Class<T> clazz, boolean useCache) {
    if (!useCache) {
      return (T)newInstantiatorOf(clazz).newInstance();
    }
    return (T)getInstantiatorOf(clazz).newInstance();
  }
  
  public <T> T newInstance(Class<T> clazz) {
    return (T)getInstantiatorOf(clazz).newInstance();
  }

  
  public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
    ObjectInstantiator<?> instantiator = (ObjectInstantiator)this.cache.get(clazz);
    if (instantiator == null) {
      ObjectInstantiator<T> newInstantiator = newInstantiatorOf(clazz);
      instantiator = (ObjectInstantiator)this.cache.putIfAbsent(clazz, newInstantiator);
      if (instantiator == null) {
        instantiator = newInstantiator;
      }
    } 
    return (ObjectInstantiator)instantiator;
  }
  
  protected <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> clazz) {
    Boolean currentWorthTrying = this.worthTrying;
    try {
      ObjectInstantiator<T> instantiator = this.strategy.newInstantiatorOf(clazz);
      if (currentWorthTrying == null) {
        this.worthTrying = Boolean.TRUE;
      }
      return instantiator;
    }
    catch (ObjenesisException ex) {
      if (currentWorthTrying == null) {
        Throwable cause = ex.getCause();
        if (cause instanceof ClassNotFoundException || cause instanceof IllegalAccessException)
        {

          
          this.worthTrying = Boolean.FALSE;
        }
      } 
      throw ex;
    }
    catch (NoClassDefFoundError err) {

      
      if (currentWorthTrying == null) {
        this.worthTrying = Boolean.FALSE;
      }
      throw new ObjenesisException(err);
    } 
  }
}
