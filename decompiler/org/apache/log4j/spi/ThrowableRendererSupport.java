package org.apache.log4j.spi;

public interface ThrowableRendererSupport {
  ThrowableRenderer getThrowableRenderer();
  
  void setThrowableRenderer(ThrowableRenderer paramThrowableRenderer);
}
