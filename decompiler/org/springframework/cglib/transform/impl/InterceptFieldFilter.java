package org.springframework.cglib.transform.impl;

import org.springframework.asm.Type;

public interface InterceptFieldFilter {
  boolean acceptRead(Type paramType, String paramString);
  
  boolean acceptWrite(Type paramType, String paramString);
}
