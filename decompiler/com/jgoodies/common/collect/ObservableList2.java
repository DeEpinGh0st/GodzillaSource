package com.jgoodies.common.collect;

public interface ObservableList2<E> extends ObservableList<E> {
  void fireContentsChanged(int paramInt);
  
  void fireContentsChanged(int paramInt1, int paramInt2);
}
