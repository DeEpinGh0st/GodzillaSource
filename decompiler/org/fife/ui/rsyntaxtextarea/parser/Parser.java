package org.fife.ui.rsyntaxtextarea.parser;

import java.net.URL;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;

public interface Parser {
  ExtendedHyperlinkListener getHyperlinkListener();
  
  URL getImageBase();
  
  boolean isEnabled();
  
  ParseResult parse(RSyntaxDocument paramRSyntaxDocument, String paramString);
}
