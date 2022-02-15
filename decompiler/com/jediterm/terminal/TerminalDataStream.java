package com.jediterm.terminal;

import java.io.IOException;






public interface TerminalDataStream
{
  char getChar() throws IOException;
  
  void pushChar(char paramChar) throws IOException;
  
  String readNonControlCharacters(int paramInt) throws IOException;
  
  void pushBackBuffer(char[] paramArrayOfchar, int paramInt) throws IOException;
  
  boolean isEmpty();
  
  public static class EOF
    extends IOException
  {
    public EOF() {
      super("EOF: There is no more data or connection is lost");
    }
  }
}
