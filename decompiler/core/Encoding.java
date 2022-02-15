package core;

import core.shell.ShellEntity;
import java.io.UnsupportedEncodingException;
import util.Log;

public class Encoding {
  private String charsetString;
  
  private Encoding(String charsetString) {
    this.charsetString = charsetString;
  }
  private static final String[] ENCODING_TYPES = new String[] { "UTF-8", "GBK", "GB2312", "BIG5", "GB18030", "ISO-8859-1", "latin1", "UTF16", "ascii", "cp850" };
  public static String[] getAllEncodingTypes() {
    return ENCODING_TYPES;
  }
  public byte[] Encoding(String string) {
    try {
      return string.getBytes(this.charsetString);
    } catch (UnsupportedEncodingException e) {
      Log.error(e);
      return string.getBytes();
    } 
  }
  public String Decoding(byte[] srcData) {
    if (srcData == null) {
      return "";
    }
    try {
      return new String(srcData, this.charsetString);
    } catch (UnsupportedEncodingException e) {
      Log.error(e);
      return new String(srcData);
    } 
  }
  public void setCharsetString(String charsetString) {
    this.charsetString = charsetString;
  }
  public String getCharsetString() {
    return this.charsetString;
  }
  public static Encoding getEncoding(ShellEntity entity) {
    return entity.getEncodingModule();
  }
  public static Encoding getEncoding(String charsetString) {
    return new Encoding(charsetString);
  }

  
  public String toString() {
    return this.charsetString;
  }
}
