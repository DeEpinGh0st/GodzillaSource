package org.fife.ui.rsyntaxtextarea.parser;

import java.util.EventListener;
import javax.swing.event.HyperlinkEvent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public interface ExtendedHyperlinkListener extends EventListener {
  void linkClicked(RSyntaxTextArea paramRSyntaxTextArea, HyperlinkEvent paramHyperlinkEvent);
}
