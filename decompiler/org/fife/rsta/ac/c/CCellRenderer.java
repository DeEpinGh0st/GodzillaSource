package org.fife.rsta.ac.c;

import javax.swing.Icon;
import javax.swing.JList;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


























class CCellRenderer
  extends CompletionCellRenderer
{
  private Icon variableIcon = getIcon("var.png");
  private Icon functionIcon = getIcon("function.png");







  
  protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus) {
    super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
    setIcon(getEmptyIcon());
  }







  
  protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
    super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
    
    setIcon(this.variableIcon);
  }







  
  protected void prepareForFunctionCompletion(JList list, FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
    super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
    
    setIcon(this.functionIcon);
  }
}
