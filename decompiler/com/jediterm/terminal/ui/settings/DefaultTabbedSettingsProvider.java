package com.jediterm.terminal.ui.settings;

import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.TerminalActionPresentation;
import com.jediterm.terminal.ui.UIUtil;
import javax.swing.KeyStroke;
import org.jetbrains.annotations.NotNull;





public class DefaultTabbedSettingsProvider
  extends DefaultSettingsProvider
  implements TabbedSettingsProvider
{
  public boolean shouldCloseTabOnLogout(TtyConnector ttyConnector) {
    return true;
  }

  
  public String tabName(TtyConnector ttyConnector, String sessionName) {
    return sessionName;
  }
  
  @NotNull
  public TerminalActionPresentation getPreviousTabActionPresentation() {
    return new TerminalActionPresentation("Previous Tab", UIUtil.isMac ? 
        KeyStroke.getKeyStroke(37, 128) : 
        KeyStroke.getKeyStroke(37, 512));
  }
  
  @NotNull
  public TerminalActionPresentation getNextTabActionPresentation() {
    return new TerminalActionPresentation("Next Tab", UIUtil.isMac ? 
        KeyStroke.getKeyStroke(39, 128) : 
        KeyStroke.getKeyStroke(39, 512));
  }
}
