package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;




















@GwtCompatible(serializable = true)
class EmptyImmutableListMultimap
  extends ImmutableListMultimap<Object, Object>
{
  static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
  
  private EmptyImmutableListMultimap() {
    super(ImmutableMap.of(), 0);
  }
  private static final long serialVersionUID = 0L;
  private Object readResolve() {
    return INSTANCE;
  }
}
