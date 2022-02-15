package org.springframework.expression.spel.ast;


























public enum TypeCode
{
  OBJECT(Object.class),



  
  BOOLEAN(boolean.class),



  
  BYTE(byte.class),



  
  CHAR(char.class),



  
  DOUBLE(double.class),



  
  FLOAT(float.class),



  
  INT(int.class),



  
  LONG(long.class),



  
  SHORT(short.class);

  
  private Class<?> type;

  
  TypeCode(Class<?> type) {
    this.type = type;
  }

  
  public Class<?> getType() {
    return this.type;
  }

  
  public static TypeCode forName(String name) {
    TypeCode[] tcs = values();
    for (int i = 1; i < tcs.length; i++) {
      if (tcs[i].name().equalsIgnoreCase(name)) {
        return tcs[i];
      }
    } 
    return OBJECT;
  }
  
  public static TypeCode forClass(Class<?> clazz) {
    TypeCode[] allValues = values();
    for (TypeCode typeCode : allValues) {
      if (clazz == typeCode.getType()) {
        return typeCode;
      }
    } 
    return OBJECT;
  }
}
