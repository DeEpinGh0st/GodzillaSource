package org.fife.rsta.ac.js;

import javax.swing.text.BadLocationException;
import org.fife.rsta.ac.js.ast.JavaScriptDeclaration;
import org.fife.rsta.ac.js.ast.JavaScriptFunctionDeclaration;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.VariableResolver;
import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SelectRegionLinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;

public class JavaScriptLinkGenerator
  implements LinkGenerator
{
  private JavaScriptLanguageSupport language;
  private boolean findLocal;
  private boolean findPreprocessed;
  private boolean findSystem;
  
  public JavaScriptLinkGenerator(JavaScriptLanguageSupport language) {
    this.language = language;
    this.findLocal = true;
  }





  
  public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
    JavaScriptDeclaration dec = null;
    IsLinkableCheckResult result = checkForLinkableToken(textArea, offs);
    if (result != null) {
      JavaScriptFunctionDeclaration javaScriptFunctionDeclaration;
      Token t = result.token;
      boolean function = result.function;
      String name = t.getLexeme();
      if (name != null && name.length() > 0)
      {
        
        if (name.length() > 1 || (name.length() == 1 && Character.isJavaIdentifierPart(name.charAt(0))))
        {
          this.language.reparseDocument(offs);
        }
      }
      JavaScriptParser parser = this.language.getJavaScriptParser();
      VariableResolver variableResolver = parser.getVariablesAndFunctions();
      
      if (variableResolver != null)
      {
        if (!function) {
          JavaScriptVariableDeclaration javaScriptVariableDeclaration = variableResolver.findDeclaration(name, offs, this.findLocal, this.findPreprocessed, this.findSystem);
        } else {
          
          String lookup = getLookupNameForFunction(textArea, offs, name);
          
          javaScriptFunctionDeclaration = variableResolver.findFunctionDeclaration(lookup, this.findLocal, this.findPreprocessed);
          if (javaScriptFunctionDeclaration == null) {
            javaScriptFunctionDeclaration = variableResolver.findFunctionDeclarationByFunctionName(name, this.findLocal, this.findPreprocessed);
          }
        } 
      }

      
      if (javaScriptFunctionDeclaration != null) {
        return createSelectedRegionResult(textArea, t, (JavaScriptDeclaration)javaScriptFunctionDeclaration);
      }
    } 
    return null;
  }



  
  protected LinkGeneratorResult createSelectedRegionResult(RSyntaxTextArea textArea, Token t, JavaScriptDeclaration dec) {
    if (dec.getTypeDeclarationOptions() != null && !dec.getTypeDeclarationOptions().isSupportsLinks()) {
      return null;
    }
    return (LinkGeneratorResult)new SelectRegionLinkGeneratorResult(textArea, t.getOffset(), dec.getStartOffSet(), dec.getEndOffset());
  }



  
  public void setFindLocal(boolean find) {
    this.findLocal = find;
  }



  
  public void setFindPreprocessed(boolean find) {
    this.findPreprocessed = find;
  }



  
  public void setFindSystem(boolean find) {
    this.findSystem = find;
  }













  
  private String getLookupNameForFunction(RSyntaxTextArea textArea, int offs, String name) {
    StringBuilder temp = new StringBuilder();
    if (offs >= 0) {
      
      try {
        int line = textArea.getLineOfOffset(offs);
        
        Token first = wrapToken(textArea.getTokenListForLine(line));
        for (Token t = first; t != null && t.isPaintable(); t = wrapToken(t.getNextToken())) {
          if (t.containsPosition(offs)) {
            for (Token tt = t; tt != null && tt.isPaintable(); tt = wrapToken(tt.getNextToken())) {
              temp.append(tt.getLexeme());
              if (tt.isSingleChar(22, ')')) {
                break;
              }
            } 
          }
        } 
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }

    
    String function = temp.toString().replaceAll("\\s", "");
    boolean params = false;
    int count = 0;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < function.length(); i++) {
      char ch = function.charAt(i);
      
      if (ch == '(') {
        params = true;
        count = 0;
        sb.append(ch);
      } else {
        
        if (ch == ')') {
          sb.append(ch);
          
          break;
        } 
        if (ch == ',') {
          count = 0;
          sb.append(ch);

        
        }
        else if (params && count == 0) {
          sb.append('p');
          count++;
        
        }
        else if (!params) {
          sb.append(ch);
        } 
      } 
    } 

    
    return sb.toString();
  }












  
  private IsLinkableCheckResult checkForLinkableToken(RSyntaxTextArea textArea, int offs) {
    IsLinkableCheckResult result = null;
    
    if (offs >= 0) {
      
      try {
        
        int line = textArea.getLineOfOffset(offs);
        Token first = wrapToken(textArea.getTokenListForLine(line));
        Token prev = null;
        
        for (Token t = first; t != null && t.isPaintable(); t = wrapToken(t
            .getNextToken())) {
          if (t.containsPosition(offs)) {


            
            Token token = wrapToken(t);
            
            boolean isFunction = false;
            
            if (prev != null && prev.isSingleChar('.')) {
              break;
            }

            
            Token next = wrapToken(
                RSyntaxUtilities.getNextImportantToken(t.getNextToken(), textArea, line));
            
            if (next != null && next
              .isSingleChar(22, '(')) {
              isFunction = true;
            }
            
            result = new IsLinkableCheckResult(token, isFunction);
            
            break;
          } 
          
          if (!t.isCommentOrWhitespace()) {
            prev = t;
          }
        }
      
      }
      catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }

    
    return result;
  }









  
  private Token wrapToken(Token token) {
    if (token != null)
      return (Token)new TokenImpl(token); 
    return token;
  }



  
  public JavaScriptLanguageSupport getLanguage() {
    return this.language;
  }





  
  private static class IsLinkableCheckResult
  {
    private Token token;




    
    private boolean function;




    
    private IsLinkableCheckResult(Token token, boolean function) {
      this.token = token;
      this.function = function;
    }
  }
}
