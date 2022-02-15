package org.fife.rsta.ac.perl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.rsta.ac.common.CodeBlock;
import org.fife.rsta.ac.common.TokenScanner;
import org.fife.rsta.ac.common.VariableDeclaration;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;












































public class PerlCompletionProvider
  extends CCompletionProvider
{
  private boolean useParensWithFunctions;
  
  protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {}
  
  private CodeBlock createAst(RSyntaxTextArea textArea) {
    CodeBlock ast = new CodeBlock(0);
    TokenScanner scanner = new TokenScanner(textArea);
    parseCodeBlock(scanner, ast);
    return ast;
  }





  
  protected CompletionProvider createCodeCompletionProvider() {
    DefaultCompletionProvider cp = new PerlCodeCompletionProvider(this);
    loadCodeCompletionsFromXml(cp);
    addShorthandCompletions(cp);
    cp.setAutoActivationRules(true, null);
    return (CompletionProvider)cp;
  }






  
  protected CompletionProvider createStringCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.setAutoActivationRules(true, null);
    return (CompletionProvider)cp;
  }






  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    List<Completion> completions = super.getCompletionsImpl(comp);
    
    SortedSet<Completion> varCompletions = getVariableCompletions(comp);
    if (varCompletions != null) {
      completions.addAll(varCompletions);
      Collections.sort(completions);
    } 
    
    return completions;
  }











  
  public char getParameterListEnd() {
    return getUseParensWithFunctions() ? ')' : Character.MIN_VALUE;
  }










  
  public char getParameterListStart() {
    return getUseParensWithFunctions() ? '(' : ' ';
  }








  
  public boolean getUseParensWithFunctions() {
    return this.useParensWithFunctions;
  }











  
  private SortedSet<Completion> getVariableCompletions(JTextComponent comp) {
    RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
    int dot = textArea.getCaretPosition();
    SortedSet<Completion> varCompletions = new TreeSet<>(this.comparator);
    
    CompletionProvider p = getDefaultCompletionProvider();
    String text = p.getAlreadyEnteredText(comp);
    char firstChar = (text.length() == 0) ? Character.MIN_VALUE : text.charAt(0);
    if (firstChar != '$' && firstChar != '@' && firstChar != '%') {
      System.out.println("DEBUG: No use matching variables, exiting");
      return null;
    } 


    
    CodeBlock block = createAst(textArea);
    recursivelyAddLocalVars(varCompletions, block, dot, firstChar);

    
    if (varCompletions.size() > 0) {
      BasicCompletion basicCompletion1 = new BasicCompletion(p, text);
      BasicCompletion basicCompletion2 = new BasicCompletion(p, text + '{');
      varCompletions = (SortedSet)varCompletions.subSet(basicCompletion1, basicCompletion2);
    } 
    
    return varCompletions;
  }

  
  private CaseInsensitiveComparator comparator = new CaseInsensitiveComparator();

  
  private static class CaseInsensitiveComparator
    implements Comparator<Completion>, Serializable
  {
    private CaseInsensitiveComparator() {}

    
    public int compare(Completion c1, Completion c2) {
      String s1 = c1.getInputText();
      String s2 = c2.getInputText();
      return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
    }
  }







  
  protected String getXmlResource() {
    return "data/perl5.xml";
  }







  
  private void parseCodeBlock(TokenScanner scanner, CodeBlock block) {
    Token t = scanner.next();
    while (t != null) {
      if (t.isRightCurly()) {
        block.setEndOffset(t.getOffset());
        return;
      } 
      if (t.isLeftCurly()) {
        CodeBlock child = block.addChildCodeBlock(t.getOffset());
        parseCodeBlock(scanner, child);
      }
      else if (t.getType() == 17) {
        
        VariableDeclaration varDec = new VariableDeclaration(t.getLexeme(), t.getOffset());
        block.addVariable(varDec);
      } 
      t = scanner.next();
    } 
  }












  
  private void recursivelyAddLocalVars(SortedSet<Completion> completions, CodeBlock block, int dot, int firstChar) {
    if (!block.contains(dot)) {
      return;
    }
    
    int i;
    for (i = 0; i < block.getVariableDeclarationCount(); ) {
      VariableDeclaration dec = block.getVariableDeclaration(i);
      int decOffs = dec.getOffset();
      if (decOffs < dot) {
        String name = dec.getName();
        char ch = name.charAt(0);
        if (firstChar <= ch) {
          if (firstChar < ch) {
            name = firstChar + name.substring(1);
          }
          BasicCompletion c = new BasicCompletion((CompletionProvider)this, name);
          completions.add(c);
        } 

        
        i++;
      } 
    } 

    
    for (i = 0; i < block.getChildCodeBlockCount(); i++) {
      CodeBlock child = block.getChildCodeBlock(i);
      if (child.contains(dot)) {
        recursivelyAddLocalVars(completions, child, dot, firstChar);
        return;
      } 
    } 
  }








  
  public void setUseParensWithFunctions(boolean use) {
    this.useParensWithFunctions = use;
  }
}
