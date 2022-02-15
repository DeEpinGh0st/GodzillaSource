package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.RTextAreaHighlighter;
import org.fife.ui.rtextarea.SmartHighlightPainter;




































public class RSyntaxTextAreaHighlighter
  extends RTextAreaHighlighter
{
  private static final Color DEFAULT_PARSER_NOTICE_COLOR = Color.RED;





  
  private List<SyntaxLayeredHighlightInfoImpl> markedOccurrences = new ArrayList<>();
  private List<SyntaxLayeredHighlightInfoImpl> parserHighlights = new ArrayList<>(0);













  
  Object addMarkedOccurrenceHighlight(int start, int end, SmartHighlightPainter p) throws BadLocationException {
    Document doc = this.textArea.getDocument();
    TextUI mapper = this.textArea.getUI();
    
    SyntaxLayeredHighlightInfoImpl i = new SyntaxLayeredHighlightInfoImpl();
    i.setPainter((Highlighter.HighlightPainter)p);
    i.setStartOffset(doc.createPosition(start));


    
    i.setEndOffset(doc.createPosition(end - 1));
    this.markedOccurrences.add(i);
    mapper.damageRange((JTextComponent)this.textArea, start, end);
    return i;
  }












  
  RTextAreaHighlighter.HighlightInfo addParserHighlight(ParserNotice notice, Highlighter.HighlightPainter p) throws BadLocationException {
    Document doc = this.textArea.getDocument();
    TextUI mapper = this.textArea.getUI();
    
    int start = notice.getOffset();
    int end = 0;
    if (start == -1) {
      int line = notice.getLine();
      Element root = doc.getDefaultRootElement();
      if (line >= 0 && line < root.getElementCount()) {
        Element elem = root.getElement(line);
        start = elem.getStartOffset();
        end = elem.getEndOffset();
      } 
    } else {
      
      end = start + notice.getLength();
    } 

    
    SyntaxLayeredHighlightInfoImpl i = new SyntaxLayeredHighlightInfoImpl();
    i.setPainter(p);
    i.setStartOffset(doc.createPosition(start));


    
    i.setEndOffset(doc.createPosition(end - 1));
    i.notice = notice;
    
    this.parserHighlights.add(i);
    mapper.damageRange((JTextComponent)this.textArea, start, end);
    return (RTextAreaHighlighter.HighlightInfo)i;
  }









  
  void clearMarkOccurrencesHighlights() {
    for (RTextAreaHighlighter.HighlightInfo info : this.markedOccurrences) {
      repaintListHighlight(info);
    }
    this.markedOccurrences.clear();
  }








  
  void clearParserHighlights() {
    for (SyntaxLayeredHighlightInfoImpl parserHighlight : this.parserHighlights) {
      repaintListHighlight((RTextAreaHighlighter.HighlightInfo)parserHighlight);
    }
    this.parserHighlights.clear();
  }







  
  public void clearParserHighlights(Parser parser) {
    Iterator<SyntaxLayeredHighlightInfoImpl> i = this.parserHighlights.iterator();
    while (i.hasNext()) {
      
      SyntaxLayeredHighlightInfoImpl info = i.next();
      
      if (info.notice.getParser() == parser) {
        if (info.width > 0 && info.height > 0) {
          this.textArea.repaint(info.x, info.y, info.width, info.height);
        }
        i.remove();
      } 
    } 
  }




  
  public void deinstall(JTextComponent c) {
    super.deinstall(c);
    this.markedOccurrences.clear();
    this.parserHighlights.clear();
  }








  
  public List<DocumentRange> getMarkedOccurrences() {
    List<DocumentRange> list = new ArrayList<>(this.markedOccurrences.size());
    for (RTextAreaHighlighter.HighlightInfo info : this.markedOccurrences) {
      int start = info.getStartOffset();
      int end = info.getEndOffset() + 1;
      if (start <= end) {



        
        DocumentRange range = new DocumentRange(start, end);
        list.add(range);
      } 
    } 
    return list;
  }















  
  public void paintLayeredHighlights(Graphics g, int lineStart, int lineEnd, Shape viewBounds, JTextComponent editor, View view) {
    paintListLayered(g, lineStart, lineEnd, viewBounds, editor, view, this.markedOccurrences);
    super.paintLayeredHighlights(g, lineStart, lineEnd, viewBounds, editor, view);
  }













  
  public void paintParserHighlights(Graphics g, int lineStart, int lineEnd, Shape viewBounds, JTextComponent editor, View view) {
    paintListLayered(g, lineStart, lineEnd, viewBounds, editor, view, this.parserHighlights);
  }







  
  void removeParserHighlight(RTextAreaHighlighter.HighlightInfo tag) {
    repaintListHighlight(tag);
    this.parserHighlights.remove(tag);
  }


  
  private static class SyntaxLayeredHighlightInfoImpl
    extends RTextAreaHighlighter.LayeredHighlightInfoImpl
  {
    private ParserNotice notice;

    
    private SyntaxLayeredHighlightInfoImpl() {}

    
    public Color getColor() {
      Color color = null;
      if (this.notice != null) {
        color = this.notice.getColor();
        if (color == null) {
          color = RSyntaxTextAreaHighlighter.DEFAULT_PARSER_NOTICE_COLOR;
        }
      } 
      return color;
    }

    
    public String toString() {
      return "[SyntaxLayeredHighlightInfoImpl: startOffs=" + 
        getStartOffset() + ", endOffs=" + 
        getEndOffset() + ", color=" + 
        getColor() + "]";
    }
  }
}
