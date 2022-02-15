package org.bouncycastle.i18n.filter;

public class HTMLFilter implements Filter {
  public String doFilter(String paramString) {
    StringBuffer stringBuffer = new StringBuffer(paramString);
    for (byte b = 0; b < stringBuffer.length(); b += 4) {
      char c = stringBuffer.charAt(b);
      switch (c) {
        case '<':
          stringBuffer.replace(b, b + 1, "&#60");
          break;
        case '>':
          stringBuffer.replace(b, b + 1, "&#62");
          break;
        case '(':
          stringBuffer.replace(b, b + 1, "&#40");
          break;
        case ')':
          stringBuffer.replace(b, b + 1, "&#41");
          break;
        case '#':
          stringBuffer.replace(b, b + 1, "&#35");
          break;
        case '&':
          stringBuffer.replace(b, b + 1, "&#38");
          break;
        case '"':
          stringBuffer.replace(b, b + 1, "&#34");
          break;
        case '\'':
          stringBuffer.replace(b, b + 1, "&#39");
          break;
        case '%':
          stringBuffer.replace(b, b + 1, "&#37");
          break;
        case ';':
          stringBuffer.replace(b, b + 1, "&#59");
          break;
        case '+':
          stringBuffer.replace(b, b + 1, "&#43");
          break;
        case '-':
          stringBuffer.replace(b, b + 1, "&#45");
          break;
        default:
          b -= 3;
          break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String doFilterUrl(String paramString) {
    return doFilter(paramString);
  }
}
