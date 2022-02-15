package org.fife.rsta.ac.js;

import javax.swing.JList;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.TemplateCompletion;
import org.fife.ui.autocomplete.VariableCompletion;































public class JavaScriptCellRenderer
  extends CompletionCellRenderer
{
  protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus) {
    super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
    setIconWithDefault(c);
  }






  
  protected void prepareForTemplateCompletion(JList list, TemplateCompletion tc, int index, boolean selected, boolean hasFocus) {
    super.prepareForTemplateCompletion(list, tc, index, selected, hasFocus);
    setIconWithDefault((Completion)tc, IconFactory.getIcon("template"));
  }






  
  protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
    super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
    setIconWithDefault((Completion)vc, IconFactory.getIcon("local_variable"));
  }






  
  protected void prepareForFunctionCompletion(JList list, FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
    super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
    setIconWithDefault((Completion)fc, IconFactory.getIcon("default_function"));
  }
}
