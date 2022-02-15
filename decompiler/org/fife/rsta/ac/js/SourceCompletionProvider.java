package org.fife.rsta.ac.js;

import java.awt.Cursor;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import org.fife.rsta.ac.ShorthandCompletionCache;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.VariableResolver;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptType;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.fife.rsta.ac.js.completion.JSVariableCompletion;
import org.fife.rsta.ac.js.engine.JavaScriptEngine;
import org.fife.rsta.ac.js.engine.JavaScriptEngineFactory;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
























public class SourceCompletionProvider
  extends DefaultCompletionProvider
{
  private JavaScriptCompletionProvider parent;
  private JarManager jarManager;
  private int dot;
  private JavaScriptEngine engine;
  private JavaScriptTypesFactory javaScriptTypesFactory;
  private VariableResolver variableResolver;
  private PreProcessingScripts preProcessing;
  private ShorthandCompletionCache shorthandCache;
  private boolean xmlSupported;
  private String self;
  private TypeDeclarationOptions typeDeclarationOptions;
  private String lastCompletionsAtText;
  private List<Completion> lastParameterizedCompletionsAt;
  
  public SourceCompletionProvider(boolean xmlSupported) {
    this((String)null, xmlSupported);
  }



































  
  public SourceCompletionProvider(String javaScriptEngine, boolean xmlSupported) {
    this.lastCompletionsAtText = null;
    this.lastParameterizedCompletionsAt = null;
    this.variableResolver = new VariableResolver();
    this.xmlSupported = xmlSupported;
    setParameterizedCompletionParams('(', ", ", ')');
    setAutoActivationRules(false, ".");
    this.engine = JavaScriptEngineFactory.Instance().getEngineFromCache(javaScriptEngine);
    this.javaScriptTypesFactory = this.engine.getJavaScriptTypesFactory(this);
    setSelf("JSGlobal"); } public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
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
      while (startOffs >= s.offset && Character.isLetterOrDigit(s.array[startOffs])) {
        startOffs--;
      }

      
      int endOffs = s.offset + offset - start;
      while (endOffs < s.offset + s.count && Character.isLetterOrDigit(s.array[endOffs])) {
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

      
      AstRoot ast = this.parent.getASTRoot();
      Set<Completion> set = new HashSet<>();
      CodeBlock block = iterateAstRoot(ast, set, text, tc.getCaretPosition(), this.typeDeclarationOptions);
      recursivelyAddLocalVars(set, block, this.dot, (String)null, false, false);
      this.lastCompletionsAtText = text;
      return this.lastParameterizedCompletionsAt = new ArrayList<>(set);
    }
    catch (BadLocationException ble) {
      ble.printStackTrace();

      
      this.lastCompletionsAtText = null;
      return this.lastParameterizedCompletionsAt = null;
    } 
  }
  private void addShorthandCompletions(Set<Completion> set) {
    if (this.shorthandCache != null)
      set.addAll(this.shorthandCache.getShorthandCompletions()); 
  }
  public void setShorthandCache(ShorthandCompletionCache shorthandCache) {
    this.shorthandCache = shorthandCache;
  }
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    comp.setCursor(Cursor.getPredefinedCursor(3));

    
    try {
      this.variableResolver.resetLocalVariables();
      
      this.completions.clear();
      
      this.dot = comp.getCaretPosition();
      
      AstRoot astRoot = this.parent.getASTRoot();
      
      if (astRoot == null) {
        return this.completions;
      }
      
      Set<Completion> set = new TreeSet<>();


      
      String text = getAlreadyEnteredText(comp);
      
      if (supportsPreProcessingScripts()) {
        this.variableResolver.resetPreProcessingVariables(false);
      }
      
      if (text == null) {
        return this.completions;
      }



      
      boolean noDotInText = (text.indexOf('.') == -1);

      
      CodeBlock block = iterateAstRoot(astRoot, set, text, this.dot, this.typeDeclarationOptions);
      
      boolean isNew = false;
      if (noDotInText) {



        
        if (text.length() > 0) {
          addShorthandCompletions(set);
        }

        
        if (text.length() > 0) {
          
          JavaScriptHelper.ParseText pt = JavaScriptHelper.parseEnteredText(text);
          text = pt.text;
          isNew = pt.isNew;
          
          if (isNew) {
            return handleNewFilter(set, text);
          }


          
          loadECMAClasses(set, "");
        } 



        
        parseTextAndResolve(set, "this." + text);
        recursivelyAddLocalVars(set, block, this.dot, (String)null, false, false);
      } else {
        
        parseTextAndResolve(set, text);
      } 

      
      if (noDotInText && supportsPreProcessingScripts() && !isNew) {
        set.addAll(this.preProcessing.getCompletions());
      }
      
      return resolveCompletions(text, set);
    } finally {
      
      comp.setCursor(Cursor.getPredefinedCursor(2));
    } 
  }


  
  private List<Completion> handleNewFilter(Set<Completion> set, String text) {
    set.clear();
    
    loadECMAClasses(set, text);
    return resolveCompletions(text, set);
  }


  
  private List<Completion> resolveCompletions(String text, Set<Completion> set) {
    this.completions.addAll(set);

    
    this.completions.sort((Comparator)this.comparator);



    
    text = text.substring(text.lastIndexOf('.') + 1);
    
    int start = Collections.binarySearch(this.completions, text, (Comparator<? super String>)this.comparator);
    if (start < 0) {
      start = -(start + 1);
    }
    else {
      
      while (start > 0 && this.comparator
        .compare(this.completions.get(start - 1), text) == 0) {
        start--;
      }
    } 
    
    int end = Collections.binarySearch(this.completions, text + '{', (Comparator<? super String>)this.comparator);
    
    end = -(end + 1);
    
    return this.completions.subList(start, end);
  }








  
  private void loadECMAClasses(Set<Completion> set, String text) {
    List<JavaScriptType> list = this.engine.getJavaScriptTypesFactory(this).getECMAObjectTypes(this);
    
    for (JavaScriptType type : list) {
      
      if (text.length() == 0) {
        if (type.getClassTypeCompletion() != null)
          set.add(type.getClassTypeCompletion()); 
        continue;
      } 
      if (type.getType().getJSName().startsWith(text)) {
        for (JSCompletion jsc : type.getConstructorCompletions().values()) {
          set.add(jsc);
        }
      }
    } 
  }







  
  public String getSelf() {
    return this.self;
  }





  
  private void parseTextAndResolve(Set<Completion> set, String text) {
    JavaScriptResolver compiler = this.engine.getJavaScriptResolver(this);
    try {
      JavaScriptType type = compiler.compileText(text);
      boolean resolved = populateCompletionsFromType(type, set);
      if (!resolved) {
        type = compiler.compileText("this." + text);
        populateCompletionsFromType(type, set);
      } 
    } catch (IOException io) {
      
      io.printStackTrace();
    } 
  }





  
  private boolean populateCompletionsFromType(JavaScriptType type, Set<Completion> set) {
    if (type != null) {
      this.javaScriptTypesFactory.populateCompletionsForType(type, set);
      return true;
    } 
    return false;
  }

  
  public String getAlreadyEnteredText(JTextComponent comp) {
    String text = super.getAlreadyEnteredText(comp);
    if (text != null) {
      int charIndex = JavaScriptHelper.findIndexOfFirstOpeningBracket(text);
      text = text.substring(charIndex, text.length());
      int sqIndex = JavaScriptHelper.findIndexOfFirstOpeningSquareBracket(text);
      text = text.substring(sqIndex).trim();
      if (charIndex > 0 || sqIndex > 0) {
        text = JavaScriptHelper.trimFromLastParam(text);
        Logger.log("SourceCompletionProvider:getAlreadyEnteredText()::afterTrim " + text);
      } 
    } 
    
    return text;
  }














  
  protected CodeBlock iterateAstRoot(AstRoot root, Set<Completion> set, String entered, int dot, TypeDeclarationOptions options) {
    JavaScriptParser parser = this.engine.getParser(this, dot, options);
    return parser.convertAstNodeToCodeBlock(root, set, entered);
  }







  
  public TypeDeclaration resolveTypeDeclation(String name) {
    return this.variableResolver.resolveType(name, this.dot);
  }







  
  public JavaScriptVariableDeclaration findDeclaration(String name) {
    return this.variableResolver.findDeclaration(name, this.dot);
  }






  
  public JavaScriptVariableDeclaration findNonLocalDeclaration(String name) {
    return this.variableResolver.findNonLocalDeclaration(name, this.dot);
  }







  
  public TypeDeclaration resolveTypeFromFunctionNode(AstNode functionNode) {
    String functionText = functionNode.toSource();

    
    return resolveTypeDeclation(functionText);
  }

  
  void setParent(JavaScriptCompletionProvider parent) {
    this.parent = parent;
  }

  
  public void setJavaScriptTypesFactory(JavaScriptTypesFactory factory) {
    this.javaScriptTypesFactory = factory;
  }

  
  public JavaScriptTypesFactory getJavaScriptTypesFactory() {
    return this.javaScriptTypesFactory;
  }












  
  protected void recursivelyAddLocalVars(Set<Completion> completions, CodeBlock block, int dot, String text, boolean findMatch, boolean isPreprocessing) {
    if (!block.contains(dot)) {
      return;
    }
    
    int i;
    for (i = 0; i < block.getVariableDeclarationCount(); ) {
      JavaScriptVariableDeclaration dec = block.getVariableDeclaration(i);
      int decOffs = dec.getOffset();
      if (dot <= decOffs) {
        
        if (!findMatch || dec.getName().equals(text)) {
          JSVariableCompletion completion = new JSVariableCompletion((CompletionProvider)this, dec, !isPreprocessing);


          
          if (completions.contains(completion)) {
            completions.remove(completion);
          }
          completions.add(completion);
        } 

        
        i++;
      } 
    } 
    
    for (i = 0; i < block.getChildCodeBlockCount(); i++) {
      CodeBlock child = block.getChildCodeBlock(i);
      if (child.contains(dot)) {
        recursivelyAddLocalVars(completions, child, dot, text, findMatch, isPreprocessing);
      }
    } 
  }



  
  protected boolean isValidChar(char ch) {
    return (Character.isJavaIdentifierPart(ch) || ch == ',' || ch == '.' || ch == 
      getParameterListStart() || ch == getParameterListEnd() || ch == ' ' || ch == '"' || ch == '[' || ch == ']');
  }







  
  public void setJarManager(JarManager jarManager) {
    this.jarManager = jarManager;
  }

  
  public JarManager getJarManager() {
    return this.jarManager;
  }

  
  public VariableResolver getVariableResolver() {
    return this.variableResolver;
  }

  
  public JavaScriptLanguageSupport getLanguageSupport() {
    return this.parent.getLanguageSupport();
  }

  
  public void setPreProcessingScripts(PreProcessingScripts preProcessing) {
    this.preProcessing = preProcessing;
  }

  
  public PreProcessingScripts getPreProcessingScripts() {
    return this.preProcessing;
  }

  
  private boolean supportsPreProcessingScripts() {
    return (this.preProcessing != null);
  }

  
  public JavaScriptEngine getJavaScriptEngine() {
    return this.engine;
  }

  
  public void setJavaScriptEngine(JavaScriptEngine engine) {
    this.engine = engine;
  }
  
  public SourceLocation getSourceLocForClass(String className) {
    return this.jarManager.getSourceLocForClass(className);
  }


  
  public boolean isXMLSupported() {
    return this.xmlSupported;
  }

  
  public void setXMLSupported(boolean xmlSupported) {
    this.xmlSupported = xmlSupported;
  }


  
  public void setSelf(String self) {
    this.self = self;
  }

  
  public void parseDocument(int dot) {
    AstRoot ast = this.parent.getASTRoot();
    Set<Completion> set = new HashSet<>();
    this.variableResolver.resetLocalVariables();
    iterateAstRoot(ast, set, "", dot, this.typeDeclarationOptions);
  }

  
  public TypeDeclarationFactory getTypesFactory() {
    return this.engine.getTypesFactory();
  }




  
  public void setTypeDeclationOptions(TypeDeclarationOptions typeDeclarationOptions) {
    this.typeDeclarationOptions = typeDeclarationOptions;
  }

  
  public void debugCodeBlock(CodeBlock block, int tab) {
    System.out.println();
    tab++;
    if (block != null) {
      for (int j = 0; j < tab; j++) {
        System.out.print("\t");
      }
      System.out.print("Start: " + block.getStartOffset() + " end:" + block
          .getEndOffset());
      for (int ii = 0; ii < block.getVariableDeclarationCount(); ii++) {
        
        JavaScriptVariableDeclaration vd = block.getVariableDeclaration(ii);
        System.out.print(" " + vd.getName() + " ");
      } 
      for (int i = 0; i < block.getChildCodeBlockCount(); i++)
        debugCodeBlock(block.getChildCodeBlock(i), tab); 
    } 
  }
}
