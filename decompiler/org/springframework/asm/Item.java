package org.springframework.asm;

final class Item {
  int a;
  
  int b;
  
  int c;
  
  long d;
  
  float e;
  
  double f;
  
  String g;
  
  String h;
  
  String i;
  
  int j;
  
  Item k;
  
  Item() {}
  
  Item(int paramInt) {
    this.a = paramInt;
  }
  
  Item(int paramInt, Item paramItem) {
    this.a = paramInt;
    this.b = paramItem.b;
    this.c = paramItem.c;
    this.d = paramItem.d;
    this.e = paramItem.e;
    this.f = paramItem.f;
    this.g = paramItem.g;
    this.h = paramItem.h;
    this.i = paramItem.i;
    this.j = paramItem.j;
  }
  
  void a(int paramInt) {
    this.b = 3;
    this.c = paramInt;
    this.j = Integer.MAX_VALUE & this.b + paramInt;
  }
  
  void a(long paramLong) {
    this.b = 5;
    this.d = paramLong;
    this.j = Integer.MAX_VALUE & this.b + (int)paramLong;
  }
  
  void a(float paramFloat) {
    this.b = 4;
    this.e = paramFloat;
    this.j = Integer.MAX_VALUE & this.b + (int)paramFloat;
  }
  
  void a(double paramDouble) {
    this.b = 6;
    this.f = paramDouble;
    this.j = Integer.MAX_VALUE & this.b + (int)paramDouble;
  }
  
  void a(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.b = paramInt;
    this.g = paramString1;
    this.h = paramString2;
    this.i = paramString3;
    switch (paramInt) {
      case 1:
      case 7:
      case 8:
        this.j = Integer.MAX_VALUE & paramInt + paramString1.hashCode();
        return;
      case 12:
        this.j = Integer.MAX_VALUE & paramInt + paramString1.hashCode() * paramString2.hashCode();
        return;
    } 
    this.j = Integer.MAX_VALUE & paramInt + paramString1.hashCode() * paramString2.hashCode() * paramString3.hashCode();
  }
  
  boolean a(Item paramItem) {
    if (paramItem.b == this.b) {
      switch (this.b) {
        case 3:
          return (paramItem.c == this.c);
        case 5:
          return (paramItem.d == this.d);
        case 4:
          return (paramItem.e == this.e);
        case 6:
          return (paramItem.f == this.f);
        case 1:
        case 7:
        case 8:
          return paramItem.g.equals(this.g);
        case 12:
          return (paramItem.g.equals(this.g) && paramItem.h.equals(this.h));
      } 
      return (paramItem.g.equals(this.g) && paramItem.h.equals(this.h) && paramItem.i.equals(this.i));
    } 
    return false;
  }
}
