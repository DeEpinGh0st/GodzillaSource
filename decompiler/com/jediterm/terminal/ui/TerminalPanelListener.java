package com.jediterm.terminal.ui;

import com.jediterm.terminal.RequestOrigin;
import org.jetbrains.annotations.NotNull;

public interface TerminalPanelListener {
  void onPanelResize(@NotNull RequestOrigin paramRequestOrigin);
  
  void onSessionChanged(TerminalSession paramTerminalSession);
  
  void onTitleChanged(String paramString);
}
