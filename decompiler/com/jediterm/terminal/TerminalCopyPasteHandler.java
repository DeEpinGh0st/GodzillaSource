package com.jediterm.terminal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TerminalCopyPasteHandler {
  void setContents(@NotNull String paramString, boolean paramBoolean);
  
  @Nullable
  String getContents(boolean paramBoolean);
}
