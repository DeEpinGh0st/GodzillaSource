package org.fife.rsta.ac.css;

import javax.swing.Icon;
import javax.swing.JList;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

























class CssCellRenderer
  extends CompletionCellRenderer
{
  private Icon tagIcon = getIcon("../html/tag.png");








  
  protected void prepareForFunctionCompletion(JList list, FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
    super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
    
    setIconWithDefault((Completion)fc);
  }






  
  protected void prepareForMarkupTagCompletion(JList list, MarkupTagCompletion c, int index, boolean selected, boolean hasFocus) {
    super.prepareForMarkupTagCompletion(list, c, index, selected, hasFocus);
    setIcon(this.tagIcon);
  }







  
  protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus) {
    super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
    setIconWithDefault(c);
  }







  
  protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
    super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
    
    setIcon(getEmptyIcon());
  }
}
