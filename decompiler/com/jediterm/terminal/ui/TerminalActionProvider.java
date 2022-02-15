package com.jediterm.terminal.ui;

import java.util.List;

public interface TerminalActionProvider {
  List<TerminalAction> getActions();
  
  TerminalActionProvider getNextProvider();
  
  void setNextProvider(TerminalActionProvider paramTerminalActionProvider);
}
