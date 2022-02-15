package com.jediterm.terminal;

import com.jediterm.terminal.emulator.mouse.MouseMode;
import com.jediterm.terminal.model.JediTerminal;
import com.jediterm.terminal.model.TerminalSelection;
import java.awt.Dimension;
import org.jetbrains.annotations.NotNull;

public interface TerminalDisplay {
  int getRowCount();
  
  int getColumnCount();
  
  void setCursor(int paramInt1, int paramInt2);
  
  void setCursorShape(CursorShape paramCursorShape);
  
  void beep();
  
  void requestResize(@NotNull Dimension paramDimension, RequestOrigin paramRequestOrigin, int paramInt1, int paramInt2, JediTerminal.ResizeHandler paramResizeHandler);
  
  void scrollArea(int paramInt1, int paramInt2, int paramInt3);
  
  void setCursorVisible(boolean paramBoolean);
  
  void setScrollingEnabled(boolean paramBoolean);
  
  void setBlinkingCursor(boolean paramBoolean);
  
  void setWindowTitle(String paramString);
  
  void setCurrentPath(String paramString);
  
  void terminalMouseModeSet(MouseMode paramMouseMode);
  
  TerminalSelection getSelection();
  
  boolean ambiguousCharsAreDoubleWidth();
}
