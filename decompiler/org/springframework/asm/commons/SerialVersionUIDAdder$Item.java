package org.springframework.asm.commons;

class SerialVersionUIDAdder$Item implements Comparable {
  String name;
  
  int access;
  
  String desc;
  
  SerialVersionUIDAdder$Item(String paramString1, int paramInt, String paramString2) {
    this.name = paramString1;
    this.access = paramInt;
    this.desc = paramString2;
  }
  
  public int compareTo(Object paramObject) {
    SerialVersionUIDAdder$Item serialVersionUIDAdder$Item = (SerialVersionUIDAdder$Item)paramObject;
    int i = this.name.compareTo(serialVersionUIDAdder$Item.name);
    if (i == 0)
      i = this.desc.compareTo(serialVersionUIDAdder$Item.desc); 
    return i;
  }
}
