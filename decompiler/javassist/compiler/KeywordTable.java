package javassist.compiler;

import java.util.HashMap;


















public final class KeywordTable
  extends HashMap<String, Integer>
{
  private static final long serialVersionUID = 1L;
  
  public int lookup(String name) {
    return containsKey(name) ? get(name).intValue() : -1;
  }
  
  public void append(String name, int t) {
    put(name, Integer.valueOf(t));
  }
}
