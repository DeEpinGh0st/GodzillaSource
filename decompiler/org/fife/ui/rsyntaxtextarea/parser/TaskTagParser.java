package org.fife.ui.rsyntaxtextarea.parser;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Element;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;




















public class TaskTagParser
  extends AbstractParser
{
  private DefaultParseResult result;
  private static final String DEFAULT_TASK_PATTERN = "TODO|FIXME|HACK";
  private Pattern taskPattern;
  private static final Color COLOR = new Color(48, 150, 252);






  
  public TaskTagParser() {
    this.result = new DefaultParseResult(this);
    setTaskPattern("TODO|FIXME|HACK");
  }









  
  public String getTaskPattern() {
    return (this.taskPattern == null) ? null : this.taskPattern.pattern();
  }



  
  public ParseResult parse(RSyntaxDocument doc, String style) {
    Element root = doc.getDefaultRootElement();
    int lineCount = root.getElementCount();
    
    if (this.taskPattern == null || style == null || "text/plain"
      .equals(style)) {
      this.result.clearNotices();
      this.result.setParsedLines(0, lineCount - 1);
      return this.result;
    } 

    
    this.result.clearNotices();
    this.result.setParsedLines(0, lineCount - 1);
    
    for (int line = 0; line < lineCount; line++) {
      
      Token t = doc.getTokenListForLine(line);
      int offs = -1;
      int start = -1;
      String text = null;
      
      while (t != null && t.isPaintable()) {
        if (t.isComment()) {
          
          offs = t.getOffset();
          text = t.getLexeme();
          
          Matcher m = this.taskPattern.matcher(text);
          if (m.find()) {
            start = m.start();
            offs += start;
            
            break;
          } 
        } 
        t = t.getNextToken();
      } 
      
      if (start > -1 && text != null) {
        text = text.substring(start);
        
        int len = text.length();
        TaskNotice pn = new TaskNotice(this, text, line + 1, offs, len);
        pn.setLevel(ParserNotice.Level.INFO);
        pn.setShowInEditor(false);
        pn.setColor(COLOR);
        this.result.addNotice(pn);
      } 
    } 

    
    return this.result;
  }













  
  public void setTaskPattern(String pattern) {
    if (pattern == null || pattern.length() == 0) {
      this.taskPattern = null;
    } else {
      
      this.taskPattern = Pattern.compile(pattern);
    } 
  }







  
  public static class TaskNotice
    extends DefaultParserNotice
  {
    public TaskNotice(Parser parser, String message, int line, int offs, int length) {
      super(parser, message, line, offs, length);
    }
  }
}
