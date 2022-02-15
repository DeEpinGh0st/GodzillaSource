package com.jediterm.terminal.util;

import org.jetbrains.annotations.NotNull;















public class Pair<A, B>
{
  public final A first;
  public final B second;
  
  @NotNull
  public static <A, B> Pair<A, B> create(A first, B second) {
    return new Pair<>(first, second);
  }
  
  public static <T> T getFirst(Pair<T, ?> pair) {
    return (pair != null) ? (T)pair.first : null;
  }
  
  public static <T> T getSecond(Pair<?, T> pair) {
    return (pair != null) ? (T)pair.second : null;
  }

  
  private static final Pair EMPTY = create(null, null);

  
  public static <A, B> Pair<A, B> empty() {
    return EMPTY;
  }
  
  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }
  
  public final A getFirst() {
    return this.first;
  }
  
  public final B getSecond() {
    return this.second;
  }

  
  public boolean equals(Object o) {
    if (this == o) return true; 
    if (o == null || getClass() != o.getClass()) return false;
    
    Pair pair = (Pair)o;
    
    if ((this.first != null) ? !this.first.equals(pair.first) : (pair.first != null)) return false; 
    if ((this.second != null) ? !this.second.equals(pair.second) : (pair.second != null)) return false;
    
    return true;
  }
  
  public int hashCode() {
    int result = (this.first != null) ? this.first.hashCode() : 0;
    result = 31 * result + ((this.second != null) ? this.second.hashCode() : 0);
    return result;
  }
  
  public String toString() {
    return "<" + this.first + "," + this.second + ">";
  }
}
