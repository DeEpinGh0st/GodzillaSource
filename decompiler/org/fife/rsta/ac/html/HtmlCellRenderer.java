package org.fife.rsta.ac.html;

import javax.swing.Icon;
import javax.swing.JList;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


























public class HtmlCellRenderer
  extends CompletionCellRenderer
{
  private Icon tagIcon = getIcon("tag.png");
  private Icon attrIcon = getIcon("attribute.png");








  
  protected void prepareForFunctionCompletion(JList list, FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
    super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
    
    setIcon(getEmptyIcon());
  }






  
  protected void prepareForMarkupTagCompletion(JList list, MarkupTagCompletion c, int index, boolean selected, boolean hasFocus) {
    super.prepareForMarkupTagCompletion(list, c, index, selected, hasFocus);
    setIcon(this.tagIcon);
  }







  
  protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus) {
    super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
    if (c instanceof AttributeCompletion) {
      setIcon(this.attrIcon);
    } else {
      
      setIcon(getEmptyIcon());
    } 
  }







  
  protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
    super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
    
    setIcon(getEmptyIcon());
  }
}
