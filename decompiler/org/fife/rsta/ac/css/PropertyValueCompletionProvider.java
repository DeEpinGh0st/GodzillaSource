package org.fife.rsta.ac.css;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.CompletionXMLParser;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


















public class PropertyValueCompletionProvider
  extends CompletionProviderBase
{
  private List<Completion> htmlTagCompletions;
  private List<Completion> propertyCompletions;
  private Map<String, List<Completion>> valueCompletions;
  private Map<String, List<CompletionGenerator>> valueCompletionGenerators;
  private Segment seg = new Segment();


  
  private AbstractCompletionProvider.CaseInsensitiveComparator comparator;


  
  private String currentProperty;


  
  private boolean isLess;


  
  private static final Pattern VENDOR_PREFIXES = Pattern.compile("^\\-(?:ms|moz|o|xv|webkit|khtml|apple)\\-");
  
  private final Completion INHERIT_COMPLETION = (Completion)new BasicCssCompletion((CompletionProvider)this, "inherit", "css_propertyvalue_identifier");



  
  public PropertyValueCompletionProvider(boolean isLess) {
    setAutoActivationRules(true, "@: ");
    
    setParameterizedCompletionParams('(', ", ", ')');
    this.isLess = isLess;
    
    try {
      this.valueCompletions = new HashMap<>();
      this.valueCompletionGenerators = new HashMap<>();
      
      loadPropertyCompletions();
      this.htmlTagCompletions = loadHtmlTagCompletions();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    } 
    
    this.comparator = new AbstractCompletionProvider.CaseInsensitiveComparator();
  }


  
  private void addAtRuleCompletions(List<Completion> completions) {
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@charset", "charset_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@import", "link_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@namespace", "charset_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@media", "media_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@page", "page_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@font-face", "fontface_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@keyframes", "charset_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@supports", "charset_rule"));
    completions.add(new BasicCssCompletion((CompletionProvider)this, "@document", "charset_rule"));
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
    if (len == 0) {
      return "";
    }
    
    String text = new String(this.seg.array, start, len);
    return removeVendorPrefix(text);
  }

  
  private static final String removeVendorPrefix(String text) {
    if (text.length() > 0 && text.charAt(0) == '-') {
      Matcher m = VENDOR_PREFIXES.matcher(text);
      if (m.find()) {
        text = text.substring(m.group().length());
      }
    } 
    return text;
  }



  
  public List<Completion> getCompletionsAt(JTextComponent comp, Point p) {
    return null;
  }



  
  public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
    return null;
  }


  
  private LexerState getLexerState(RSyntaxTextArea textArea, int line) {
    int dot = textArea.getCaretPosition();
    LexerState state = LexerState.SELECTOR;
    boolean somethingFound = false;
    this.currentProperty = null;
    
    while (line >= 0 && !somethingFound) {
      Token t = textArea.getTokenListForLine(line--);
      while (t != null && t.isPaintable() && !t.containsPosition(dot)) {
        if (t.getType() == 6) {
          state = LexerState.PROPERTY;
          this.currentProperty = removeVendorPrefix(t.getLexeme());
          somethingFound = true;
        }
        else if (!this.isLess && t.getType() == 17) {
          
          state = LexerState.SELECTOR;
          this.currentProperty = null;
          somethingFound = true;
        }
        else if (t.getType() == 24 || t
          .getType() == 8 || t
          .getType() == 10) {
          state = LexerState.VALUE;
          somethingFound = true;
        }
        else if (t.isLeftCurly()) {
          state = LexerState.PROPERTY;
          somethingFound = true;
        }
        else if (t.isRightCurly()) {
          state = LexerState.SELECTOR;
          this.currentProperty = null;
          somethingFound = true;
        }
        else if (t.isSingleChar(23, ':')) {
          state = LexerState.VALUE;
          somethingFound = true;
        }
        else if (t.isSingleChar(23, ';')) {
          state = LexerState.PROPERTY;
          this.currentProperty = null;
          somethingFound = true;
        } 
        t = t.getNextToken();
      } 
    } 
    
    return state;
  }





  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    List<Completion> retVal = new ArrayList<>();
    String text = getAlreadyEnteredText(comp);
    
    if (text != null) {
      List<CompletionGenerator> generators;
      
      RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
      LexerState lexerState = getLexerState(textArea, textArea
          .getCaretLineNumber());
      
      List<Completion> choices = new ArrayList<>();
      switch (lexerState) {
        case SELECTOR:
          choices = this.htmlTagCompletions;
          break;
        case PROPERTY:
          choices = this.propertyCompletions;
          break;
        case VALUE:
          choices = this.valueCompletions.get(this.currentProperty);
          
          generators = this.valueCompletionGenerators.get(this.currentProperty);
          if (generators != null) {
            for (CompletionGenerator generator : generators) {
              
              List<Completion> toMerge = generator.generate((CompletionProvider)this, text);
              if (toMerge != null) {
                if (choices == null) {
                  choices = toMerge;

                  
                  continue;
                } 
                
                choices = new ArrayList<>(choices);
                choices.addAll(toMerge);
              } 
            } 
          }
          
          if (choices == null) {
            choices = new ArrayList<>();
          }
          Collections.sort(choices);
          break;
      } 
      
      if (this.isLess && 
        addLessCompletions(choices, lexerState, comp, text)) {
        Collections.sort(choices);
      }

      
      int index = Collections.binarySearch(choices, text, (Comparator<?>)this.comparator);
      if (index < 0) {
        index = -index - 1;

      
      }
      else {

        
        int pos = index - 1;
        while (pos > 0 && this.comparator
          .compare(choices.get(pos), text) == 0) {
          retVal.add(choices.get(pos));
          pos--;
        } 
      } 
      
      while (index < choices.size()) {
        Completion c = choices.get(index);
        if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
          retVal.add(c);
          index++;
        } 
      } 
    } 




    
    return retVal;
  }













  
  protected boolean addLessCompletions(List<Completion> completions, LexerState state, JTextComponent comp, String alreadyEntered) {
    return false;
  }






  
  public boolean isAutoActivateOkay(JTextComponent tc) {
    boolean ok = super.isAutoActivateOkay(tc);




    
    if (ok) {
      RSyntaxDocument doc = (RSyntaxDocument)tc.getDocument();
      int dot = tc.getCaretPosition();
      try {
        if (dot > 1 && doc.charAt(dot) == ' ') {
          ok = (doc.charAt(dot - 1) == ':');
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 
    
    return ok;
  }


  
  public boolean isValidChar(char ch) {
    switch (ch) {
      case '#':
      case '-':
      case '.':
      case '@':
      case '_':
        return true;
    } 
    return Character.isLetterOrDigit(ch);
  }



  
  private List<Completion> loadHtmlTagCompletions() throws IOException {
    List<Completion> completions = loadFromXML("data/html.xml");
    
    addAtRuleCompletions(completions);
    
    Collections.sort(completions);
    return completions;
  }


  
  private void loadPropertyCompletions() throws IOException {
    BufferedReader r;
    this.propertyCompletions = new ArrayList<>();

    
    ClassLoader cl = getClass().getClassLoader();
    InputStream in = cl.getResourceAsStream("data/css_properties.txt");
    if (in != null) {
      r = new BufferedReader(new InputStreamReader(in));
    } else {
      
      r = new BufferedReader(new FileReader("data/css_properties.txt"));
    } 
    
    String line;
    while ((line = r.readLine()) != null) {
      if (line.length() > 0 && line.charAt(0) != '#') {
        parsePropertyValueCompletionLine(line);
      }
    } 
    
    r.close();

    
    Collections.sort(this.propertyCompletions);
  }

















  
  private List<Completion> loadFromXML(InputStream in, ClassLoader cl) throws IOException {
    List<Completion> completions;
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    CompletionXMLParser handler = new CompletionXMLParser((CompletionProvider)this, cl);
    BufferedInputStream bin = new BufferedInputStream(in);
    try {
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(bin, (DefaultHandler)handler);
      completions = handler.getCompletions();
    }
    catch (SAXException|javax.xml.parsers.ParserConfigurationException e) {
      throw new IOException(e.toString());
    }
    finally {
      
      bin.close();
    } 
    
    return completions;
  }








  
  protected List<Completion> loadFromXML(String resource) throws IOException {
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
      return loadFromXML(bin, (ClassLoader)null);
    } 
  }






  
  private static final void add(Map<String, List<CompletionGenerator>> generatorMap, String prop, CompletionGenerator generator) {
    List<CompletionGenerator> generators = generatorMap.get(prop);
    if (generators == null) {
      generators = new ArrayList<>();
      generatorMap.put(prop, generators);
    } 
    generators.add(generator);
  }


  
  private void parsePropertyValueCompletionLine(String line) {
    String[] tokens = line.split("\\s+");
    String prop = tokens[0];
    String icon = (tokens.length > 1) ? tokens[1] : null;
    this.propertyCompletions.add(new PropertyCompletion((CompletionProvider)this, prop, icon));
    
    if (tokens.length > 2) {
      
      List<Completion> completions = new ArrayList<>();
      completions.add(this.INHERIT_COMPLETION);

      
      if (tokens[2].equals("[") && tokens[tokens.length - 1]
        .equals("]")) {
        for (int i = 3; i < tokens.length - 1; i++) {
          BasicCssCompletion basicCssCompletion; String token = tokens[i];
          Completion completion = null;
          if ("*length*".equals(token)) {
            add(this.valueCompletionGenerators, prop, new PercentageOrLengthCompletionGenerator(false));
          
          }
          else if ("*percentage-or-length*".equals(token)) {
            add(this.valueCompletionGenerators, prop, new PercentageOrLengthCompletionGenerator(true));
          
          }
          else if ("*color*".equals(token)) {
            add(this.valueCompletionGenerators, prop, new ColorCompletionGenerator((CompletionProvider)this));
          
          }
          else if ("*border-style*".equals(token)) {
            add(this.valueCompletionGenerators, prop, new BorderStyleCompletionGenerator());
          
          }
          else if ("*time*".equals(token)) {
            add(this.valueCompletionGenerators, prop, new TimeCompletionGenerator());
          
          }
          else if ("*common-fonts*".equals(token)) {
            add(this.valueCompletionGenerators, prop, new CommonFontCompletionGenerator());
          }
          else {
            
            basicCssCompletion = new BasicCssCompletion((CompletionProvider)this, tokens[i], "css_propertyvalue_identifier");
          } 
          
          if (basicCssCompletion != null) {
            completions.add(basicCssCompletion);
          }
        } 
      }
      
      this.valueCompletions.put(prop, completions);
    } 
  }





  
  protected enum LexerState
  {
    SELECTOR, PROPERTY, VALUE;
  }
}
