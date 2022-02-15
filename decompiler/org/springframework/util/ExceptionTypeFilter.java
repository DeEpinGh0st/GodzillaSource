package org.springframework.util;

import java.util.Collection;

























public class ExceptionTypeFilter
  extends InstanceFilter<Class<? extends Throwable>>
{
  public ExceptionTypeFilter(Collection<? extends Class<? extends Throwable>> includes, Collection<? extends Class<? extends Throwable>> excludes, boolean matchIfEmpty) {
    super(includes, excludes, matchIfEmpty);
  }

  
  protected boolean match(Class<? extends Throwable> instance, Class<? extends Throwable> candidate) {
    return candidate.isAssignableFrom(instance);
  }
}
