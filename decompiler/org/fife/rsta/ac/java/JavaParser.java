package org.fife.rsta.ac.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Reader;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.fife.io.DocumentReader;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.notices.ParserNotice;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;












































public class JavaParser
  extends AbstractParser
{
  public static final String PROPERTY_COMPILATION_UNIT = "CompilationUnit";
  private CompilationUnit cu;
  private PropertyChangeSupport support = new PropertyChangeSupport(this);
  private DefaultParseResult result = new DefaultParseResult((Parser)this);


  
  public JavaParser(RSyntaxTextArea textArea) {}


  
  private void addNotices(RSyntaxDocument doc) {
    this.result.clearNotices();
    int count = (this.cu == null) ? 0 : this.cu.getParserNoticeCount();
    
    if (count == 0) {
      return;
    }
    
    for (int i = 0; i < count; i++) {
      ParserNotice notice = this.cu.getParserNotice(i);
      int offs = getOffset(doc, notice);
      if (offs > -1) {
        int len = notice.getLength();
        this.result.addNotice((ParserNotice)new DefaultParserNotice((Parser)this, notice
              .getMessage(), notice.getLine(), offs, len));
      } 
    } 
  }


  
  public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
    this.support.addPropertyChangeListener(prop, l);
  }








  
  public CompilationUnit getCompilationUnit() {
    return this.cu;
  }

  
  public int getOffset(RSyntaxDocument doc, ParserNotice notice) {
    Element root = doc.getDefaultRootElement();
    Element elem = root.getElement(notice.getLine());
    int offs = elem.getStartOffset() + notice.getColumn();
    return (offs >= elem.getEndOffset()) ? -1 : offs;
  }






  
  public ParseResult parse(RSyntaxDocument doc, String style) {
    this.cu = null;
    this.result.clearNotices();
    
    int lineCount = doc.getDefaultRootElement().getElementCount();
    this.result.setParsedLines(0, lineCount - 1);
    
    DocumentReader r = new DocumentReader((Document)doc);
    Scanner scanner = new Scanner((Reader)r);
    scanner.setDocument((Document)doc);
    ASTFactory fact = new ASTFactory();
    long start = System.currentTimeMillis();
    try {
      this.cu = fact.getCompilationUnit("SomeFile.java", scanner);
      long time = System.currentTimeMillis() - start;
      this.result.setParseTime(time);
    } finally {
      r.close();
    } 
    
    addNotices(doc);
    this.support.firePropertyChange("CompilationUnit", (Object)null, this.cu);
    return (ParseResult)this.result;
  }


  
  public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
    this.support.removePropertyChangeListener(prop, l);
  }
}
