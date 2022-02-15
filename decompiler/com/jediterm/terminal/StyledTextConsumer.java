package com.jediterm.terminal;

import com.jediterm.terminal.model.CharBuffer;
import org.jetbrains.annotations.NotNull;

public interface StyledTextConsumer {
  void consume(int paramInt1, int paramInt2, @NotNull TextStyle paramTextStyle, @NotNull CharBuffer paramCharBuffer, int paramInt3);
  
  void consumeNul(int paramInt1, int paramInt2, int paramInt3, @NotNull TextStyle paramTextStyle, @NotNull CharBuffer paramCharBuffer, int paramInt4);
  
  void consumeQueue(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}
