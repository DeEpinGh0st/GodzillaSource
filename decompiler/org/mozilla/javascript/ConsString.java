package org.mozilla.javascript;

import java.io.Serializable;






















public class ConsString
  implements CharSequence, Serializable
{
  private static final long serialVersionUID = -8432806714471372570L;
  private CharSequence s1;
  private CharSequence s2;
  private final int length;
  private int depth;
  
  public ConsString(CharSequence str1, CharSequence str2) {
    this.s1 = str1;
    this.s2 = str2;
    this.length = str1.length() + str2.length();
    this.depth = 1;
    if (str1 instanceof ConsString) {
      this.depth += ((ConsString)str1).depth;
    }
    if (str2 instanceof ConsString) {
      this.depth += ((ConsString)str2).depth;
    }
    
    if (this.depth > 2000) {
      flatten();
    }
  }

  
  private Object writeReplace() {
    return toString();
  }

  
  public String toString() {
    return (this.depth == 0) ? (String)this.s1 : flatten();
  }
  
  private synchronized String flatten() {
    if (this.depth > 0) {
      StringBuilder b = new StringBuilder(this.length);
      appendTo(b);
      this.s1 = b.toString();
      this.s2 = "";
      this.depth = 0;
    } 
    return (String)this.s1;
  }
  
  private synchronized void appendTo(StringBuilder b) {
    appendFragment(this.s1, b);
    appendFragment(this.s2, b);
  }
  
  private static void appendFragment(CharSequence s, StringBuilder b) {
    if (s instanceof ConsString) {
      ((ConsString)s).appendTo(b);
    } else {
      b.append(s);
    } 
  }
  
  public int length() {
    return this.length;
  }
  
  public char charAt(int index) {
    String str = (this.depth == 0) ? (String)this.s1 : flatten();
    return str.charAt(index);
  }
  
  public CharSequence subSequence(int start, int end) {
    String str = (this.depth == 0) ? (String)this.s1 : flatten();
    return str.substring(start, end);
  }
}
