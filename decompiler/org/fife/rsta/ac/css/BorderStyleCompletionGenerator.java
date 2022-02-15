package org.fife.rsta.ac.css;

import java.util.ArrayList;
import java.util.List;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
























class BorderStyleCompletionGenerator
  implements CompletionGenerator
{
  private static final String ICON_KEY = "css_propertyvalue_identifier";
  
  public List<Completion> generate(CompletionProvider provider, String input) {
    List<Completion> completions = new ArrayList<>();
    
    completions.add(new BorderStyleCompletion(provider, "none"));
    completions.add(new BorderStyleCompletion(provider, "hidden"));
    completions.add(new BorderStyleCompletion(provider, "dotted"));
    completions.add(new BorderStyleCompletion(provider, "dashed"));
    completions.add(new BorderStyleCompletion(provider, "solid"));
    completions.add(new BorderStyleCompletion(provider, "double"));
    completions.add(new BorderStyleCompletion(provider, "groove"));
    completions.add(new BorderStyleCompletion(provider, "ridge"));
    completions.add(new BorderStyleCompletion(provider, "inset"));
    completions.add(new BorderStyleCompletion(provider, "outset"));
    
    return completions;
  }



  
  private static class BorderStyleCompletion
    extends BasicCssCompletion
  {
    public BorderStyleCompletion(CompletionProvider provider, String value) {
      super(provider, value, "css_propertyvalue_identifier");
    }
  }
}
