package org.springframework.cglib.transform.impl;

public interface InterceptFieldEnabled {
  void setInterceptFieldCallback(InterceptFieldCallback paramInterceptFieldCallback);
  
  InterceptFieldCallback getInterceptFieldCallback();
}
