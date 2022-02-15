package org.springframework.util.comparator;

import java.util.Comparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;






































public class InstanceComparator<T>
  implements Comparator<T>
{
  private final Class<?>[] instanceOrder;
  
  public InstanceComparator(Class<?>... instanceOrder) {
    Assert.notNull(instanceOrder, "'instanceOrder' array must not be null");
    this.instanceOrder = instanceOrder;
  }


  
  public int compare(T o1, T o2) {
    int i1 = getOrder(o1);
    int i2 = getOrder(o2);
    return Integer.compare(i1, i2);
  }
  
  private int getOrder(@Nullable T object) {
    if (object != null) {
      for (int i = 0; i < this.instanceOrder.length; i++) {
        if (this.instanceOrder[i].isInstance(object)) {
          return i;
        }
      } 
    }
    return this.instanceOrder.length;
  }
}
