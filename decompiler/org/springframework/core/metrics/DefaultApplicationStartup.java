package org.springframework.core.metrics;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;























class DefaultApplicationStartup
  implements ApplicationStartup
{
  private static final DefaultStartupStep DEFAULT_STARTUP_STEP = new DefaultStartupStep();

  
  public DefaultStartupStep start(String name) {
    return DEFAULT_STARTUP_STEP;
  }
  
  static class DefaultStartupStep
    implements StartupStep
  {
    private final DefaultTags TAGS = new DefaultTags();

    
    public String getName() {
      return "default";
    }

    
    public long getId() {
      return 0L;
    }

    
    public Long getParentId() {
      return null;
    }

    
    public StartupStep.Tags getTags() {
      return this.TAGS;
    }

    
    public StartupStep tag(String key, String value) {
      return this;
    }

    
    public StartupStep tag(String key, Supplier<String> value) {
      return this;
    }


    
    public void end() {}


    
    static class DefaultTags
      implements StartupStep.Tags
    {
      public Iterator<StartupStep.Tag> iterator() {
        return Collections.emptyIterator();
      }
    }
  }
}
