package org.apache.log4j.spi;

public interface ThrowableRenderer {
  String[] doRender(Throwable paramThrowable);
}
