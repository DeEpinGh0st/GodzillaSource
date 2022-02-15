package com.jediterm.terminal;

import com.jediterm.terminal.emulator.mouse.MouseFormat;
import com.jediterm.terminal.emulator.mouse.MouseMode;
import com.jediterm.terminal.model.StyleState;
import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Terminal {
  void resize(@NotNull Dimension paramDimension, @NotNull RequestOrigin paramRequestOrigin);
  
  void resize(@NotNull Dimension paramDimension, @NotNull RequestOrigin paramRequestOrigin, @NotNull CompletableFuture<?> paramCompletableFuture);
  
  void beep();
  
  void backspace();
  
  void horizontalTab();
  
  void carriageReturn();
  
  void newLine();
  
  void mapCharsetToGL(int paramInt);
  
  void mapCharsetToGR(int paramInt);
  
  void designateCharacterSet(int paramInt, char paramChar);
  
  void setAnsiConformanceLevel(int paramInt);
  
  void writeDoubleByte(char[] paramArrayOfchar) throws UnsupportedEncodingException;
  
  void writeCharacters(String paramString);
  
  int distanceToLineEnd();
  
  void reverseIndex();
  
  void index();
  
  void nextLine();
  
  void fillScreen(char paramChar);
  
  void saveCursor();
  
  void restoreCursor();
  
  void reset();
  
  void characterAttributes(TextStyle paramTextStyle);
  
  void setScrollingRegion(int paramInt1, int paramInt2);
  
  void scrollUp(int paramInt);
  
  void scrollDown(int paramInt);
  
  void resetScrollRegions();
  
  void cursorHorizontalAbsolute(int paramInt);
  
  void linePositionAbsolute(int paramInt);
  
  void cursorPosition(int paramInt1, int paramInt2);
  
  void cursorUp(int paramInt);
  
  void cursorDown(int paramInt);
  
  void cursorForward(int paramInt);
  
  void cursorBackward(int paramInt);
  
  void cursorShape(CursorShape paramCursorShape);
  
  void eraseInLine(int paramInt);
  
  void deleteCharacters(int paramInt);
  
  int getTerminalWidth();
  
  int getTerminalHeight();
  
  void eraseInDisplay(int paramInt);
  
  void setModeEnabled(TerminalMode paramTerminalMode, boolean paramBoolean);
  
  void disconnected();
  
  int getCursorX();
  
  int getCursorY();
  
  void singleShiftSelect(int paramInt);
  
  void setWindowTitle(String paramString);
  
  void setCurrentPath(String paramString);
  
  void clearScreen();
  
  void setCursorVisible(boolean paramBoolean);
  
  void useAlternateBuffer(boolean paramBoolean);
  
  byte[] getCodeForKey(int paramInt1, int paramInt2);
  
  void setApplicationArrowKeys(boolean paramBoolean);
  
  void setApplicationKeypad(boolean paramBoolean);
  
  void setAutoNewLine(boolean paramBoolean);
  
  StyleState getStyleState();
  
  void insertLines(int paramInt);
  
  void deleteLines(int paramInt);
  
  void setBlinkingCursor(boolean paramBoolean);
  
  void eraseCharacters(int paramInt);
  
  void insertBlankCharacters(int paramInt);
  
  void clearTabStopAtCursor();
  
  void clearAllTabStops();
  
  void setTabStopAtCursor();
  
  void writeUnwrappedString(String paramString);
  
  void setTerminalOutput(@Nullable TerminalOutputStream paramTerminalOutputStream);
  
  void setMouseMode(@NotNull MouseMode paramMouseMode);
  
  void setMouseFormat(MouseFormat paramMouseFormat);
  
  void setAltSendsEscape(boolean paramBoolean);
  
  void deviceStatusReport(String paramString);
  
  void deviceAttributes(byte[] paramArrayOfbyte);
  
  void setLinkUriStarted(@NotNull String paramString);
  
  void setLinkUriFinished();
}
