package org.fife.ui.rsyntaxtextarea;

import org.fife.ui.rtextarea.SmartHighlightPainter;

public interface OccurrenceMarker {
  Token getTokenToMark(RSyntaxTextArea paramRSyntaxTextArea);
  
  boolean isValidType(RSyntaxTextArea paramRSyntaxTextArea, Token paramToken);
  
  void markOccurrences(RSyntaxDocument paramRSyntaxDocument, Token paramToken, RSyntaxTextAreaHighlighter paramRSyntaxTextAreaHighlighter, SmartHighlightPainter paramSmartHighlightPainter);
}
