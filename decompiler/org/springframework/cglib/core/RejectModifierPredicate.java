package org.springframework.cglib.core;

import java.lang.reflect.Member;














public class RejectModifierPredicate
  implements Predicate
{
  private int rejectMask;
  
  public RejectModifierPredicate(int rejectMask) {
    this.rejectMask = rejectMask;
  }
  
  public boolean evaluate(Object arg) {
    return ((((Member)arg).getModifiers() & this.rejectMask) == 0);
  }
}
