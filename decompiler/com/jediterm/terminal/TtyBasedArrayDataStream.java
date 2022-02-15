package com.jediterm.terminal;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;



public class TtyBasedArrayDataStream
  extends ArrayTerminalDataStream
{
  private final TtyConnector myTtyConnector;
  
  public TtyBasedArrayDataStream(TtyConnector ttyConnector) {
    super(new char[1024], 0, 0);
    this.myTtyConnector = ttyConnector;
  }
  
  private void fillBuf() throws IOException {
    this.myOffset = 0;
    this.myLength = this.myTtyConnector.read(this.myBuf, this.myOffset, this.myBuf.length);
    
    if (this.myLength <= 0) {
      this.myLength = 0;
      throw new TerminalDataStream.EOF();
    } 
  }
  
  public char getChar() throws IOException {
    if (this.myLength == 0) {
      fillBuf();
    }
    return super.getChar();
  }
  
  public String readNonControlCharacters(int maxChars) throws IOException {
    if (this.myLength == 0) {
      fillBuf();
    }
    
    return super.readNonControlCharacters(maxChars);
  }

  
  public String toString() {
    return getDebugText();
  }
  @NotNull
  private String getDebugText() {
    String s = new String(this.myBuf, this.myOffset, this.myLength);
    if (s.replace("\033", "ESC").replace("\n", "\\n").replace("\r", "\\r").replace("\007", "BEL").replace(" ", "<S>") == null) $$$reportNull$$$0(0);  return s.replace("\033", "ESC").replace("\n", "\\n").replace("\r", "\\r").replace("\007", "BEL").replace(" ", "<S>");
  }
}
