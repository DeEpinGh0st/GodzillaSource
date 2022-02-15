package org.springframework.objenesis;

import java.io.Serializable;
import org.springframework.objenesis.instantiator.ObjectInstantiator;






















public final class ObjenesisHelper
{
  private static final Objenesis OBJENESIS_STD = new ObjenesisStd();
  
  private static final Objenesis OBJENESIS_SERIALIZER = new ObjenesisSerializer();










  
  public static <T> T newInstance(Class<T> clazz) {
    return OBJENESIS_STD.newInstance(clazz);
  }








  
  public static <T extends Serializable> T newSerializableInstance(Class<T> clazz) {
    return (T)OBJENESIS_SERIALIZER.<Serializable>newInstance(clazz);
  }









  
  public static <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
    return OBJENESIS_STD.getInstantiatorOf(clazz);
  }









  
  public static <T extends Serializable> ObjectInstantiator<T> getSerializableObjectInstantiatorOf(Class<T> clazz) {
    return OBJENESIS_SERIALIZER.getInstantiatorOf(clazz);
  }
}
