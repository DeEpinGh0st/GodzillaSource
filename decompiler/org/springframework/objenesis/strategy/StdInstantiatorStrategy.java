package org.springframework.objenesis.strategy;

import java.io.Serializable;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.android.Android10Instantiator;
import org.springframework.objenesis.instantiator.android.Android17Instantiator;
import org.springframework.objenesis.instantiator.android.Android18Instantiator;
import org.springframework.objenesis.instantiator.basic.AccessibleInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.springframework.objenesis.instantiator.gcj.GCJInstantiator;
import org.springframework.objenesis.instantiator.perc.PercInstantiator;
import org.springframework.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.springframework.objenesis.instantiator.sun.UnsafeFactoryInstantiator;








































public class StdInstantiatorStrategy
  extends BaseInstantiatorStrategy
{
  public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
    if (PlatformDescription.isThisJVM("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
      
      if (PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
        if (Serializable.class.isAssignableFrom(type)) {
          return (ObjectInstantiator<T>)new ObjectInputStreamInstantiator(type);
        }
        return (ObjectInstantiator<T>)new AccessibleInstantiator(type);
      } 

      
      return (ObjectInstantiator<T>)new SunReflectionFactoryInstantiator(type);
    } 
    if (PlatformDescription.isThisJVM("Dalvik")) {
      if (PlatformDescription.isAndroidOpenJDK())
      {
        return (ObjectInstantiator<T>)new UnsafeFactoryInstantiator(type);
      }
      if (PlatformDescription.ANDROID_VERSION <= 10)
      {
        return (ObjectInstantiator<T>)new Android10Instantiator(type);
      }
      if (PlatformDescription.ANDROID_VERSION <= 17)
      {
        return (ObjectInstantiator<T>)new Android17Instantiator(type);
      }
      
      return (ObjectInstantiator<T>)new Android18Instantiator(type);
    } 
    if (PlatformDescription.isThisJVM("GNU libgcj")) {
      return (ObjectInstantiator<T>)new GCJInstantiator(type);
    }
    if (PlatformDescription.isThisJVM("PERC")) {
      return (ObjectInstantiator<T>)new PercInstantiator(type);
    }

    
    return (ObjectInstantiator<T>)new UnsafeFactoryInstantiator(type);
  }
}
