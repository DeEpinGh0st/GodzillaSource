package com.jediterm.terminal.util;

import java.lang.reflect.Array;
import java.util.BitSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class Util
{
  public static <T> T[] copyOf(T[] original, int newLength) {
    Class<T> type = (Class)original.getClass().getComponentType();
    T[] newArr = (T[])Array.newInstance(type, newLength);
    
    System.arraycopy(original, 0, newArr, 0, Math.min(original.length, newLength));
    
    return newArr;
  }
  
  public static int[] copyOf(int[] original, int newLength) {
    int[] newArr = new int[newLength];
    
    System.arraycopy(original, 0, newArr, 0, Math.min(original.length, newLength));
    
    return newArr;
  }
  
  public static char[] copyOf(char[] original, int newLength) {
    char[] newArr = new char[newLength];
    
    System.arraycopy(original, 0, newArr, 0, Math.min(original.length, newLength));
    
    return newArr;
  }
  
  public static void bitsetCopy(BitSet src, int srcOffset, BitSet dest, int destOffset, int length) {
    for (int i = 0; i < length; i++) {
      dest.set(destOffset + i, src.get(srcOffset + i));
    }
  }
  
  public static String trimTrailing(String string) {
    int index = string.length() - 1;
    for (; index >= 0 && Character.isWhitespace(string.charAt(index)); index--);
    return string.substring(0, index + 1);
  }

  
  public static boolean containsIgnoreCase(@NotNull String where, @NotNull String what) {
    if (where == null) $$$reportNull$$$0(0);  if (what == null) $$$reportNull$$$0(1);  return (indexOfIgnoreCase(where, what, 0) >= 0);
  }



  
  public static int indexOfIgnoreCase(@NotNull String where, @NotNull String what, int fromIndex) {
    if (where == null) $$$reportNull$$$0(2);  if (what == null) $$$reportNull$$$0(3);  int targetCount = what.length();
    int sourceCount = where.length();
    
    if (fromIndex >= sourceCount) {
      return (targetCount == 0) ? sourceCount : -1;
    }
    
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    
    if (targetCount == 0) {
      return fromIndex;
    }
    
    char first = what.charAt(0);
    int max = sourceCount - targetCount;
    
    for (int i = fromIndex; i <= max; i++) {
      
      if (!charsEqualIgnoreCase(where.charAt(i), first)) {
        while (++i <= max && !charsEqualIgnoreCase(where.charAt(i), first));
      }

      
      if (i <= max) {
        int j = i + 1;
        int end = j + targetCount - 1;
        for (int k = 1; j < end && charsEqualIgnoreCase(where.charAt(j), what.charAt(k)); ) { j++; k++; }
        
        if (j == end)
        {
          return i;
        }
      } 
    } 
    
    return -1;
  }
  
  public static boolean charsEqualIgnoreCase(char a, char b) {
    return (a == b || toUpperCase(a) == toUpperCase(b) || toLowerCase(a) == toLowerCase(b));
  }
  
  private static char toLowerCase(char b) {
    return Character.toLowerCase(b);
  }
  
  private static char toUpperCase(char a) {
    return Character.toUpperCase(a);
  }
  
  public static int compareVersionNumbers(@Nullable String v1, @Nullable String v2) {
    if (v1 == null && v2 == null) {
      return 0;
    }
    if (v1 == null) {
      return -1;
    }
    if (v2 == null) {
      return 1;
    }
    
    String[] part1 = v1.split("[\\.\\_\\-]");
    String[] part2 = v2.split("[\\.\\_\\-]");
    
    int idx = 0;
    for (; idx < part1.length && idx < part2.length; idx++) {
      int cmp; String p1 = part1[idx];
      String p2 = part2[idx];

      
      if (p1.matches("\\d+") && p2.matches("\\d+")) {
        cmp = (new Integer(p1)).compareTo(new Integer(p2));
      } else {
        
        cmp = part1[idx].compareTo(part2[idx]);
      } 
      if (cmp != 0) return cmp;
    
    } 
    if (part1.length == part2.length) {
      return 0;
    }
    if (part1.length > idx) {
      return 1;
    }
    
    return -1;
  }
}
