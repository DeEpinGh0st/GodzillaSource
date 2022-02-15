package org.bouncycastle.asn1.x509;

public class X509NameTokenizer {
  private String value;
  
  private int index;
  
  private char separator;
  
  private StringBuffer buf = new StringBuffer();
  
  public X509NameTokenizer(String paramString) {
    this(paramString, ',');
  }
  
  public X509NameTokenizer(String paramString, char paramChar) {
    this.value = paramString;
    this.index = -1;
    this.separator = paramChar;
  }
  
  public boolean hasMoreTokens() {
    return (this.index != this.value.length());
  }
  
  public String nextToken() {
    if (this.index == this.value.length())
      return null; 
    int i = this.index + 1;
    boolean bool1 = false;
    boolean bool2 = false;
    this.buf.setLength(0);
    while (i != this.value.length()) {
      char c = this.value.charAt(i);
      if (c == '"') {
        if (!bool2)
          bool1 = !bool1 ? true : false; 
        this.buf.append(c);
        bool2 = false;
      } else if (bool2 || bool1) {
        this.buf.append(c);
        bool2 = false;
      } else if (c == '\\') {
        this.buf.append(c);
        bool2 = true;
      } else {
        if (c == this.separator)
          break; 
        this.buf.append(c);
      } 
      i++;
    } 
    this.index = i;
    return this.buf.toString();
  }
}
