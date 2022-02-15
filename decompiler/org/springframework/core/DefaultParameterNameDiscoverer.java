package org.springframework.core;






































public class DefaultParameterNameDiscoverer
  extends PrioritizedParameterNameDiscoverer
{
  public DefaultParameterNameDiscoverer() {
    if (KotlinDetector.isKotlinReflectPresent() && !NativeDetector.inNativeImage()) {
      addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
    }
    addDiscoverer(new StandardReflectionParameterNameDiscoverer());
    addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
  }
}
