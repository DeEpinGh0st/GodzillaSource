package org.bouncycastle.util;

public interface StringList extends Iterable<String> {
  boolean add(String paramString);
  
  String get(int paramInt);
  
  int size();
  
  String[] toStringArray();
  
  String[] toStringArray(int paramInt1, int paramInt2);
}
