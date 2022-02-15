package org.fife.ui.rsyntaxtextarea.parser;

import java.util.ArrayList;
import java.util.List;


















public class DefaultParseResult
  implements ParseResult
{
  private Parser parser;
  private int firstLineParsed;
  private int lastLineParsed;
  private List<ParserNotice> notices;
  private long parseTime;
  private Exception error;
  
  public DefaultParseResult(Parser parser) {
    this.parser = parser;
    this.notices = new ArrayList<>();
  }







  
  public void addNotice(ParserNotice notice) {
    this.notices.add(notice);
  }






  
  public void clearNotices() {
    this.notices.clear();
  }


  
  public Exception getError() {
    return this.error;
  }


  
  public int getFirstLineParsed() {
    return this.firstLineParsed;
  }


  
  public int getLastLineParsed() {
    return this.lastLineParsed;
  }


  
  public List<ParserNotice> getNotices() {
    return this.notices;
  }


  
  public Parser getParser() {
    return this.parser;
  }


  
  public long getParseTime() {
    return this.parseTime;
  }








  
  public void setError(Exception e) {
    this.error = e;
  }









  
  public void setParsedLines(int first, int last) {
    this.firstLineParsed = first;
    this.lastLineParsed = last;
  }







  
  public void setParseTime(long time) {
    this.parseTime = time;
  }
}
