package org.fife.rsta.ac.js;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.fife.io.DocumentReader;
import org.fife.rsta.ac.js.ast.VariableResolver;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;











































public class JavaScriptParser
  extends AbstractParser
{
  public static final String PROPERTY_AST = "AST";
  private RSyntaxTextArea textArea;
  private AstRoot astRoot;
  private JavaScriptLanguageSupport langSupport;
  private PropertyChangeSupport support;
  private DefaultParseResult result;
  private VariableResolver variableResolver;
  
  public JavaScriptParser(JavaScriptLanguageSupport langSupport, RSyntaxTextArea textArea) {
    this.textArea = textArea;
    this.langSupport = langSupport;
    this.support = new PropertyChangeSupport(this);
    this.result = new DefaultParseResult((Parser)this);
  }









  
  public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
    this.support.addPropertyChangeListener(prop, l);
  }








  
  public static CompilerEnvirons createCompilerEnvironment(ErrorReporter errorHandler, JavaScriptLanguageSupport langSupport) {
    CompilerEnvirons env = new CompilerEnvirons();
    env.setErrorReporter(errorHandler);
    env.setIdeMode(true);
    env.setRecordingComments(true);
    env.setRecordingLocalJsDocComments(true);
    env.setRecoverFromErrors(true);
    if (langSupport != null) {
      env.setXmlAvailable(langSupport.isXmlAvailable());
      env.setStrictMode(langSupport.isStrictMode());
      int version = langSupport.getLanguageVersion();
      if (version > 0) {
        Logger.log("[JavaScriptParser]: JS language version set to: " + version);
        env.setLanguageVersion(version);
      } 
    } 
    return env;
  }









  
  private void gatherParserErrorsJsHint(RSyntaxDocument doc) {
    try {
      JsHinter.parse(this, this.textArea, this.result);
    } catch (IOException ioe) {
      
      String msg = "Error launching jshint: " + ioe.getMessage();
      this.result.addNotice((ParserNotice)new DefaultParserNotice((Parser)this, msg, 0));
      ioe.printStackTrace();
    } 
  }











  
  private void gatherParserErrorsRhino(ErrorCollector errorHandler, Element root) {
    List<ParseProblem> errors = errorHandler.getErrors();
    if (errors != null && errors.size() > 0)
    {
      for (ParseProblem problem : errors) {
        
        int offs = problem.getFileOffset();
        int len = problem.getLength();
        int line = root.getElementIndex(offs);
        String desc = problem.getMessage();
        DefaultParserNotice notice = new DefaultParserNotice((Parser)this, desc, line, offs, len);
        
        if (problem.getType() == ParseProblem.Type.Warning) {
          notice.setLevel(ParserNotice.Level.WARNING);
        }
        this.result.addNotice((ParserNotice)notice);
      } 
    }
  }










  
  public AstRoot getAstRoot() {
    return this.astRoot;
  }

  
  public int getJsHintIndent() {
    return this.langSupport.getJsHintIndent();
  }












  
  public File getJsHintRCFile(RSyntaxTextArea textArea) {
    if (textArea instanceof TextEditorPane) {
      TextEditorPane tep = (TextEditorPane)textArea;
      File file = new File(tep.getFileFullPath());
      File parent = file.getParentFile();
      while (parent != null) {
        File possibleJsHintRc = new File(parent, ".jshintrc");
        if (possibleJsHintRc.isFile()) {
          return possibleJsHintRc;
        }
        parent = parent.getParentFile();
      } 
    } 

    
    return this.langSupport.getDefaultJsHintRCFile();
  }







  
  public ParseResult parse(RSyntaxDocument doc, String style) {
    this.astRoot = null;
    this.result.clearNotices();
    
    Element root = doc.getDefaultRootElement();
    int lineCount = root.getElementCount();
    this.result.setParsedLines(0, lineCount - 1);
    
    DocumentReader r = new DocumentReader((Document)doc);
    ErrorCollector errorHandler = new ErrorCollector();
    CompilerEnvirons env = createCompilerEnvironment((ErrorReporter)errorHandler, this.langSupport);
    long start = System.currentTimeMillis();
    try {
      Parser parser = new Parser(env);
      this.astRoot = parser.parse((Reader)r, null, 0);
      long time = System.currentTimeMillis() - start;
      this.result.setParseTime(time);
    } catch (IOException ioe) {
      this.result.setError(ioe);
      ioe.printStackTrace();
    } catch (RhinoException re) {
      
      int line = re.lineNumber();
      
      Element elem = root.getElement(line);
      int offs = elem.getStartOffset();
      int len = elem.getEndOffset() - offs - 1;
      String msg = re.details();
      this.result.addNotice((ParserNotice)new DefaultParserNotice((Parser)this, msg, line, offs, len));
    }
    catch (Exception e) {
      this.result.setError(e);
    } 
    
    r.close();

    
    switch (this.langSupport.getErrorParser())
    
    { default:
        gatherParserErrorsRhino(errorHandler, root);






        
        this.support.firePropertyChange("AST", (Object)null, this.astRoot);
        
        return (ParseResult)this.result;case JSHINT: break; }  gatherParserErrorsJsHint(doc); this.support.firePropertyChange("AST", (Object)null, this.astRoot); return (ParseResult)this.result;
  }

  
  public void setVariablesAndFunctions(VariableResolver variableResolver) {
    this.variableResolver = variableResolver;
  }
  
  public VariableResolver getVariablesAndFunctions() {
    return this.variableResolver;
  }








  
  public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
    this.support.removePropertyChangeListener(prop, l);
  }





  
  public static class JSErrorReporter
    implements ErrorReporter
  {
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {}




    
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
      return null;
    }
    
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {}
  }
}
