package org.fife.rsta.ac.groovy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.common.CodeBlock;
import org.fife.rsta.ac.common.TokenScanner;
import org.fife.rsta.ac.common.VariableDeclaration;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;


















public class GroovySourceCompletionProvider
  extends DefaultCompletionProvider
{
  private static final char[] KEYWORD_DEF = new char[] { 'd', 'e', 'f' };




  
  public GroovySourceCompletionProvider() {
    this((JarManager)null);
  }






  
  public GroovySourceCompletionProvider(JarManager jarManager) {
    if (jarManager == null) {
      jarManager = new JarManager();
    }
    
    setParameterizedCompletionParams('(', ", ", ')');
    setAutoActivationRules(false, ".");
  }



  
  private CodeBlock createAst(JTextComponent comp) {
    CodeBlock ast = new CodeBlock(0);
    
    RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
    TokenScanner scanner = new TokenScanner(textArea);
    parseCodeBlock(scanner, ast);
    
    return ast;
  }








  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    this.completions.clear();
    
    CodeBlock ast = createAst(comp);
    
    int dot = comp.getCaretPosition();
    recursivelyAddLocalVars(this.completions, ast, dot);
    
    Collections.sort(this.completions);

    
    String text = getAlreadyEnteredText(comp);
    
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






  
  protected boolean isValidChar(char ch) {
    return (Character.isJavaIdentifierPart(ch) || ch == '.');
  }


  
  private void parseCodeBlock(TokenScanner scanner, CodeBlock block) {
    Token t = scanner.next();
    while (t != null) {
      if (t.isRightCurly()) {
        block.setEndOffset(t.getOffset()); return;
      } 
      if (t.isLeftCurly()) {
        CodeBlock child = block.addChildCodeBlock(t.getOffset());
        parseCodeBlock(scanner, child);
      } else if (t.is(6, KEYWORD_DEF)) {
        t = scanner.next();
        if (t != null) {
          
          VariableDeclaration varDec = new VariableDeclaration(t.getLexeme(), t.getOffset());
          block.addVariable(varDec);
        } 
      } 
      t = scanner.next();
    } 
  }




  
  private void recursivelyAddLocalVars(List<Completion> completions, CodeBlock block, int dot) {
    if (!block.contains(dot)) {
      return;
    }
    
    int i;
    for (i = 0; i < block.getVariableDeclarationCount(); ) {
      VariableDeclaration dec = block.getVariableDeclaration(i);
      int decOffs = dec.getOffset();
      if (decOffs < dot) {
        BasicCompletion c = new BasicCompletion((CompletionProvider)this, dec.getName());
        completions.add(c);

        
        i++;
      } 
    } 

    
    for (i = 0; i < block.getChildCodeBlockCount(); i++) {
      CodeBlock child = block.getChildCodeBlock(i);
      if (child.contains(dot)) {
        recursivelyAddLocalVars(completions, child, dot);
        return;
      } 
    } 
  }
}
