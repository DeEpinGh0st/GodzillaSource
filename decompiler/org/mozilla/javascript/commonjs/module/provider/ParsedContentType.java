package org.mozilla.javascript.commonjs.module.provider;

import java.io.Serializable;
import java.util.StringTokenizer;
















public final class ParsedContentType
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final String contentType;
  private final String encoding;
  
  public ParsedContentType(String mimeType) {
    String contentType = null;
    String encoding = null;
    if (mimeType != null) {
      StringTokenizer tok = new StringTokenizer(mimeType, ";");
      if (tok.hasMoreTokens()) {
        contentType = tok.nextToken().trim();
        while (tok.hasMoreTokens()) {
          String param = tok.nextToken().trim();
          if (param.startsWith("charset=")) {
            encoding = param.substring(8).trim();
            int l = encoding.length();
            if (l > 0) {
              if (encoding.charAt(0) == '"') {
                encoding = encoding.substring(1);
              }
              if (encoding.charAt(l - 1) == '"') {
                encoding = encoding.substring(0, l - 1);
              }
            } 
            break;
          } 
        } 
      } 
    } 
    this.contentType = contentType;
    this.encoding = encoding;
  }





  
  public String getContentType() {
    return this.contentType;
  }





  
  public String getEncoding() {
    return this.encoding;
  }
}
