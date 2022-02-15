package com.jediterm.terminal;

import org.apache.log4j.Logger;

public enum TerminalMode {
  Null,
  CursorKey
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setApplicationArrowKeys(enabled);
    }
  },
  ANSI,
  WideColumn
  {
    
    public void setEnabled(Terminal terminal, boolean enabled)
    {
      terminal.clearScreen();
      terminal.resetScrollRegions();
    }
  },
  CursorVisible
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setCursorVisible(enabled);
    }
  },
  AlternateBuffer
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.useAlternateBuffer(enabled);
    }
  },
  SmoothScroll,
  ReverseVideo,
  OriginMode
  {
    
    public void setEnabled(Terminal terminal, boolean enabled) {}
  },
  AutoWrap
  {

    
    public void setEnabled(Terminal terminal, boolean enabled) {}
  },
  AutoRepeatKeys,
  Interlace,
  Keypad
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setApplicationKeypad(enabled);
    }
  },
  StoreCursor
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      if (enabled) {
        terminal.saveCursor();
      } else {
        
        terminal.restoreCursor();
      } 
    }
  },
  CursorBlinking
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setBlinkingCursor(enabled);
    }
  },
  AllowWideColumn,
  ReverseWrapAround,
  AutoNewLine
  {
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setAutoNewLine(enabled);
    }
  },
  KeyboardAction,
  InsertMode,
  SendReceive,
  EightBitInput,

  
  AltSendsEscape
  {
    public void setEnabled(Terminal terminal, boolean enabled)
    {
      terminal.setAltSendsEscape(enabled);
    }
  };
  
  static {
    LOG = Logger.getLogger(TerminalMode.class);
  } private static final Logger LOG;
  public void setEnabled(Terminal terminal, boolean enabled) {
    LOG.error("Mode " + name() + " is not implemented, setting to " + enabled);
  }
}
