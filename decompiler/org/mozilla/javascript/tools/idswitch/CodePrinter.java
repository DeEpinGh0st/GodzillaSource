package org.mozilla.javascript.tools.idswitch;








class CodePrinter
{
  private static final int LITERAL_CHAR_MAX_SIZE = 6;
  private String lineTerminator = "\n";
  
  private int indentStep = 4;
  private int indentTabSize = 8;
  
  private char[] buffer = new char[4096];
  private int offset;
  
  public String getLineTerminator() { return this.lineTerminator; } public void setLineTerminator(String value) {
    this.lineTerminator = value;
  }
  public int getIndentStep() { return this.indentStep; } public void setIndentStep(int char_count) {
    this.indentStep = char_count;
  }
  public int getIndentTabSize() { return this.indentTabSize; } public void setIndentTabSize(int tab_size) {
    this.indentTabSize = tab_size;
  }
  public void clear() {
    this.offset = 0;
  }
  
  private int ensure_area(int area_size) {
    int begin = this.offset;
    int end = begin + area_size;
    if (end > this.buffer.length) {
      int new_capacity = this.buffer.length * 2;
      if (end > new_capacity) new_capacity = end; 
      char[] tmp = new char[new_capacity];
      System.arraycopy(this.buffer, 0, tmp, 0, begin);
      this.buffer = tmp;
    } 
    return begin;
  }
  
  private int add_area(int area_size) {
    int pos = ensure_area(area_size);
    this.offset = pos + area_size;
    return pos;
  }
  
  public int getOffset() {
    return this.offset;
  }
  
  public int getLastChar() {
    return (this.offset == 0) ? -1 : this.buffer[this.offset - 1];
  }
  
  public void p(char c) {
    int pos = add_area(1);
    this.buffer[pos] = c;
  }
  
  public void p(String s) {
    int l = s.length();
    int pos = add_area(l);
    s.getChars(0, l, this.buffer, pos);
  }
  
  public final void p(char[] array) {
    p(array, 0, array.length);
  }
  
  public void p(char[] array, int begin, int end) {
    int l = end - begin;
    int pos = add_area(l);
    System.arraycopy(array, begin, this.buffer, pos, l);
  }
  
  public void p(int i) {
    p(Integer.toString(i));
  }
  
  public void qchar(int c) {
    int pos = ensure_area(8);
    this.buffer[pos] = '\'';
    pos = put_string_literal_char(pos + 1, c, false);
    this.buffer[pos] = '\'';
    this.offset = pos + 1;
  }
  
  public void qstring(String s) {
    int l = s.length();
    int pos = ensure_area(2 + 6 * l);
    this.buffer[pos] = '"';
    pos++;
    for (int i = 0; i != l; i++) {
      pos = put_string_literal_char(pos, s.charAt(i), true);
    }
    this.buffer[pos] = '"';
    this.offset = pos + 1;
  }
  
  private int put_string_literal_char(int pos, int c, boolean in_string) {
    boolean backslash_symbol = true;
    switch (c) { case 8:
        c = 98; break;
      case 9: c = 116; break;
      case 10: c = 110; break;
      case 12: c = 102; break;
      case 13: c = 114; break;
      case 39: backslash_symbol = !in_string; break;
      case 34: backslash_symbol = in_string; break;
      default: backslash_symbol = false;
        break; }
    
    if (backslash_symbol) {
      this.buffer[pos] = '\\';
      this.buffer[pos + 1] = (char)c;
      pos += 2;
    }
    else if (32 <= c && c <= 126) {
      this.buffer[pos] = (char)c;
      pos++;
    } else {
      
      this.buffer[pos] = '\\';
      this.buffer[pos + 1] = 'u';
      this.buffer[pos + 2] = digit_to_hex_letter(0xF & c >> 12);
      this.buffer[pos + 3] = digit_to_hex_letter(0xF & c >> 8);
      this.buffer[pos + 4] = digit_to_hex_letter(0xF & c >> 4);
      this.buffer[pos + 5] = digit_to_hex_letter(0xF & c);
      pos += 6;
    } 
    return pos;
  }
  
  private static char digit_to_hex_letter(int d) {
    return (char)((d < 10) ? (48 + d) : (55 + d));
  }
  
  public void indent(int level) {
    int indent_size, tab_count, visible_size = this.indentStep * level;
    
    if (this.indentTabSize <= 0) {
      tab_count = 0; indent_size = visible_size;
    } else {
      
      tab_count = visible_size / this.indentTabSize;
      indent_size = tab_count + visible_size % this.indentTabSize;
    } 
    int pos = add_area(indent_size);
    int tab_end = pos + tab_count;
    int indent_end = pos + indent_size;
    while (pos != tab_end) { this.buffer[pos] = '\t'; pos++; }
     while (pos != indent_end) { this.buffer[pos] = ' '; pos++; }
  
  }
  public void nl() {
    p('\n');
  }
  
  public void line(int indent_level, String s) {
    indent(indent_level); p(s); nl();
  }
  
  public void erase(int begin, int end) {
    System.arraycopy(this.buffer, end, this.buffer, begin, this.offset - end);
    this.offset -= end - begin;
  }

  
  public String toString() {
    return new String(this.buffer, 0, this.offset);
  }
}
