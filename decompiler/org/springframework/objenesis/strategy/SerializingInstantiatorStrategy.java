package org.springframework.objenesis.strategy;

import java.io.NotSerializableException;
import java.io.Serializable;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.android.AndroidSerializationInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.springframework.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.springframework.objenesis.instantiator.perc.PercSerializationInstantiator;
import org.springframework.objenesis.instantiator.sun.SunReflectionFactorySerializationInstantiator;








































public class SerializingInstantiatorStrategy
  extends BaseInstantiatorStrategy
{
  public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
    if (!Serializable.class.isAssignableFrom(type)) {
      throw new ObjenesisException(new NotSerializableException(type + " not serializable"));
    }
    if (PlatformDescription.JVM_NAME.startsWith("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
      
      if (PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
        return (ObjectInstantiator<T>)new ObjectInputStreamInstantiator(type);
      }
      return (ObjectInstantiator<T>)new SunReflectionFactorySerializationInstantiator(type);
    } 
    if (PlatformDescription.JVM_NAME.startsWith("Dalvik")) {
      if (PlatformDescription.isAndroidOpenJDK()) {
        return (ObjectInstantiator<T>)new ObjectStreamClassInstantiator(type);
      }
      return (ObjectInstantiator<T>)new AndroidSerializationInstantiator(type);
    } 
    if (PlatformDescription.JVM_NAME.startsWith("GNU libgcj")) {
      return (ObjectInstantiator<T>)new GCJSerializationInstantiator(type);
    }
    if (PlatformDescription.JVM_NAME.startsWith("PERC")) {
      return (ObjectInstantiator<T>)new PercSerializationInstantiator(type);
    }
    
    return (ObjectInstantiator<T>)new SunReflectionFactorySerializationInstantiator(type);
  }
}
