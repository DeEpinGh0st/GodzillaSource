package org.fife.ui.autocomplete;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;




































public class DefaultCompletionProvider
  extends AbstractCompletionProvider
{
  protected Segment seg;
  private String lastCompletionsAtText;
  private List<Completion> lastParameterizedCompletionsAt;
  
  public DefaultCompletionProvider() {
    init();
  }









  
  public DefaultCompletionProvider(String[] words) {
    init();
    addWordCompletions(words);
  }












  
  public String getAlreadyEnteredText(JTextComponent comp) {
    Document doc = comp.getDocument();
    
    int dot = comp.getCaretPosition();
    Element root = doc.getDefaultRootElement();
    int index = root.getElementIndex(dot);
    Element elem = root.getElement(index);
    int start = elem.getStartOffset();
    int len = dot - start;
    try {
      doc.getText(start, len, this.seg);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      return "";
    } 
    
    int segEnd = this.seg.offset + len;
    start = segEnd - 1;
    while (start >= this.seg.offset && isValidChar(this.seg.array[start])) {
      start--;
    }
    start++;
    
    len = segEnd - start;
    return (len == 0) ? "" : new String(this.seg.array, start, len);
  }







  
  public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
    int offset = tc.viewToModel(p);
    if (offset < 0 || offset >= tc.getDocument().getLength()) {
      this.lastCompletionsAtText = null;
      return this.lastParameterizedCompletionsAt = null;
    } 
    
    Segment s = new Segment();
    Document doc = tc.getDocument();
    Element root = doc.getDefaultRootElement();
    int line = root.getElementIndex(offset);
    Element elem = root.getElement(line);
    int start = elem.getStartOffset();
    int end = elem.getEndOffset() - 1;

    
    try {
      doc.getText(start, end - start, s);

      
      int startOffs = s.offset + offset - start - 1;
      while (startOffs >= s.offset && isValidChar(s.array[startOffs])) {
        startOffs--;
      }

      
      int endOffs = s.offset + offset - start;
      while (endOffs < s.offset + s.count && isValidChar(s.array[endOffs])) {
        endOffs++;
      }
      
      int len = endOffs - startOffs - 1;
      if (len <= 0) {
        return this.lastParameterizedCompletionsAt = null;
      }
      String text = new String(s.array, startOffs + 1, len);
      
      if (text.equals(this.lastCompletionsAtText)) {
        return this.lastParameterizedCompletionsAt;
      }

      
      List<Completion> list = getCompletionByInputText(text);
      this.lastCompletionsAtText = text;
      return this.lastParameterizedCompletionsAt = list;
    }
    catch (BadLocationException ble) {
      ble.printStackTrace();

      
      this.lastCompletionsAtText = null;
      return this.lastParameterizedCompletionsAt = null;
    } 
  }







  
  public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
    List<ParameterizedCompletion> list = null;


    
    char paramListStart = getParameterListStart();
    if (paramListStart == '\000') {
      return list;
    }
    
    int dot = tc.getCaretPosition();
    Segment s = new Segment();
    Document doc = tc.getDocument();
    Element root = doc.getDefaultRootElement();
    int line = root.getElementIndex(dot);
    Element elem = root.getElement(line);
    int offs = elem.getStartOffset();
    int len = dot - offs - 1;
    if (len <= 0) {
      return list;
    }

    
    try {
      doc.getText(offs, len, s);


      
      offs = s.offset + len - 1;
      while (offs >= s.offset && Character.isWhitespace(s.array[offs])) {
        offs--;
      }
      int end = offs;
      while (offs >= s.offset && isValidChar(s.array[offs])) {
        offs--;
      }
      
      String text = new String(s.array, offs + 1, end - offs);


      
      List<Completion> l = getCompletionByInputText(text);
      if (l != null && !l.isEmpty()) {
        for (Completion o : l) {
          if (o instanceof ParameterizedCompletion) {
            if (list == null) {
              list = new ArrayList<>(1);
            }
            list.add((ParameterizedCompletion)o);
          }
        
        } 
      }
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    
    return list;
  }





  
  protected void init() {
    this.seg = new Segment();
  }










  
  protected boolean isValidChar(char ch) {
    return (Character.isLetterOrDigit(ch) || ch == '_');
  }








  
  public void loadFromXML(File file) throws IOException {
    try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file))) {
      
      loadFromXML(bin);
    } 
  }








  
  public void loadFromXML(InputStream in) throws IOException {
    loadFromXML(in, (ClassLoader)null);
  }















  
  public void loadFromXML(InputStream in, ClassLoader cl) throws IOException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    CompletionXMLParser handler = new CompletionXMLParser(this, cl);
    try (BufferedInputStream bin = new BufferedInputStream(in)) {
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(bin, handler);
      List<Completion> completions = handler.getCompletions();
      addCompletions(completions);
      char startChar = handler.getParamStartChar();
      if (startChar != '\000') {
        char endChar = handler.getParamEndChar();
        String sep = handler.getParamSeparator();
        if (endChar != '\000' && sep != null && sep.length() > 0) {
          setParameterizedCompletionParams(startChar, sep, endChar);
        }
      } 
    } catch (SAXException|javax.xml.parsers.ParserConfigurationException e) {
      throw new IOException(e.toString());
    } 
  }











  
  public void loadFromXML(String resource) throws IOException {
    ClassLoader cl = getClass().getClassLoader();
    InputStream in = cl.getResourceAsStream(resource);
    if (in == null) {
      File file = new File(resource);
      if (file.isFile()) {
        in = new FileInputStream(file);
      } else {
        
        throw new IOException("No such resource: " + resource);
      } 
    } 
    try (BufferedInputStream bin = new BufferedInputStream(in)) {
      loadFromXML(bin);
    } 
  }
}
