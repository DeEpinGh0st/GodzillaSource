package com.jediterm.terminal;

public interface TerminalOutputStream {
  void sendBytes(byte[] paramArrayOfbyte);
  
  void sendString(String paramString);
}
