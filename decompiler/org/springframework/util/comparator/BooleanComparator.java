package org.springframework.util.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.springframework.lang.Nullable;





























public class BooleanComparator
  implements Comparator<Boolean>, Serializable
{
  public static final BooleanComparator TRUE_LOW = new BooleanComparator(true);




  
  public static final BooleanComparator TRUE_HIGH = new BooleanComparator(false);






  
  private final boolean trueLow;






  
  public BooleanComparator(boolean trueLow) {
    this.trueLow = trueLow;
  }


  
  public int compare(Boolean v1, Boolean v2) {
    return ((v1.booleanValue() ^ v2.booleanValue()) != 0) ? (((v1.booleanValue() ^ this.trueLow) != 0) ? 1 : -1) : 0;
  }


  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof BooleanComparator && this.trueLow == ((BooleanComparator)other).trueLow));
  }


  
  public int hashCode() {
    return getClass().hashCode() * (this.trueLow ? -1 : 1);
  }

  
  public String toString() {
    return "BooleanComparator: " + (this.trueLow ? "true low" : "true high");
  }
}
