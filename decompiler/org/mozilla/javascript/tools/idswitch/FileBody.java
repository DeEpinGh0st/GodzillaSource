package org.mozilla.javascript.tools.idswitch;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;





public class FileBody
{
  private static class ReplaceItem
  {
    ReplaceItem next;
    int begin;
    int end;
    String replacement;
    
    ReplaceItem(int begin, int end, String text) {
      this.begin = begin;
      this.end = end;
      this.replacement = text;
    }
  }
  
  private char[] buffer = new char[16384];
  
  private int bufferEnd;
  
  private int lineBegin;
  private int lineEnd;
  private int nextLineStart;
  private int lineNumber;
  ReplaceItem firstReplace;
  ReplaceItem lastReplace;
  
  public char[] getBuffer() {
    return this.buffer;
  }
  public void readData(Reader r) throws IOException {
    int capacity = this.buffer.length;
    int offset = 0;
    while (true) {
      int n_read = r.read(this.buffer, offset, capacity - offset);
      if (n_read < 0)
        break;  offset += n_read;
      if (capacity == offset) {
        capacity *= 2;
        char[] tmp = new char[capacity];
        System.arraycopy(this.buffer, 0, tmp, 0, offset);
        this.buffer = tmp;
      } 
    } 
    this.bufferEnd = offset;
  }
  
  public void writeInitialData(Writer w) throws IOException {
    w.write(this.buffer, 0, this.bufferEnd);
  }
  
  public void writeData(Writer w) throws IOException {
    int offset = 0;
    for (ReplaceItem x = this.firstReplace; x != null; x = x.next) {
      int before_replace = x.begin - offset;
      if (before_replace > 0) {
        w.write(this.buffer, offset, before_replace);
      }
      w.write(x.replacement);
      offset = x.end;
    } 
    int tail = this.bufferEnd - offset;
    if (tail != 0)
      w.write(this.buffer, offset, tail); 
  }
  
  public boolean wasModified() {
    return (this.firstReplace != null);
  }
  public boolean setReplacement(int begin, int end, String text) {
    if (equals(text, this.buffer, begin, end)) return false;
    
    ReplaceItem item = new ReplaceItem(begin, end, text);
    if (this.firstReplace == null) {
      this.firstReplace = this.lastReplace = item;
    }
    else if (begin < this.firstReplace.begin) {
      item.next = this.firstReplace;
      this.firstReplace = item;
    } else {
      
      ReplaceItem cursor = this.firstReplace;
      ReplaceItem next = cursor.next;
      while (next != null) {
        if (begin < next.begin) {
          item.next = next;
          cursor.next = item;
          break;
        } 
        cursor = next;
        next = next.next;
      } 
      if (next == null) {
        this.lastReplace.next = item;
      }
    } 
    
    return true;
  }
  public int getLineNumber() {
    return this.lineNumber;
  } public int getLineBegin() {
    return this.lineBegin;
  } public int getLineEnd() {
    return this.lineEnd;
  }
  public void startLineLoop() {
    this.lineNumber = 0;
    this.lineBegin = this.lineEnd = this.nextLineStart = 0;
  }
  
  public boolean nextLine() {
    if (this.nextLineStart == this.bufferEnd) {
      this.lineNumber = 0; return false;
    } 
    int c = 0; int i;
    for (i = this.nextLineStart; i != this.bufferEnd; i++) {
      c = this.buffer[i];
      if (c == 10 || c == 13)
        break; 
    }  this.lineBegin = this.nextLineStart;
    this.lineEnd = i;
    if (i == this.bufferEnd) {
      this.nextLineStart = i;
    }
    else if (c == 13 && i + 1 != this.bufferEnd && this.buffer[i + 1] == '\n') {
      this.nextLineStart = i + 2;
    } else {
      
      this.nextLineStart = i + 1;
    } 
    this.lineNumber++;
    return true;
  }

  
  private static boolean equals(String str, char[] array, int begin, int end) {
    if (str.length() == end - begin) {
      for (int i = begin, j = 0; i != end; i++, j++) {
        if (array[i] != str.charAt(j)) return false; 
      } 
      return true;
    } 
    return false;
  }
}
