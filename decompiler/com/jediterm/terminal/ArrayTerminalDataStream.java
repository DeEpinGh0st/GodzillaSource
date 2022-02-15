package com.jediterm.terminal;

import com.jediterm.terminal.util.CharUtils;
import java.io.IOException;





public class ArrayTerminalDataStream
  implements TerminalDataStream
{
  protected char[] myBuf;
  protected int myOffset;
  protected int myLength;
  
  public ArrayTerminalDataStream(char[] buf, int offset, int length) {
    this.myBuf = buf;
    this.myOffset = offset;
    this.myLength = length;
  }
  
  public ArrayTerminalDataStream(char[] buf) {
    this(buf, 0, buf.length);
  }
  
  public char getChar() throws IOException {
    if (this.myLength == 0) {
      throw new TerminalDataStream.EOF();
    }
    
    this.myLength--;
    
    return this.myBuf[this.myOffset++];
  }
  
  public void pushChar(char c) throws TerminalDataStream.EOF {
    if (this.myOffset == 0) {
      char[] newBuf;

      
      if (this.myBuf.length - this.myLength == 0) {
        newBuf = new char[this.myBuf.length + 1];
      } else {
        
        newBuf = this.myBuf;
      } 
      this.myOffset = newBuf.length - this.myLength;
      System.arraycopy(this.myBuf, 0, newBuf, this.myOffset, this.myLength);
      this.myBuf = newBuf;
    } 
    
    this.myLength++;
    this.myBuf[--this.myOffset] = c;
  }
  
  public String readNonControlCharacters(int maxChars) throws IOException {
    String nonControlCharacters = CharUtils.getNonControlCharacters(maxChars, this.myBuf, this.myOffset, this.myLength);
    
    this.myOffset += nonControlCharacters.length();
    this.myLength -= nonControlCharacters.length();
    
    return nonControlCharacters;
  }
  
  public void pushBackBuffer(char[] bytes, int length) throws TerminalDataStream.EOF {
    for (int i = length - 1; i >= 0; i--) {
      pushChar(bytes[i]);
    }
  }

  
  public boolean isEmpty() {
    return (this.myLength == 0);
  }
}
