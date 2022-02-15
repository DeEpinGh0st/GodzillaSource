package org.fife.ui.autocomplete;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ListCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;






















































public abstract class CompletionProviderBase
  implements CompletionProvider
{
  private CompletionProvider parent;
  private ListCellRenderer<Object> listCellRenderer;
  private char paramListStart;
  private char paramListEnd;
  private String paramListSeparator;
  private boolean autoActivateAfterLetters;
  private String autoActivateChars;
  private ParameterChoicesProvider paramChoicesProvider;
  private Segment s = new Segment();


  
  protected static final String EMPTY_STRING = "";


  
  private static final Comparator<Completion> SORT_BY_RELEVANCE_COMPARATOR = new SortByRelevanceComparator();



  
  public void clearParameterizedCompletionParams() {
    this.paramListEnd = this.paramListStart = Character.MIN_VALUE;
    this.paramListSeparator = null;
  }



  
  public List<Completion> getCompletions(JTextComponent comp) {
    List<Completion> completions = getCompletionsImpl(comp);
    if (this.parent != null) {
      List<Completion> parentCompletions = this.parent.getCompletions(comp);
      if (parentCompletions != null) {
        completions.addAll(parentCompletions);
        Collections.sort(completions);
      } 
    } 




    
    completions.sort(SORT_BY_RELEVANCE_COMPARATOR);

    
    return completions;
  }






  
  protected abstract List<Completion> getCompletionsImpl(JTextComponent paramJTextComponent);





  
  public ListCellRenderer<Object> getListCellRenderer() {
    return this.listCellRenderer;
  }


  
  public ParameterChoicesProvider getParameterChoicesProvider() {
    return this.paramChoicesProvider;
  }


  
  public char getParameterListEnd() {
    return this.paramListEnd;
  }


  
  public String getParameterListSeparator() {
    return this.paramListSeparator;
  }


  
  public char getParameterListStart() {
    return this.paramListStart;
  }


  
  public CompletionProvider getParent() {
    return this.parent;
  }


  
  public boolean isAutoActivateOkay(JTextComponent tc) {
    Document doc = tc.getDocument();
    char ch = Character.MIN_VALUE;
    try {
      doc.getText(tc.getCaretPosition(), 1, this.s);
      ch = this.s.first();
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    return ((this.autoActivateAfterLetters && Character.isLetter(ch)) || (this.autoActivateChars != null && this.autoActivateChars
      .indexOf(ch) > -1));
  }










  
  public void setAutoActivationRules(boolean letters, String others) {
    this.autoActivateAfterLetters = letters;
    this.autoActivateChars = others;
  }











  
  public void setParameterChoicesProvider(ParameterChoicesProvider pcp) {
    this.paramChoicesProvider = pcp;
  }


  
  public void setListCellRenderer(ListCellRenderer<Object> r) {
    this.listCellRenderer = r;
  }



  
  public void setParameterizedCompletionParams(char listStart, String separator, char listEnd) {
    if (listStart < ' ' || listStart == '') {
      throw new IllegalArgumentException("Invalid listStart");
    }
    if (listEnd < ' ' || listEnd == '') {
      throw new IllegalArgumentException("Invalid listEnd");
    }
    if (separator == null || separator.length() == 0) {
      throw new IllegalArgumentException("Invalid separator");
    }
    this.paramListStart = listStart;
    this.paramListSeparator = separator;
    this.paramListEnd = listEnd;
  }


  
  public void setParent(CompletionProvider parent) {
    this.parent = parent;
  }
}
