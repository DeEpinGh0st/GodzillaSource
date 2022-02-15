package com.jediterm.terminal.ui;

import com.jediterm.terminal.TerminalDisplay;
import com.jediterm.terminal.TtyConnector;
import java.awt.Dimension;
import javax.swing.JComponent;




public interface TerminalWidget
{
  JediTermWidget createTerminalSession(TtyConnector paramTtyConnector);
  
  JComponent getComponent();
  
  default JComponent getPreferredFocusableComponent() {
    return getComponent();
  }
  
  boolean canOpenSession();
  
  void setTerminalPanelListener(TerminalPanelListener paramTerminalPanelListener);
  
  Dimension getPreferredSize();
  
  TerminalSession getCurrentSession();
  
  TerminalDisplay getTerminalDisplay();
  
  void addListener(TerminalWidgetListener paramTerminalWidgetListener);
  
  void removeListener(TerminalWidgetListener paramTerminalWidgetListener);
}
