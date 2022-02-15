package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;




























public class PrioritizedParameterNameDiscoverer
  implements ParameterNameDiscoverer
{
  private final List<ParameterNameDiscoverer> parameterNameDiscoverers = new ArrayList<>(2);





  
  public void addDiscoverer(ParameterNameDiscoverer pnd) {
    this.parameterNameDiscoverers.add(pnd);
  }


  
  @Nullable
  public String[] getParameterNames(Method method) {
    for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
      String[] result = pnd.getParameterNames(method);
      if (result != null) {
        return result;
      }
    } 
    return null;
  }

  
  @Nullable
  public String[] getParameterNames(Constructor<?> ctor) {
    for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
      String[] result = pnd.getParameterNames(ctor);
      if (result != null) {
        return result;
      }
    } 
    return null;
  }
}
