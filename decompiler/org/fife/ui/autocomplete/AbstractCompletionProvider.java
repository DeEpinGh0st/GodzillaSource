package org.fife.ui.autocomplete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.JTextComponent;
































public abstract class AbstractCompletionProvider
  extends CompletionProviderBase
{
  protected List<Completion> completions;
  protected CaseInsensitiveComparator comparator;
  
  public AbstractCompletionProvider() {
    this.comparator = new CaseInsensitiveComparator();
    clearParameterizedCompletionParams();
    this.completions = new ArrayList<>();
  }













  
  public void addCompletion(Completion c) {
    checkProviderAndAdd(c);
    Collections.sort(this.completions);
  }













  
  public void addCompletions(List<Completion> completions) {
    for (Completion c : completions) {
      checkProviderAndAdd(c);
    }
    Collections.sort(this.completions);
  }







  
  protected void addWordCompletions(String[] words) {
    int count = (words == null) ? 0 : words.length;
    for (int i = 0; i < count; i++) {
      this.completions.add(new BasicCompletion(this, words[i]));
    }
    Collections.sort(this.completions);
  }

  
  protected void checkProviderAndAdd(Completion c) {
    if (c.getProvider() != this) {
      throw new IllegalArgumentException("Invalid CompletionProvider");
    }
    this.completions.add(c);
  }









  
  public void clear() {
    this.completions.clear();
  }












  
  public List<Completion> getCompletionByInputText(String inputText) {
    int end = Collections.binarySearch(this.completions, inputText, this.comparator);
    if (end < 0) {
      return null;
    }

    
    int start = end;
    while (start > 0 && this.comparator
      .compare(this.completions.get(start - 1), inputText) == 0) {
      start--;
    }
    int count = this.completions.size();
    while (++end < count && this.comparator
      .compare(this.completions.get(end), inputText) == 0);
    
    return this.completions.subList(start, end);
  }








  
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    List<Completion> retVal = new ArrayList<>();
    String text = getAlreadyEnteredText(comp);
    
    if (text != null) {
      
      int index = Collections.binarySearch(this.completions, text, this.comparator);
      if (index < 0) {
        index = -index - 1;

      
      }
      else {

        
        int pos = index - 1;
        while (pos > 0 && this.comparator
          .compare(this.completions.get(pos), text) == 0) {
          retVal.add(this.completions.get(pos));
          pos--;
        } 
      } 
      
      while (index < this.completions.size()) {
        Completion c = this.completions.get(index);
        if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
          retVal.add(c);
          index++;
        } 
      } 
    } 




    
    return retVal;
  }














  
  public boolean removeCompletion(Completion c) {
    int index = Collections.binarySearch((List)this.completions, c);
    if (index < 0) {
      return false;
    }
    this.completions.remove(index);
    return true;
  }








  
  public static class CaseInsensitiveComparator
    implements Comparator, Serializable
  {
    public int compare(Object o1, Object o2) {
      String s1 = (o1 instanceof String) ? (String)o1 : ((Completion)o1).getInputText();
      
      String s2 = (o2 instanceof String) ? (String)o2 : ((Completion)o2).getInputText();
      return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
    }
  }
}
