package org.springframework.asm.commons;

import java.util.HashMap;
import java.util.Map;
import org.springframework.asm.Type;

public class Method {
  private final String name;
  
  private final String desc;
  
  private static final Map DESCRIPTORS = new HashMap();
  
  public Method(String paramString1, String paramString2) {
    this.name = paramString1;
    this.desc = paramString2;
  }
  
  public Method(String paramString, Type paramType, Type[] paramArrayOfType) {
    this(paramString, Type.getMethodDescriptor(paramType, paramArrayOfType));
  }
  
  public static Method getMethod(String paramString) throws IllegalArgumentException {
    int i = paramString.indexOf(' ');
    int j = paramString.indexOf('(', i) + 1;
    int k = paramString.indexOf(')', j);
    if (i == -1 || j == -1 || k == -1)
      throw new IllegalArgumentException(); 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1, j - 1).trim();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    while (true) {
      int m = paramString.indexOf(',', j);
      if (m == -1) {
        stringBuffer.append(map(paramString.substring(j, k).trim()));
      } else {
        stringBuffer.append(map(paramString.substring(j, m).trim()));
        j = m + 1;
      } 
      if (m == -1) {
        stringBuffer.append(')');
        stringBuffer.append(map(str1));
        return new Method(str2, stringBuffer.toString());
      } 
    } 
  }
  
  private static String map(String paramString) {
    if (paramString.equals(""))
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    while ((i = paramString.indexOf("[]", i) + 1) > 0)
      stringBuffer.append('['); 
    String str1 = paramString.substring(0, paramString.length() - stringBuffer.length() * 2);
    String str2 = (String)DESCRIPTORS.get(str1);
    if (str2 != null) {
      stringBuffer.append(str2);
    } else {
      stringBuffer.append('L');
      if (str1.indexOf('.') < 0) {
        stringBuffer.append("java/lang/" + str1);
      } else {
        stringBuffer.append(str1.replace('.', '/'));
      } 
      stringBuffer.append(';');
    } 
    return stringBuffer.toString();
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDescriptor() {
    return this.desc;
  }
  
  public Type getReturnType() {
    return Type.getReturnType(this.desc);
  }
  
  public Type[] getArgumentTypes() {
    return Type.getArgumentTypes(this.desc);
  }
  
  public String toString() {
    return this.name + this.desc;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Method))
      return false; 
    Method method = (Method)paramObject;
    return (this.name.equals(method.name) && this.desc.equals(method.desc));
  }
  
  public int hashCode() {
    return this.name.hashCode() ^ this.desc.hashCode();
  }
  
  static {
    DESCRIPTORS.put("void", "V");
    DESCRIPTORS.put("byte", "B");
    DESCRIPTORS.put("char", "C");
    DESCRIPTORS.put("double", "D");
    DESCRIPTORS.put("float", "F");
    DESCRIPTORS.put("int", "I");
    DESCRIPTORS.put("long", "J");
    DESCRIPTORS.put("short", "S");
    DESCRIPTORS.put("boolean", "Z");
  }
}
