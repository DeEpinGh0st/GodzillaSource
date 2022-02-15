package org.fife.rsta.ac.perl;

import org.fife.ui.autocomplete.DefaultCompletionProvider;


















class PerlCodeCompletionProvider
  extends DefaultCompletionProvider
{
  private PerlCompletionProvider parent;
  
  public PerlCodeCompletionProvider(PerlCompletionProvider parent) {
    this.parent = parent;
  }


  
  public char getParameterListEnd() {
    return this.parent.getParameterListEnd();
  }


  
  public char getParameterListStart() {
    return this.parent.getParameterListStart();
  }





  
  public boolean isValidChar(char ch) {
    return (super.isValidChar(ch) || ch == '@' || ch == '$' || ch == '%');
  }
}
