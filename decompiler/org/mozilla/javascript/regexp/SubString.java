package org.mozilla.javascript.regexp;













public class SubString
{
  public SubString() {}
  
  public SubString(String str) {
    this.str = str;
    this.index = 0;
    this.length = str.length();
  }

  
  public SubString(String source, int start, int len) {
    this.str = source;
    this.index = start;
    this.length = len;
  }

  
  public String toString() {
    return (this.str == null) ? "" : this.str.substring(this.index, this.index + this.length);
  }


  
  public static final SubString emptySubString = new SubString();
  String str;
  int index;
  int length;
}
