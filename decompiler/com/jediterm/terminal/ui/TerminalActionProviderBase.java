package com.jediterm.terminal.ui;



public abstract class TerminalActionProviderBase
  implements TerminalActionProvider
{
  public TerminalActionProvider getNextProvider() {
    return null;
  }
  
  public void setNextProvider(TerminalActionProvider provider) {}
}
