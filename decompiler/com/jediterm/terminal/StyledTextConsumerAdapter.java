package com.jediterm.terminal;

import com.jediterm.terminal.model.CharBuffer;

public class StyledTextConsumerAdapter implements StyledTextConsumer {
  public void consume(int x, int y, TextStyle style, CharBuffer characters, int startRow) {}
  
  public void consumeNul(int x, int y, int nulIndex, TextStyle style, CharBuffer characters, int startRow) {}
  
  public void consumeQueue(int x, int y, int nulIndex, int startRow) {}
}
