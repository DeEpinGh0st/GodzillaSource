package org.fife.rsta.ac.php;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionXMLParser;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;






































public class PhpCompletionProvider
  extends HtmlCompletionProvider
{
  private boolean phpCompletion;
  private List<Completion> phpCompletions;
  
  public PhpCompletionProvider() {
    ClassLoader cl = getClass().getClassLoader();
    InputStream in = cl.getResourceAsStream("data/php.xml");
    try {
      if (in == null) {
        in = new FileInputStream("data/php.xml");
      }
      loadPhpCompletionsFromXML(in);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 
  }










  
  public void loadPhpCompletionsFromXML(InputStream in) throws IOException {
    long start = System.currentTimeMillis();
    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    CompletionXMLParser handler = new CompletionXMLParser((CompletionProvider)this);
    BufferedInputStream bin = new BufferedInputStream(in);
    try {
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(bin, (DefaultHandler)handler);
      this.phpCompletions = handler.getCompletions();
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
    } finally {
      long time = System.currentTimeMillis() - start;
      System.out.println("XML loaded in: " + time + "ms");
      bin.close();
    } 
  }







  
  public String getAlreadyEnteredText(JTextComponent comp) {
    this.phpCompletion = false;
    
    String text = super.getAlreadyEnteredText(comp);
    if (text == null && 
      inPhpBlock(comp)) {
      text = defaultGetAlreadyEnteredText(comp);
      this.phpCompletion = true;
    } 

    
    return text;
  }







  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    List<Completion> list;
    String text = getAlreadyEnteredText(comp);
    
    if (this.phpCompletion) {
      
      if (text == null) {
        list = new ArrayList<>(0);
      
      }
      else {
        
        list = new ArrayList<>();

        
        int index = Collections.binarySearch(this.phpCompletions, text, (Comparator<?>)this.comparator);
        if (index < 0) {
          index = -index - 1;
        }
        
        while (index < this.phpCompletions.size()) {
          Completion c = this.phpCompletions.get(index);
          if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
            list.add(c);
            index++;
          
          }
        
        }
      
      }
    
    }
    else {
      
      list = super.getCompletionsImpl(comp);
    } 
    
    return list;
  }









  
  private boolean inPhpBlock(JTextComponent comp) {
    int line;
    RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
    int dot = comp.getCaretPosition();
    RSyntaxDocument doc = (RSyntaxDocument)comp.getDocument();
    
    try {
      line = textArea.getLineOfOffset(dot);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      return false;
    } 
    Token token = doc.getTokenListForLine(line);
    
    boolean inPhp = false;


    
    while (token != null && token.isPaintable() && token.getOffset() <= dot) {
      if (token.getType() == 22 && token.length() >= 2) {
        char ch1 = token.charAt(0);
        char ch2 = token.charAt(1);
        if (ch1 == '<' && ch2 == '?') {
          inPhp = true;
        }
        else if (ch1 == '?' && ch2 == '>') {
          inPhp = false;
        } 
      } 
      token = token.getNextToken();
    } 



    
    if (!inPhp && line > 0) {
      int prevLineEndType = doc.getLastTokenTypeOnLine(line - 1);
      if (prevLineEndType <= -8192) {
        inPhp = true;
      }
    } 
    
    return inPhp;
  }






  
  public boolean isAutoActivateOkay(JTextComponent tc) {
    return (inPhpBlock(tc) || super.isAutoActivateOkay(tc));
  }
}
