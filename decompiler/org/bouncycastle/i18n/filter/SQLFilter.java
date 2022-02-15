package org.bouncycastle.i18n.filter;

public class SQLFilter implements Filter {
  public String doFilter(String paramString) {
    StringBuffer stringBuffer = new StringBuffer(paramString);
    for (byte b = 0; b < stringBuffer.length(); b++) {
      char c = stringBuffer.charAt(b);
      switch (c) {
        case '\'':
          stringBuffer.replace(b, b + 1, "\\'");
          b++;
          break;
        case '"':
          stringBuffer.replace(b, b + 1, "\\\"");
          b++;
          break;
        case '=':
          stringBuffer.replace(b, b + 1, "\\=");
          b++;
          break;
        case '-':
          stringBuffer.replace(b, b + 1, "\\-");
          b++;
          break;
        case '/':
          stringBuffer.replace(b, b + 1, "\\/");
          b++;
          break;
        case '\\':
          stringBuffer.replace(b, b + 1, "\\\\");
          b++;
          break;
        case ';':
          stringBuffer.replace(b, b + 1, "\\;");
          b++;
          break;
        case '\r':
          stringBuffer.replace(b, b + 1, "\\r");
          b++;
          break;
        case '\n':
          stringBuffer.replace(b, b + 1, "\\n");
          b++;
          break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String doFilterUrl(String paramString) {
    return doFilter(paramString);
  }
}
