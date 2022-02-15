package org.bouncycastle.est;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

class HttpUtil {
  static String mergeCSL(String paramString, Map<String, String> paramMap) {
    StringWriter stringWriter = new StringWriter();
    stringWriter.write(paramString);
    stringWriter.write(32);
    boolean bool = false;
    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
      if (!bool) {
        bool = true;
      } else {
        stringWriter.write(44);
      } 
      stringWriter.write((String)entry.getKey());
      stringWriter.write("=\"");
      stringWriter.write((String)entry.getValue());
      stringWriter.write(34);
    } 
    return stringWriter.toString();
  }
  
  static Map<String, String> splitCSL(String paramString1, String paramString2) {
    paramString2 = paramString2.trim();
    if (paramString2.startsWith(paramString1))
      paramString2 = paramString2.substring(paramString1.length()); 
    return (new PartLexer(paramString2)).Parse();
  }
  
  public static String[] append(String[] paramArrayOfString, String paramString) {
    if (paramArrayOfString == null)
      return new String[] { paramString }; 
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i + 1];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, i);
    arrayOfString[i] = paramString;
    return arrayOfString;
  }
  
  static class Headers extends HashMap<String, String[]> {
    public String getFirstValue(String param1String) {
      String[] arrayOfString = getValues(param1String);
      return (arrayOfString != null && arrayOfString.length > 0) ? arrayOfString[0] : null;
    }
    
    public String[] getValues(String param1String) {
      param1String = actualKey(param1String);
      return (param1String == null) ? null : get(param1String);
    }
    
    private String actualKey(String param1String) {
      if (containsKey(param1String))
        return param1String; 
      for (String str : keySet()) {
        if (param1String.equalsIgnoreCase(str))
          return str; 
      } 
      return null;
    }
    
    private boolean hasHeader(String param1String) {
      return (actualKey(param1String) != null);
    }
    
    public void set(String param1String1, String param1String2) {
      put(param1String1, new String[] { param1String2 });
    }
    
    public void add(String param1String1, String param1String2) {
      put(param1String1, HttpUtil.append(get(param1String1), param1String2));
    }
    
    public void ensureHeader(String param1String1, String param1String2) {
      if (!containsKey(param1String1))
        set(param1String1, param1String2); 
    }
    
    public Object clone() {
      Headers headers = new Headers();
      for (Map.Entry<String, String> entry : entrySet())
        headers.put((String)entry.getKey(), copy((String[])entry.getValue())); 
      return headers;
    }
    
    private String[] copy(String[] param1ArrayOfString) {
      String[] arrayOfString = new String[param1ArrayOfString.length];
      System.arraycopy(param1ArrayOfString, 0, arrayOfString, 0, arrayOfString.length);
      return arrayOfString;
    }
  }
  
  static class PartLexer {
    private final String src;
    
    int last = 0;
    
    int p = 0;
    
    PartLexer(String param1String) {
      this.src = param1String;
    }
    
    Map<String, String> Parse() {
      HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
      String str1 = null;
      String str2 = null;
      while (this.p < this.src.length()) {
        skipWhiteSpace();
        str1 = consumeAlpha();
        if (str1.length() == 0)
          throw new IllegalArgumentException("Expecting alpha label."); 
        skipWhiteSpace();
        if (!consumeIf('='))
          throw new IllegalArgumentException("Expecting assign: '='"); 
        skipWhiteSpace();
        if (!consumeIf('"'))
          throw new IllegalArgumentException("Expecting start quote: '\"'"); 
        discard();
        str2 = consumeUntil('"');
        discard(1);
        hashMap.put(str1, str2);
        skipWhiteSpace();
        if (!consumeIf(','))
          break; 
        discard();
      } 
      return (Map)hashMap;
    }
    
    private String consumeAlpha() {
      for (char c = this.src.charAt(this.p); this.p < this.src.length() && ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')); c = this.src.charAt(this.p))
        this.p++; 
      String str = this.src.substring(this.last, this.p);
      this.last = this.p;
      return str;
    }
    
    private void skipWhiteSpace() {
      while (this.p < this.src.length() && this.src.charAt(this.p) < '!')
        this.p++; 
      this.last = this.p;
    }
    
    private boolean consumeIf(char param1Char) {
      if (this.p < this.src.length() && this.src.charAt(this.p) == param1Char) {
        this.p++;
        return true;
      } 
      return false;
    }
    
    private String consumeUntil(char param1Char) {
      while (this.p < this.src.length() && this.src.charAt(this.p) != param1Char)
        this.p++; 
      String str = this.src.substring(this.last, this.p);
      this.last = this.p;
      return str;
    }
    
    private void discard() {
      this.last = this.p;
    }
    
    private void discard(int param1Int) {
      this.p += param1Int;
      this.last = this.p;
    }
  }
}
