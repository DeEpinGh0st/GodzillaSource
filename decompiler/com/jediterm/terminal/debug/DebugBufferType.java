package com.jediterm.terminal.debug;

import com.jediterm.terminal.LoggingTtyConnector;
import com.jediterm.terminal.ui.TerminalSession;



public enum DebugBufferType
{
  Back {
    public String getValue(TerminalSession session) {
      return session.getTerminalTextBuffer().getScreenLines();
    }
  },
  BackStyle {
    public String getValue(TerminalSession session) {
      return session.getTerminalTextBuffer().getStyleLines();
    }
  },
  Scroll {
    public String getValue(TerminalSession session) {
      return session.getTerminalTextBuffer().getHistoryBuffer().getLines();
    }
  },
  
  ControlSequences {
    private ControlSequenceVisualizer myVisualizer = new ControlSequenceVisualizer();
    
    public String getValue(TerminalSession session) {
      if (session.getTtyConnector() instanceof LoggingTtyConnector) {
        return this.myVisualizer.getVisualizedString(((LoggingTtyConnector)session.getTtyConnector()).getChunks());
      }
      return "Control sequences aren't logged";
    }
  };
  
  public abstract String getValue(TerminalSession paramTerminalSession);
}
