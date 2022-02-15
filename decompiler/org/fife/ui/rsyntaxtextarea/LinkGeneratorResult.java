package org.fife.ui.rsyntaxtextarea;

import javax.swing.event.HyperlinkEvent;

public interface LinkGeneratorResult {
  HyperlinkEvent execute();
  
  int getSourceOffset();
}
