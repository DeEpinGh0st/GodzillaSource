package org.bouncycastle.asn1.eac;

import java.util.Enumeration;
import java.util.Hashtable;

public class Flags {
  int value = 0;
  
  public Flags() {}
  
  public Flags(int paramInt) {
    this.value = paramInt;
  }
  
  public void set(int paramInt) {
    this.value |= paramInt;
  }
  
  public boolean isSet(int paramInt) {
    return ((this.value & paramInt) != 0);
  }
  
  public int getFlags() {
    return this.value;
  }
  
  String decode(Hashtable paramHashtable) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    Enumeration<Integer> enumeration = paramHashtable.keys();
    while (enumeration.hasMoreElements()) {
      Integer integer = enumeration.nextElement();
      if (isSet(integer.intValue()))
        stringJoiner.add((String)paramHashtable.get(integer)); 
    } 
    return stringJoiner.toString();
  }
  
  private class StringJoiner {
    String mSeparator;
    
    boolean First = true;
    
    StringBuffer b = new StringBuffer();
    
    public StringJoiner(String param1String) {
      this.mSeparator = param1String;
    }
    
    public void add(String param1String) {
      if (this.First) {
        this.First = false;
      } else {
        this.b.append(this.mSeparator);
      } 
      this.b.append(param1String);
    }
    
    public String toString() {
      return this.b.toString();
    }
  }
}
