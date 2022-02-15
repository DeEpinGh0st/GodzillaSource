package org.fife.ui.rsyntaxtextarea.parser;

import java.util.List;

public interface ParseResult {
  Exception getError();
  
  int getFirstLineParsed();
  
  int getLastLineParsed();
  
  List<ParserNotice> getNotices();
  
  Parser getParser();
  
  long getParseTime();
}
