package org.fife.rsta.ac.perl;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Element;
import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;


















class PerlOutputCollector
  extends OutputCollector
{
  private PerlParser parser;
  private DefaultParseResult result;
  private Element root;
  private static final Pattern ERROR_PATTERN = Pattern.compile(" at .+ line (\\d+)\\.$");







  
  public PerlOutputCollector(InputStream in, PerlParser parser, DefaultParseResult result, Element root) {
    super(in);
    this.parser = parser;
    this.result = result;
    this.root = root;
  }






  
  protected void handleLineRead(String line) {
    Matcher m = ERROR_PATTERN.matcher(line);
    
    if (m.find()) {
      
      line = line.substring(0, line.length() - m.group().length());
      
      int lineNumber = Integer.parseInt(m.group(1)) - 1;
      Element elem = this.root.getElement(lineNumber);
      int start = elem.getStartOffset();
      int end = elem.getEndOffset();
      
      DefaultParserNotice pn = new DefaultParserNotice((Parser)this.parser, line, lineNumber, start, end - start);

      
      this.result.addNotice((ParserNotice)pn);
    } 
  }
}
