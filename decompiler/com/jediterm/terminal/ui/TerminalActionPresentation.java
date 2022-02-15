package com.jediterm.terminal.ui;

import java.util.Collections;
import java.util.List;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;

public class TerminalActionPresentation
{
  private final String myName;
  private final List<KeyStroke> myKeyStrokes;
  
  public TerminalActionPresentation(@NotNull String name, @NotNull KeyStroke keyStroke) {
    this(name, Collections.singletonList(keyStroke));
  }
  
  public TerminalActionPresentation(@NotNull String name, @NotNull List<KeyStroke> keyStrokes) {
    this.myName = name;
    this.myKeyStrokes = keyStrokes;
  }
  @NotNull
  public String getName() {
    if (this.myName == null) $$$reportNull$$$0(4);  return this.myName;
  }
  @NotNull
  public List<KeyStroke> getKeyStrokes() {
    if (this.myKeyStrokes == null) $$$reportNull$$$0(5);  return this.myKeyStrokes;
  }
}
