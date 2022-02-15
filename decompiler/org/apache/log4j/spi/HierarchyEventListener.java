package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;

public interface HierarchyEventListener {
  void addAppenderEvent(Category paramCategory, Appender paramAppender);
  
  void removeAppenderEvent(Category paramCategory, Appender paramAppender);
}
